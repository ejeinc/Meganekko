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
 * Renders a vertically split GL_TEXTURE_EXTERNAL_OES texture.
 ***************************************************************************/

#include "OESVerticalStereoShader.h"

#include "objects/Material.h"
#include "objects/mesh.h"
#include "objects/components/RenderData.h"
#include "util/gvr_gl.h"

namespace mgn {
static const char VERTEX_SHADER[] = "attribute vec4 a_position;\n"
        "attribute vec4 a_tex_coord;\n"
        "uniform mat4 u_mvp;\n"
        "varying vec2 v_tex_coord;\n"
        "void main() {\n"
        "  v_tex_coord = a_tex_coord.xy;\n"
        "  gl_Position = u_mvp * a_position;\n"
        "}\n";

static const char FRAGMENT_SHADER[] =
        "#extension GL_OES_EGL_image_external : require\n"
                "precision highp float;\n"
                "uniform samplerExternalOES u_texture;\n"
                "uniform vec3 u_color;\n"
                "uniform float u_opacity;\n"
                "uniform int u_right;\n"
                "varying vec2 v_tex_coord;\n"
                "void main()\n"
                "{\n"
                "  vec2 tex_coord = vec2(v_tex_coord.x, 0.5 * (v_tex_coord.y + float(u_right)));\n"
                "  vec4 color = texture2D(u_texture, tex_coord);\n"
                "  gl_FragColor = vec4(color.r * u_color.r * u_opacity, color.g * u_color.g * u_opacity, color.b * u_color.b * u_opacity, color.a * u_opacity);\n"
                "}\n";

OESVerticalStereoShader::OESVerticalStereoShader() :
        a_position_(0), a_tex_coord_(0), u_mvp_(0), u_texture_(0), u_color_(
                0), u_opacity_(0), u_right_(0) {
    program_ = BuildProgram(VERTEX_SHADER, FRAGMENT_SHADER);
    a_position_ = glGetAttribLocation(program_.program, "a_position");
    a_tex_coord_ = glGetAttribLocation(program_.program, "a_tex_coord");
    u_mvp_ = glGetUniformLocation(program_.program, "u_mvp");
    u_texture_ = glGetUniformLocation(program_.program, "u_texture");
    u_color_ = glGetUniformLocation(program_.program, "u_color");
    u_opacity_ = glGetUniformLocation(program_.program, "u_opacity");
    u_right_ = glGetUniformLocation(program_.program, "u_right");
}

OESVerticalStereoShader::~OESVerticalStereoShader() {
    recycle();
}

void OESVerticalStereoShader::recycle() {
    DeleteProgram(program_);
}

void OESVerticalStereoShader::render(const OVR::Matrix4f& mvp_matrix,
        RenderData* render_data, Material* material, bool right) {
    Mesh* mesh = render_data->mesh();
    OVR::Vector3f color = material->getVec3("color");
    float opacity = material->getFloat("opacity");

    mesh->setVertexLoc(a_position_);
    mesh->setTexCoordLoc(a_tex_coord_);
    mesh->generateVAO(Material::OES_VERTICAL_STEREO_SHADER);

    glUseProgram(program_.program);

    glUniformMatrix4fv(u_mvp_, 1, GL_TRUE, mvp_matrix.M[0]);
    glActiveTexture (GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, material->getId());
    glUniform1i(u_texture_, 0);
    glUniform3f(u_color_, color.x, color.y, color.z);
    glUniform1f(u_opacity_, opacity);
    glUniform1i(u_right_, right ? 1 : 0);

    glBindVertexArray(mesh->getVAOId(Material::OES_VERTICAL_STEREO_SHADER));
    glDrawElements(GL_TRIANGLES, mesh->triangles().size(), GL_UNSIGNED_SHORT,
            0);
    glBindVertexArray(0);

    GL_CheckErrors("OESVerticalStereoShader::render");
}

}
