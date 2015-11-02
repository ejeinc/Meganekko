/*
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
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
 * Renders a texture with light.
 ***************************************************************************/

#ifndef TEXTURE_SHADER_H_
#define TEXTURE_SHADER_H_

#include <memory>

#ifndef GL_ES_VERSION_3_0
#include "GLES3/gl3.h"
#endif

#include "objects/recyclable_object.h"
#include "Kernel/OVR_Math.h"
#include "GlProgram.h"

using namespace OVR;

namespace mgn {
class RenderData;
class Material;

class TextureShader: public RecyclableObject {
public:
    TextureShader();
    ~TextureShader();
    void recycle();
    void render(const OVR::Matrix4f& model_matrix,
            const OVR::Matrix4f& model_it_matrix,
            const OVR::Matrix4f& mvp_matrix, RenderData* render_data,
            Material* material);

private:
    TextureShader(const TextureShader& texture_shader);
    TextureShader(TextureShader&& texture_shader);
    TextureShader& operator=(const TextureShader& texture_shader);
    TextureShader& operator=(TextureShader&& texture_shader);

private:
    GlProgram program_light_;
    GlProgram program_no_light_;

    GLuint a_position_no_light_;
    GLuint a_tex_coord_no_light_;
    GLuint u_texture_no_light_;
    GLuint u_opacity_no_light_;

    GLuint a_position_;
    GLuint a_tex_coord_;
    GLuint a_normal_;
    GLuint u_mv_;
    GLuint u_mv_it_;
    GLuint u_light_pos_;
    GLuint u_texture_;
    GLuint u_opacity_;
    GLuint u_material_ambient_color_;
    GLuint u_material_diffuse_color_;
    GLuint u_material_specular_color_;
    GLuint u_material_specular_exponent_;
    GLuint u_light_ambient_intensity_;
    GLuint u_light_diffuse_intensity_;
    GLuint u_light_specular_intensity_;
};

}

#endif
