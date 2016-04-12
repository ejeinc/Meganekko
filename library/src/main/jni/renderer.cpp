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

#include <android/log.h>
#include "Renderer.h"

#include "Material.h"
#include "Scene.h"
#include "RenderData.h"
#include "OESShader.h"
#include "util/GL.h"

using namespace OVR;

namespace mgn {
class SceneObject;

void Renderer::RenderEyeView(Scene* scene, std::vector<SceneObject*> scene_objects, OESShader* oesShader,
        const Matrix4f &eyeViewMatrix, const Matrix4f &eyeProjectionMatrix, const Matrix4f &eyeViewProjection, const int eye) {
    // there is no need to flat and sort every frame.
    // however let's keep it as is and assume we are not changed
    // This is not right way to do data conversion. However since GVRF doesn't support
    // bone/weight/joint and other assimp data, we will put general model conversion
    // on hold and do this kind of conversion fist

    std::vector<RenderData*> render_data_vector;

    // do occlusion culling, if enabled
    occlusion_cull(scene, scene_objects);

    // do frustum culling, if enabled
    frustum_cull(scene, eyeViewMatrix.GetTranslation(), scene_objects, render_data_vector,
            eyeViewProjection, oesShader);

    // do sorting based on render order
    if (!scene->get_frustum_culling()) {
        std::sort(render_data_vector.begin(), render_data_vector.end(),
                compareRenderData);
    } else {
        std::sort(render_data_vector.begin(), render_data_vector.end(),
                compareRenderDataWithFrustumCulling);
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

    int renderMask = eye == 0 ? RenderData::RenderMaskBit::Left : RenderData::RenderMaskBit::Right;

    for (auto it = render_data_vector.begin();
            it != render_data_vector.end(); ++it) {
        renderRenderData(*it, eyeViewMatrix, eyeProjectionMatrix, renderMask, oesShader, eye);
    }

}

void Renderer::occlusion_cull(Scene* scene,
        std::vector<SceneObject*> scene_objects) {
    if (!scene->get_occlusion_culling()) {
        return;
    }

    for (auto it = scene_objects.begin(); it != scene_objects.end(); ++it) {
        RenderData* render_data = (*it)->render_data();
        if (render_data == 0) {
            continue;
        }

        if (render_data->pass(0)->material() == 0) {
            continue;
        }

        //If a query was issued on an earlier or same frame and if results are
        //available, then update the same. If results are unavailable, do nothing
        if (!(*it)->is_query_issued()) {
            continue;
        }

        GLuint query_result = GL_FALSE;
        GLuint *query = (*it)->get_occlusion_array();
        glGetQueryObjectuiv(query[0], GL_QUERY_RESULT_AVAILABLE, &query_result);

        if (query_result) {
            GLuint pixel_count;
            glGetQueryObjectuiv(query[0], GL_QUERY_RESULT, &pixel_count);
            bool visibility = ((pixel_count & GL_TRUE) == GL_TRUE);

            (*it)->set_visible(visibility);
            (*it)->set_query_issued(false);
        }
    }
}

void Renderer::frustum_cull(Scene* scene, const Vector3f& camera_position,
        std::vector<SceneObject*> scene_objects,
        std::vector<RenderData*>& render_data_vector, const Matrix4f &vp_matrix,
        OESShader * oesShader) {
    for (auto it = scene_objects.begin(); it != scene_objects.end(); ++it) {
        SceneObject *scene_object = (*it);
        RenderData* render_data = scene_object->render_data();
        if (render_data == 0 || render_data->pass(0)->material() == 0) {
            continue;
        }

        // Check for frustum culling flag
        if (!scene->get_frustum_culling()) {
            //No occlusion or frustum tests enabled
            render_data_vector.push_back(render_data);
            continue;
        }

        // Frustum culling setup
        Mesh* currentMesh = render_data->mesh();
        if (currentMesh == NULL) {
            continue;
        }

        const float* bounding_box_info = currentMesh->getBoundingBoxInfo();
        if (bounding_box_info == NULL) {
            continue;
        }

        Matrix4f model_matrix_tmp = render_data->owner_object()->transform()->getModelMatrix();
        Matrix4f mvp_matrix_tmp(vp_matrix * model_matrix_tmp);

        // Frustum
        float frustum[6][4];

        // Matrix to array
        float mvp_matrix_array[16] = { 0.0 };
        const float *mat_to_array = (const float*)mvp_matrix_tmp.Transposed().M[0]; // TODO this is originally glm::mat4 so transposed
        memcpy(mvp_matrix_array, mat_to_array, sizeof(float) * 16);

        // Build the frustum
        build_frustum(frustum, mvp_matrix_array);

        // Check for being inside or outside frustum
        bool is_inside = is_cube_in_frustum(frustum, bounding_box_info);

        // Only push those scene objects that are inside of the frustum
        if (!is_inside) {
            scene_object->set_in_frustum(false);
            continue;
        }

        // Transform the bounding sphere
        const float *sphere_info = currentMesh->getBoundingSphereInfo();
        Vector4f sphere_center(sphere_info[0], sphere_info[1], sphere_info[2],
                1.0f);
        Vector4f transformed_sphere_center = mvp_matrix_tmp.Transform(sphere_center);

        // Calculate distance from camera
        Vector4f position(camera_position, 1.0f);
        Vector4f difference = transformed_sphere_center - position;
        float distance = difference.Dot(difference);

        // this distance will be used when sorting transparent objects
        render_data->set_camera_distance(distance);

        // Check if this is the correct LOD level
        if (!scene_object->inLODRange(distance)) {
            // not in range, don't add it to the list
            continue;
        }

        scene_object->set_in_frustum();
        bool visible = scene_object->visible();

        //If visibility flag was set by an earlier occlusion query,
        //turn visibility on for the object
        if (visible) {
            render_data_vector.push_back(render_data);
        }

        if (render_data->pass(0)->material() == 0
                || !scene->get_occlusion_culling()) {
            continue;
        }
    }
}

void Renderer::build_frustum(float frustum[6][4], float mvp_matrix[16]) {
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

bool Renderer::is_cube_in_frustum(float frustum[6][4],
        const float *vertex_limit) {
    int p;
    float Xmin = vertex_limit[0];
    float Ymin = vertex_limit[1];
    float Zmin = vertex_limit[2];
    float Xmax = vertex_limit[3];
    float Ymax = vertex_limit[4];
    float Zmax = vertex_limit[5];

    for (p = 0; p < 6; p++) {
        if (frustum[p][0] * (Xmin) + frustum[p][1] * (Ymin)
                + frustum[p][2] * (Zmin) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (Xmax) + frustum[p][1] * (Ymin)
                + frustum[p][2] * (Zmin) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (Xmin) + frustum[p][1] * (Ymax)
                + frustum[p][2] * (Zmin) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (Xmax) + frustum[p][1] * (Ymax)
                + frustum[p][2] * (Zmin) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (Xmin) + frustum[p][1] * (Ymin)
                + frustum[p][2] * (Zmax) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (Xmax) + frustum[p][1] * (Ymin)
                + frustum[p][2] * (Zmax) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (Xmin) + frustum[p][1] * (Ymax)
                + frustum[p][2] * (Zmax) + frustum[p][3] > 0)
            continue;
        if (frustum[p][0] * (Xmax) + frustum[p][1] * (Ymax)
                + frustum[p][2] * (Zmax) + frustum[p][3] > 0)
            continue;
        return false;
    }
    return true;
}

void Renderer::renderRenderData(RenderData* render_data,
        const Matrix4f& view_matrix, const Matrix4f& projection_matrix,
        int render_mask, OESShader * oesShader, const int eye) {
    if (render_mask & render_data->render_mask()) {

        if (render_data->offset()) {
            glEnable (GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(render_data->offset_factor(),
                    render_data->offset_units());
        }
        if (!render_data->depth_test()) {
            glDisable (GL_DEPTH_TEST);
        }
        if (!render_data->alpha_blend()) {
            glDisable (GL_BLEND);
        }
        if (render_data->mesh() != 0) {
            for (int curr_pass = 0; curr_pass < render_data->pass_count();
                    ++curr_pass) {

                set_face_culling(render_data->pass(curr_pass)->cull_face());
                Material* curr_material = render_data->pass(curr_pass)->material();

                if (curr_material != nullptr) {
                    Matrix4f model_matrix = render_data->owner_object()->transform()->getModelMatrix();
                    Matrix4f mv_matrix(view_matrix * model_matrix);
                    Matrix4f mvp_matrix = projection_matrix * mv_matrix;
                    try {
                        oesShader->render(mvp_matrix, render_data, curr_material, eye);
                    } catch (std::string error) {
                        __android_log_print(ANDROID_LOG_ERROR, "mgn", "Error detected in Renderer::renderRenderData; error : %s", error.c_str());
                    }
                }
            }
        }

        // Restoring to Default.
        // TODO: There's a lot of redundant state changes. If on every render face culling is being set there's no need to
        // restore defaults. Possibly later we could add a OpenGL state wrapper to avoid redundant api calls.
        if (render_data->cull_face() != RenderData::CullBack) {
            glEnable (GL_CULL_FACE);
            glCullFace (GL_BACK);
        }

        if (render_data->offset()) {
            glDisable (GL_POLYGON_OFFSET_FILL);
        }
        if (!render_data->depth_test()) {
            glEnable (GL_DEPTH_TEST);
        }
        if (!render_data->alpha_blend()) {
            glEnable (GL_BLEND);
        }
    }
}

void Renderer::set_face_culling(int cull_face) {
    switch (cull_face) {
    case RenderData::CullFront:
        glEnable (GL_CULL_FACE);
        glCullFace (GL_FRONT);
        break;

    case RenderData::CullNone:
        glDisable(GL_CULL_FACE);
        break;

        // CullBack as Default
    default:
        glEnable(GL_CULL_FACE);
        glCullFace (GL_BACK);
        break;
    }
}

}
