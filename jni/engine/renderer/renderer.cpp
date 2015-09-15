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

#include "renderer.h"

#include "eglextension/tiledrendering/tiled_rendering_enhancer.h"
#include "objects/material.h"
#include "objects/scene.h"
#include "objects/scene_object.h"
#include "objects/components/eye_pointee_holder.h"
#include "objects/components/render_data.h"
#include "objects/textures/render_texture.h"
#include "shaders/shader_manager.h"
#include "util/gvr_gl.h"
#include "util/gvr_log.h"

namespace gvr {

void Renderer::RenderEyeView(Scene* scene, ShaderManager* shader_manager,
        const OVR::Matrix4f &eyeViewMatrix, const OVR::Matrix4f &eyeProjectionMatrix, const OVR::Matrix4f &eyeViewProjection, int eye) {
    // there is no need to flat and sort every frame.
    // however let's keep it as is and assume we are not changed
    // This is not right way to do data conversion. However since GVRF doesn't support
    // bone/weight/joint and other assimp data, we will put general model conversion
    // on hold and do this kind of conversion fist

    if (scene->getSceneDirtyFlag()) {

        std::vector<SceneObject*> scene_objects = scene->getWholeSceneObjects();
        std::vector<RenderData*> render_data_vector;

        // do occlusion culling, if enabled
        occlusion_cull(scene, scene_objects);

        // do frustum culling, if enabled
        frustum_cull(scene, eyeViewMatrix.GetTranslation(), scene_objects, render_data_vector,
                eyeViewProjection, shader_manager);

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
            renderRenderData(*it, eyeViewMatrix, eyeProjectionMatrix, renderMask, shader_manager);
        }

    } // flag checking

}

void Renderer::occlusion_cull(Scene* scene,
        std::vector<SceneObject*> scene_objects) {
#if _GVRF_USE_GLES3_
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
#endif
}

void Renderer::frustum_cull(Scene* scene, const OVR::Vector3f& camera_position,
        std::vector<SceneObject*> scene_objects,
        std::vector<RenderData*>& render_data_vector, const OVR::Matrix4f &vp_matrix,
        ShaderManager* shader_manager) {
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

        OVR::Matrix4f model_matrix_tmp = render_data->owner_object()->transform()->getModelMatrix();
        OVR::Matrix4f mvp_matrix_tmp(vp_matrix * model_matrix_tmp);

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
        OVR::Vector4f sphere_center(sphere_info[0], sphere_info[1], sphere_info[2],
                1.0f);
        OVR::Vector4f transformed_sphere_center = mvp_matrix_tmp.Transform(sphere_center);

        // Calculate distance from camera
        OVR::Vector4f position(camera_position, 1.0f);
        OVR::Vector4f difference = transformed_sphere_center - position;
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

#if _GVRF_USE_GLES3_
        //If a previous query is active, do not issue a new query.
        //This avoids overloading the GPU with too many queries
        //Queries may span multiple frames

        bool is_query_issued = scene_object->is_query_issued();
        if (!is_query_issued) {
            //Setup basic bounding box and material
            RenderData* bounding_box_render_data(new RenderData());
            Mesh* bounding_box_mesh = render_data->mesh()->getBoundingBox();
            bounding_box_render_data->set_mesh(bounding_box_mesh);

            GLuint *query = scene_object->get_occlusion_array();

            glDepthFunc (GL_LEQUAL);
            glEnable (GL_DEPTH_TEST);
            glColorMask(GL_FALSE, GL_FALSE, GL_FALSE, GL_FALSE);

            //Issue the query only with a bounding box
            glBeginQuery(GL_ANY_SAMPLES_PASSED, query[0]);
            shader_manager->getBoundingBoxShader()->render(mvp_matrix_tmp,
                    bounding_box_render_data,
                    bounding_box_render_data->pass(0)->material());
            glEndQuery (GL_ANY_SAMPLES_PASSED);
            scene_object->set_query_issued(true);

            glColorMask(GL_TRUE, GL_TRUE, GL_TRUE, GL_TRUE);

            //Delete the generated bounding box mesh
            bounding_box_mesh->cleanUp();
            delete bounding_box_render_data;
        }
#endif
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
        const OVR::Matrix4f& view_matrix, const OVR::Matrix4f& projection_matrix,
        int render_mask, ShaderManager* shader_manager) {
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
                Material* curr_material =
                        render_data->pass(curr_pass)->material();

                if (curr_material != nullptr) {
                    OVR::Matrix4f model_matrix = render_data->owner_object()->transform()->getModelMatrix();
                    OVR::Matrix4f mv_matrix(view_matrix * model_matrix);
                    OVR::Matrix4f mvp_matrix = projection_matrix * mv_matrix;
                    try {
                        bool right = render_mask
                                & RenderData::RenderMaskBit::Right;
                        switch (curr_material->shader_type()) {
                        case Material::ShaderType::UNLIT_HORIZONTAL_STEREO_SHADER:
                            shader_manager->getUnlitHorizontalStereoShader()->render(
                                    mvp_matrix, render_data, curr_material,
                                    right);
                            break;
                        case Material::ShaderType::UNLIT_VERTICAL_STEREO_SHADER:
                            shader_manager->getUnlitVerticalStereoShader()->render(
                                    mvp_matrix, render_data, curr_material,
                                    right);
                            break;
                        case Material::ShaderType::OES_SHADER:
                            shader_manager->getOESShader()->render(mvp_matrix,
                                    render_data, curr_material);
                            break;
                        case Material::ShaderType::OES_HORIZONTAL_STEREO_SHADER:
                            shader_manager->getOESHorizontalStereoShader()->render(
                                    mvp_matrix, render_data, curr_material,
                                    right);
                            break;
                        case Material::ShaderType::OES_VERTICAL_STEREO_SHADER:
                            shader_manager->getOESVerticalStereoShader()->render(
                                    mvp_matrix, render_data, curr_material,
                                    right);
                            break;
                        case Material::ShaderType::CUBEMAP_SHADER:
                            shader_manager->getCubemapShader()->render(
                                    model_matrix, mvp_matrix, render_data,
                                    curr_material);
                            break;
                        case Material::ShaderType::CUBEMAP_REFLECTION_SHADER:
                            shader_manager->getCubemapReflectionShader()->render(
                                    mv_matrix, mv_matrix.Inverted().Transposed(),
                                    view_matrix.Inverted(), mvp_matrix,
                                    render_data, curr_material);
                            break;
                        case Material::ShaderType::TEXTURE_SHADER:
                            shader_manager->getTextureShader()->render(
                                   mv_matrix, mv_matrix.Inverted().Transposed(),
                                   mvp_matrix, render_data, curr_material);
                            break;
                        case Material::ShaderType::EXTERNAL_RENDERER_SHADER:
                            shader_manager->getExternalRendererShader()->render(
                                    mvp_matrix, render_data);
                            break;
                        case Material::ShaderType::ASSIMP_SHADER:
                            shader_manager->getAssimpShader()->render(
                                    mv_matrix, mv_matrix.Inverted().Transposed(),
                                    mvp_matrix, render_data, curr_material);
                            break;
                        default:
                            shader_manager->getCustomShader(
                                    curr_material->shader_type())->render(
                                    mvp_matrix, render_data, curr_material,
                                    right);
                            break;
                        }
                    } catch (std::string error) {
                        LOGE(
                                "Error detected in Renderer::renderRenderData; name : %s, error : %s", render_data->owner_object()->name().c_str(), error.c_str());
                        shader_manager->getErrorShader()->render(mvp_matrix,
                                render_data);
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
