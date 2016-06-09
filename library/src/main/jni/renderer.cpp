/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/***************************************************************************
 * Renders a scene, a screen.
 ***************************************************************************/

#include "includes.h"
#include "Renderer.h"

#include "Material.h"
#include "Scene.h"
#include "RenderData.h"

using namespace OVR;

namespace mgn {

void Renderer::RenderEyeView(const Scene* scene, const Array<SceneObject*> & scene_objects, OESShader* oesShader,
        const Matrix4f &eyeViewMatrix, const Matrix4f &eyeProjectionMatrix, const Matrix4f &eyeViewProjection, const int eye) {
    // there is no need to flat and sort every frame.
    // however let's keep it as is and assume we are not changed
    // This is not right way to do data conversion. However since GVRF doesn't support
    // bone/weight/joint and other assimp data, we will put general model conversion
    // on hold and do this kind of conversion fist

    Array<RenderData*> render_data_vector;

    // do occlusion culling, if enabled
    OcclusionCull(scene, scene_objects);

    // do frustum culling, if enabled
    FrustumCull(scene, eyeViewMatrix.GetTranslation(), scene_objects, render_data_vector,
            eyeViewProjection, oesShader);

    // do sorting based on render order
    if (!scene->GetFrustumCulling()) {
        Alg::QuickSort(render_data_vector, compareRenderData);
    } else {
        Alg::QuickSort(render_data_vector, compareRenderDataWithFrustumCulling);
    }

    glEnable (GL_DEPTH_TEST);
    glDepthFunc (GL_LEQUAL);
    glEnable (GL_CULL_FACE);
    glFrontFace (GL_CCW);
    glCullFace (GL_BACK);
    glEnable (GL_BLEND);
    glBlendEquation (GL_FUNC_ADD);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    glDisable (GL_POLYGON_OFFSET_FILL);

    // TODO background color as parameter
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    for (auto it = render_data_vector.Begin();
            it != render_data_vector.End(); ++it) {
        RenderRenderData(*it, eyeViewMatrix, eyeProjectionMatrix, oesShader, eye);
    }

}

void Renderer::OcclusionCull(const Scene * scene, const Array<SceneObject*> & scene_objects) {
    if (!scene->GetOcclusionCulling()) {
        return;
    }

    for (auto it = scene_objects.Begin(); it != scene_objects.End(); ++it) {
        RenderData* render_data = (*it)->GetRenderData();
        if (render_data == nullptr) {
            continue;
        }

        if (render_data->GetMaterial() == nullptr) {
            continue;
        }

        //If a query was issued on an earlier or same frame and if results are
        //available, then update the same. If results are unavailable, do nothing
        if (!(*it)->IsQueryIssued()) {
            continue;
        }

        GLuint query_result = GL_FALSE;
        GLuint *query = (*it)->GetOcclusionArray();
        glGetQueryObjectuiv(query[0], GL_QUERY_RESULT_AVAILABLE, &query_result);

        if (query_result) {
            GLuint pixel_count;
            glGetQueryObjectuiv(query[0], GL_QUERY_RESULT, &pixel_count);
            bool visibility = ((pixel_count & GL_TRUE) == GL_TRUE);

            (*it)->SetVisible(visibility);
            (*it)->SetQueryIssued(false);
        }
    }
}

void Renderer::FrustumCull(const Scene * scene, const Vector3f& camera_position,
        const Array<SceneObject*> & scene_objects,
        Array<RenderData*> & render_data_vector, const Matrix4f &vp_matrix,
        OESShader * oesShader) {
    for (auto it = scene_objects.Begin(); it != scene_objects.End(); ++it) {
        SceneObject *scene_object = (*it);
        RenderData* render_data = scene_object->GetRenderData();
        if (render_data == nullptr || render_data->GetMaterial() == nullptr) {
            continue;
        }

        // Check for frustum culling flag
        if (!scene->GetFrustumCulling()) {
            //No occlusion or frustum tests enabled
            render_data_vector.PushBack(render_data);
            continue;
        }

        // Frustum culling setup
        Mesh* currentMesh = render_data->GetMesh();
        if (currentMesh == nullptr) {
            continue;
        }

        BoundingBoxInfo bounding_box_info = currentMesh->GetBoundingBoxInfo();

        Matrix4f modelMatrixTmp = render_data->GetOwnerObject()->GetMatrixWorld();
        Matrix4f mvpMatrixTmp(vp_matrix * modelMatrixTmp);

        // Frustum
        float frustum[6][4];

        // Matrix to array
        float mvp_matrix_array[16] = { 0.0 };
        const float *mat_to_array = (const float*)mvpMatrixTmp.Transposed().M[0]; // TODO this is originally glm::mat4 so transposed
        memcpy(mvp_matrix_array, mat_to_array, sizeof(float) * 16);

        // Build the frustum
        BuildFrustum(frustum, mvp_matrix_array);

        // Check for being inside or outside frustum
        bool is_inside = IsCubeInFrustum(frustum, bounding_box_info);

        // Only push those scene objects that are inside of the frustum
        if (!is_inside) {
            scene_object->SetInFrustum(false);
            continue;
        }

        // Transform the bounding sphere
        const BoundingSphereInfo sphereInfo = currentMesh->GetBoundingSphereInfo();
        Vector4f sphere_center(sphereInfo.center, 1.0f);
        Vector4f transformed_sphere_center = mvpMatrixTmp.Transform(sphere_center);

        // Calculate distance from camera
        Vector4f position(camera_position, 1.0f);
        Vector4f difference = transformed_sphere_center - position;
        float distance = difference.Dot(difference);

        // this distance will be used when sorting transparent objects
        render_data->SetCameraDistance(distance);

        // Check if this is the correct LOD level
        if (!scene_object->InLODRange(distance)) {
            // not in range, don't add it to the list
            continue;
        }

        scene_object->SetInFrustum();
        bool visible = scene_object->IsVisible();

        //If visibility flag was set by an earlier occlusion query,
        //turn visibility on for the object
        if (visible) {
            render_data_vector.PushBack(render_data);
        }

        if (render_data->GetMaterial() == nullptr
                || !scene->GetOcclusionCulling()) {
            continue;
        }
    }
}

void Renderer::BuildFrustum(float frustum[6][4], float mvp_matrix[16]) {
    float t;

    /* Extract the numbers for the RIGHT plane */
    frustum[0][0] = mvp_matrix[3] - mvp_matrix[0];
    frustum[0][1] = mvp_matrix[7] - mvp_matrix[4];
    frustum[0][2] = mvp_matrix[11] - mvp_matrix[8];
    frustum[0][3] = mvp_matrix[15] - mvp_matrix[12];

    /* Normalize the result */
    t = sqrt(
            frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1]
                    + frustum[0][2] * frustum[0][2]);
    frustum[0][0] /= t;
    frustum[0][1] /= t;
    frustum[0][2] /= t;
    frustum[0][3] /= t;

    /* Extract the numbers for the LEFT plane */
    frustum[1][0] = mvp_matrix[3] + mvp_matrix[0];
    frustum[1][1] = mvp_matrix[7] + mvp_matrix[4];
    frustum[1][2] = mvp_matrix[11] + mvp_matrix[8];
    frustum[1][3] = mvp_matrix[15] + mvp_matrix[12];

    /* Normalize the result */
    t = sqrt(
            frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1]
                    + frustum[1][2] * frustum[1][2]);
    frustum[1][0] /= t;
    frustum[1][1] /= t;
    frustum[1][2] /= t;
    frustum[1][3] /= t;

    /* Extract the BOTTOM plane */
    frustum[2][0] = mvp_matrix[3] + mvp_matrix[1];
    frustum[2][1] = mvp_matrix[7] + mvp_matrix[5];
    frustum[2][2] = mvp_matrix[11] + mvp_matrix[9];
    frustum[2][3] = mvp_matrix[15] + mvp_matrix[13];

    /* Normalize the result */
    t = sqrt(
            frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1]
                    + frustum[2][2] * frustum[2][2]);
    frustum[2][0] /= t;
    frustum[2][1] /= t;
    frustum[2][2] /= t;
    frustum[2][3] /= t;

    /* Extract the TOP plane */
    frustum[3][0] = mvp_matrix[3] - mvp_matrix[1];
    frustum[3][1] = mvp_matrix[7] - mvp_matrix[5];
    frustum[3][2] = mvp_matrix[11] - mvp_matrix[9];
    frustum[3][3] = mvp_matrix[15] - mvp_matrix[13];

    /* Normalize the result */
    t = sqrt(
            frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1]
                    + frustum[3][2] * frustum[3][2]);
    frustum[3][0] /= t;
    frustum[3][1] /= t;
    frustum[3][2] /= t;
    frustum[3][3] /= t;

    /* Extract the FAR plane */
    frustum[4][0] = mvp_matrix[3] - mvp_matrix[2];
    frustum[4][1] = mvp_matrix[7] - mvp_matrix[6];
    frustum[4][2] = mvp_matrix[11] - mvp_matrix[10];
    frustum[4][3] = mvp_matrix[15] - mvp_matrix[14];

    /* Normalize the result */
    t = sqrt(
            frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1]
                    + frustum[4][2] * frustum[4][2]);
    frustum[4][0] /= t;
    frustum[4][1] /= t;
    frustum[4][2] /= t;
    frustum[4][3] /= t;

    /* Extract the NEAR plane */
    frustum[5][0] = mvp_matrix[3] + mvp_matrix[2];
    frustum[5][1] = mvp_matrix[7] + mvp_matrix[6];
    frustum[5][2] = mvp_matrix[11] + mvp_matrix[10];
    frustum[5][3] = mvp_matrix[15] + mvp_matrix[14];

    /* Normalize the result */
    t = sqrt(
            frustum[5][0] * frustum[5][0] + frustum[5][1] * frustum[5][1]
                    + frustum[5][2] * frustum[5][2]);
    frustum[5][0] /= t;
    frustum[5][1] /= t;
    frustum[5][2] /= t;
    frustum[5][3] /= t;
}

bool Renderer::IsCubeInFrustum(float frustum[6][4], const BoundingBoxInfo & vertex_limit) {
    int p;

    for (p = 0; p < 6; p++) {
        if (frustum[p][0] * (vertex_limit.mins.x) + frustum[p][1] * (vertex_limit.mins.y)
                + frustum[p][2] * (vertex_limit.mins.z) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (vertex_limit.maxs.x) + frustum[p][1] * (vertex_limit.mins.y)
                + frustum[p][2] * (vertex_limit.mins.z) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (vertex_limit.mins.x) + frustum[p][1] * (vertex_limit.maxs.y)
                + frustum[p][2] * (vertex_limit.mins.z) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (vertex_limit.maxs.x) + frustum[p][1] * (vertex_limit.maxs.y)
                + frustum[p][2] * (vertex_limit.mins.z) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (vertex_limit.mins.x) + frustum[p][1] * (vertex_limit.mins.y)
                + frustum[p][2] * (vertex_limit.maxs.z) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (vertex_limit.maxs.x) + frustum[p][1] * (vertex_limit.mins.y)
                + frustum[p][2] * (vertex_limit.maxs.z) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (vertex_limit.mins.x) + frustum[p][1] * (vertex_limit.maxs.y)
                + frustum[p][2] * (vertex_limit.maxs.z) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (vertex_limit.maxs.x) + frustum[p][1] * (vertex_limit.maxs.y)
                + frustum[p][2] * (vertex_limit.maxs.z) + frustum[p][3] > 0)
            continue;
        return false;
    }
    return true;
}

void Renderer::RenderRenderData(RenderData* renderData,
        const Matrix4f& view_matrix, const Matrix4f& projection_matrix,
        OESShader * oesShader, const int eye) {

    if (!renderData->IsVisible()) return;

    Mesh * mesh = renderData->GetMesh();
    if (mesh == nullptr) return;

    Material* material = renderData->GetMaterial();
    if (material == nullptr) return;

    if (renderData->GetOffset()) {
        glEnable (GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(renderData->GetOffsetFactor(), renderData->GetOffsetUnits());
    }

    if (!renderData->GetDepthTest()) {
        glDisable (GL_DEPTH_TEST);
    }

    if (!renderData->GetAlphaBlend()) {
        glDisable (GL_BLEND);
    }

    SetFaceCulling(material->GetSide());

    Matrix4f model_matrix = renderData->GetOwnerObject()->GetMatrixWorld();
    Matrix4f mv_matrix(view_matrix * model_matrix);
    Matrix4f mvp_matrix = projection_matrix * mv_matrix;
    try {
        oesShader->Render(mvp_matrix, mesh->GetGeometry(), material, eye);
    } catch (String error) {
        __android_log_print(ANDROID_LOG_ERROR, "mgn", "Error detected in Renderer::renderRenderData; error : %s", error.ToCStr());
    }

    // Restoring to Default.
    // TODO: There's a lot of redundant state changes. If on every render face culling is being set there's no need to
    // restore defaults. Possibly later we could add a OpenGL state wrapper to avoid redundant api calls.
    if (renderData->GetMaterial()->GetSide() != Material::FrontSide) {
        glEnable (GL_CULL_FACE);
        glCullFace (GL_BACK);
    }

    if (renderData->GetOffset()) {
        glDisable (GL_POLYGON_OFFSET_FILL);
    }

    if (!renderData->GetDepthTest()) {
        glEnable (GL_DEPTH_TEST);
    }

    if (!renderData->GetAlphaBlend()) {
        glEnable (GL_BLEND);
    }
}

void Renderer::SetFaceCulling(int cull_face) {
    switch (cull_face) {
    case Material::BackSide:
        glEnable (GL_CULL_FACE);
        glCullFace (GL_FRONT);
        break;

    case Material::DoubleSide:
        glDisable(GL_CULL_FACE);
        break;

        // FrontSide as Default
    default:
        glEnable(GL_CULL_FACE);
        glCullFace (GL_BACK);
        break;
    }
}

}
