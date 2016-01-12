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

#include "texture_shader.h"

#include "objects/material.h"
#include "objects/light.h"
#include "objects/mesh.h"
#include "objects/components/render_data.h"
#include "objects/textures/texture.h"
#include "util/gvr_gl.h"

#include "util/gvr_log.h"

namespace mgn {
static const char USE_LIGHT[] = "#define USE_LIGHT\n";
static const char NOT_USE_LIGHT[] = "#undef USE_LIGHT\n";
static const char VERTEX_SHADER[] =
        "attribute vec4 a_position;\n"
                "attribute vec4 a_tex_coord;\n"
                "uniform mat4 Mvpm;\n"
                "varying vec2 v_tex_coord;\n"
                "#ifdef USE_LIGHT\n"
                "attribute vec3 a_normal;\n"
                "uniform mat4 u_mv;\n"
                "uniform mat4 u_mv_it;\n"
                "uniform vec3 u_light_pos;\n"
                "varying vec3 v_viewspace_normal;\n"
                "varying vec3 v_viewspace_light_direction;\n"
                "#endif\n"
                "\n"
                "void main() {\n"
                "#ifdef USE_LIGHT\n"
                "  vec4 v_viewspace_position_vec4 = u_mv * a_position;\n"
                "  vec3 v_viewspace_position = v_viewspace_position_vec4.xyz / v_viewspace_position_vec4.w;\n"
                "  v_viewspace_light_direction = u_light_pos - v_viewspace_position;\n"
                "  v_viewspace_normal = (u_mv_it * vec4(a_normal, 1.0)).xyz;\n"
                "#endif\n"
                "  v_tex_coord = a_tex_coord.xy;\n"
                "  gl_Position = Mvpm * a_position;\n"
                "}\n";

static const char FRAGMENT_SHADER[] =
        "precision highp float;\n"
                "uniform sampler2D u_texture;\n"
                "uniform vec3 UniformColor;\n"
                "uniform float u_opacity;\n"
                "varying vec2 v_tex_coord;\n"
                "#ifdef USE_LIGHT\n"
                "uniform vec4 materialAmbientColor;\n"
                "uniform vec4 materialDiffuseColor;\n"
                "uniform vec4 materialSpecularColor;\n"
                "uniform float materialSpecularExponent;\n"
                "uniform vec4 lightAmbientIntensity;\n"
                "uniform vec4 lightDiffuseIntensity;\n"
                "uniform vec4 lightSpecularIntensity;\n"
                "varying vec3 v_viewspace_normal;\n"
                "varying vec3 v_viewspace_light_direction;\n"
                "#endif\n"
                "\n"
                "void main()\n"
                "{\n"
                "  vec4 color;\n"
                "#ifdef USE_LIGHT\n"
                "  // Dot product gives us diffuse intensity\n"
                "  float diffuse = max(0.0, dot(normalize(v_viewspace_normal), normalize(v_viewspace_light_direction)));\n"
                "\n"
                "  // Multiply intensity by diffuse color, force alpha to 1.0\n"
                "  color = diffuse * materialDiffuseColor * lightDiffuseIntensity;\n"
                "\n"
                "  // Add in ambient light\n"
                "  color += materialAmbientColor * lightAmbientIntensity;\n"
                "\n"
                "  // Modulate in the texture\n"
                "  color *= texture2D(u_texture, v_tex_coord);\n"
                "\n"
                "  // Specular Light\n"
                "  vec3 reflection = normalize(reflect(-normalize(v_viewspace_light_direction), normalize(v_viewspace_normal)));\n"
                "  float specular = max(0.0, dot(normalize(v_viewspace_normal), reflection));\n"
                "  if(diffuse != 0.0) {\n"
                "    color += pow(specular, materialSpecularExponent) * materialSpecularColor * lightSpecularIntensity;\n"
                "  }\n"
                "#else\n"
                "  color = texture2D(u_texture, v_tex_coord);\n"
                "#endif\n"
                "\n"
                "  gl_FragColor = vec4(color.x * UniformColor.x * u_opacity, color.y * UniformColor.y * u_opacity, color.z * UniformColor.z * u_opacity, color.w * u_opacity);\n"
                "}\n";

TextureShader::TextureShader() :
        a_position_(0), a_tex_coord_(
                0), a_normal_(0), u_mv_(0), u_mv_it_(0), u_light_pos_(
                0), u_texture_(0),  u_opacity_(0), u_material_ambient_color_(
                0), u_material_diffuse_color_(0), u_material_specular_color_(0), u_material_specular_exponent_(
                0), u_light_ambient_intensity_(0), u_light_diffuse_intensity_(
                0), u_light_specular_intensity_(0) {

    char VertexShaderLight[strlen(USE_LIGHT) + strlen(VERTEX_SHADER)];
    sprintf(VertexShaderLight, "%s%s", USE_LIGHT, VERTEX_SHADER);

    char VertexShaderNoLight[strlen(NOT_USE_LIGHT) + strlen(VERTEX_SHADER)];
    sprintf(VertexShaderNoLight, "%s%s", NOT_USE_LIGHT, VERTEX_SHADER);

    char FragmentShaderLight[strlen(USE_LIGHT) + strlen(FRAGMENT_SHADER)];
    sprintf(FragmentShaderLight, "%s%s", USE_LIGHT, FRAGMENT_SHADER);

    char FragmentShaderNoLight[strlen(NOT_USE_LIGHT) + strlen(FRAGMENT_SHADER)];
    sprintf(FragmentShaderNoLight, "%s%s", NOT_USE_LIGHT, FRAGMENT_SHADER);

    program_light_ = BuildProgram(VertexShaderLight, FragmentShaderLight);
    program_no_light_ = BuildProgram(VertexShaderNoLight, FragmentShaderNoLight);

    a_position_no_light_ = glGetAttribLocation(program_no_light_.program,
            "a_position");
    a_tex_coord_no_light_ = glGetAttribLocation(program_no_light_.program,
            "a_tex_coord");
    u_texture_no_light_ = glGetUniformLocation(program_no_light_.program,
            "u_texture");
    u_opacity_no_light_ = glGetUniformLocation(program_no_light_.program,
            "u_opacity");

    a_position_ = glGetAttribLocation(program_light_.program, "a_position");
    a_tex_coord_ = glGetAttribLocation(program_light_.program, "a_tex_coord");
    u_texture_ = glGetUniformLocation(program_light_.program, "u_texture");
    u_opacity_ = glGetUniformLocation(program_light_.program, "u_opacity");

    a_normal_ = glGetAttribLocation(program_light_.program, "a_normal");
    u_mv_ = glGetUniformLocation(program_light_.program, "u_mv");
    u_mv_it_ = glGetUniformLocation(program_light_.program, "u_mv_it");
    u_light_pos_ = glGetUniformLocation(program_light_.program, "u_light_pos");
    u_material_ambient_color_ = glGetUniformLocation(program_light_.program,
            "materialAmbientColor");
    u_material_diffuse_color_ = glGetUniformLocation(program_light_.program,
            "materialDiffuseColor");
    u_material_specular_color_ = glGetUniformLocation(program_light_.program,
            "materialSpecularColor");
    u_material_specular_exponent_ = glGetUniformLocation(program_light_.program,
            "materialSpecularExponent");
    u_light_ambient_intensity_ = glGetUniformLocation(program_light_.program,
            "lightAmbientIntensity");
    u_light_diffuse_intensity_ = glGetUniformLocation(program_light_.program,
            "lightDiffuseIntensity");
    u_light_specular_intensity_ = glGetUniformLocation(program_light_.program,
            "lightSpecularIntensity");
}

TextureShader::~TextureShader() {
    recycle();
}

void TextureShader::recycle() {
    DeleteProgram(program_light_);
    DeleteProgram(program_no_light_);
}

void TextureShader::render(const OVR::Matrix4f& mv_matrix,
        const OVR::Matrix4f& mv_it_matrix, const OVR::Matrix4f& mvp_matrix,
        RenderData* render_data, Material* material) {
    Mesh* mesh = render_data->mesh();
    Texture* texture = material->getTexture("main_texture");
    OVR::Vector3f color = material->getVec3("color");
    float opacity = material->getFloat("opacity");
    OVR::Vector4f material_ambient_color = material->getVec4("ambient_color");
    OVR::Vector4f material_diffuse_color = material->getVec4("diffuse_color");
    OVR::Vector4f material_specular_color = material->getVec4("specular_color");
    float material_specular_exponent = material->getFloat("specular_exponent");

    if (texture->getTarget() != GL_TEXTURE_2D) {
        std::string error = "TextureShader::render : texture with wrong target.";
        throw error;
    }

    bool use_light = false;
    Light* light;
    if (render_data->light_enabled()) {
        light = render_data->light();
        if (light->enabled()) {
            use_light = true;
        }
    }

    if (use_light) {
        mesh->setVertexLoc(a_position_);
        mesh->setTexCoordLoc(a_tex_coord_);
        mesh->setNormalLoc(a_normal_);
        mesh->generateVAO(Material::TEXTURE_SHADER);
    } else {
        mesh->setVertexLoc(a_position_no_light_);
        mesh->setTexCoordLoc(a_tex_coord_no_light_);
        mesh->setNormalLoc(-1);
        mesh->generateVAO(Material::TEXTURE_SHADER_NOLIGHT);
    }

    if (use_light) {
        glUseProgram(program_light_.program);
    } else {
        glUseProgram(program_no_light_.program);
    }

    glActiveTexture (GL_TEXTURE0);
    glBindTexture(texture->getTarget(), texture->getId());

    if (use_light) {
        OVR::Vector3f light_position = light->getVec3("position");
        OVR::Vector4f light_ambient_intensity = light->getVec4("ambient_intensity");
        OVR::Vector4f light_diffuse_intensity = light->getVec4("diffuse_intensity");
        OVR::Vector4f light_specular_intensity = light->getVec4("specular_intensity");

        glUniformMatrix4fv(program_light_.uMvp, 1, GL_TRUE, mvp_matrix.M[0]);
        glUniformMatrix4fv(u_mv_, 1, GL_TRUE, mv_matrix.M[0]);
        glUniformMatrix4fv(u_mv_it_, 1, GL_TRUE, mv_it_matrix.M[0]);
        glUniform3f(u_light_pos_, light_position.x, light_position.y,
                light_position.z);

        glUniform1i(u_texture_, 0);
        glUniform3f(program_light_.uColor, color.x, color.y, color.z);
        glUniform1f(u_opacity_, opacity);

        glUniform4f(u_material_ambient_color_, material_ambient_color.x,
                material_ambient_color.y, material_ambient_color.z,
                material_ambient_color.w);
        glUniform4f(u_material_diffuse_color_, material_diffuse_color.x,
                material_diffuse_color.y, material_diffuse_color.z,
                material_diffuse_color.w);
        glUniform4f(u_material_specular_color_, material_specular_color.x,
                material_specular_color.y, material_specular_color.z,
                material_specular_color.w);
        glUniform1f(u_material_specular_exponent_, material_specular_exponent);
        glUniform4f(u_light_ambient_intensity_, light_ambient_intensity.x,
                light_ambient_intensity.y, light_ambient_intensity.z,
                light_ambient_intensity.w);
        glUniform4f(u_light_diffuse_intensity_, light_diffuse_intensity.x,
                light_diffuse_intensity.y, light_diffuse_intensity.z,
                light_diffuse_intensity.w);
        glUniform4f(u_light_specular_intensity_, light_specular_intensity.x,
                light_specular_intensity.y, light_specular_intensity.z,
                light_specular_intensity.w);

        glBindVertexArray(mesh->getVAOId(Material::TEXTURE_SHADER));
    } else {
        glUniformMatrix4fv(program_no_light_.uMvp, 1, GL_TRUE, mvp_matrix.M[0]);

        glUniform1i(u_texture_no_light_, 0);
        glUniform3f(program_no_light_.uColor, color.x, color.y, color.z);
        glUniform1f(u_opacity_no_light_, opacity);

        glBindVertexArray(mesh->getVAOId(Material::TEXTURE_SHADER_NOLIGHT));
    }

    glDrawElements(GL_TRIANGLES, mesh->triangles().size(), GL_UNSIGNED_SHORT,
            0);
    glBindVertexArray(0);

    GL_CheckErrors("TextureShader::render");
}

}
;
