/*
 * Copyright 2016 eje inc.
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

#include "includes.h"

#include "RenderData.h"

namespace mgn {

static const char * ImageExternalDirectives =
    "#extension GL_OES_EGL_image_external : enable\n"
    "#extension GL_OES_EGL_image_external_essl3 : enable\n";

static const char VERTEX_SHADER[] =
        "in vec4 Position;\n"
        "in vec2 TexCoord;\n"
        "uniform highp mat4 Mvpm;\n"
        "uniform highp mat4 Texm;\n"
        "out highp vec2 oTexCoord;\n"
        "void main() {\n"
        "  oTexCoord = vec2(Texm * vec4(TexCoord, 0, 1));\n"
        "  gl_Position = Mvpm * Position;\n"
        "}\n";

static const char FRAGMENT_SHADER[] =
        "precision highp float;\n"
        "uniform samplerExternalOES Texture0;\n"
        "uniform vec4 UniformColor;\n"
        "uniform float Opacity;\n"
        "in highp vec2 oTexCoord;\n"
        "void main() {\n"
        "  vec4 texel = texture2D(Texture0, oTexCoord) * UniformColor * Opacity;\n"
        "  if (texel.a < 0.1)\n"
        "    discard;\n"
        "  gl_FragColor = texel;\n"
        "}\n";

RenderData::RenderData() : Component(),
                            material(nullptr),
                            mesh(nullptr),
                            visible(true),
                            renderingOrder(DEFAULT_RENDERING_ORDER),
                            offset(false),
                            offsetFactor(0.0f),
                            offsetUnits(0.0f),
                            depthTest(true),
                            alphaBlend(true),
                            drawMode(GL_TRIANGLES) {
    program = GlProgram::Build(NULL, VERTEX_SHADER, ImageExternalDirectives, FRAGMENT_SHADER, NULL, 0);
    opacity = glGetUniformLocation(program.Program, "Opacity");
}

RenderData::~RenderData() {
    DeleteProgram(program);
}

void RenderData::Render(const Matrix4f & mvpMatrix, const GlGeometry & geometry, const Material * material, const int eye) {

    Vector4f color = material->GetColor();

    GL(glUseProgram(program.Program));

    GL(glUniformMatrix4fv(program.uMvp, 1, GL_TRUE, mvpMatrix.M[0]));
    GL(glUniformMatrix4fv(program.uTexm, 1, GL_TRUE, TexmForVideo(material->GetStereoMode(), eye).M[ 0 ] ));
    GL(glActiveTexture (GL_TEXTURE0));
    GL(glBindTexture(GL_TEXTURE_EXTERNAL_OES, material->GetTextureId()));
    GL(glUniform4f(program.uColor, color.x, color.y, color.z, color.w));
    GL(glUniform1f(opacity, material->GetOpacity()));

    geometry.Draw();

    GL(glBindTexture( GL_TEXTURE_EXTERNAL_OES, 0 ));
}

inline const Matrix4f & RenderData::TexmForVideo(const Material::StereoMode stereoMode, const int eye ) const
{
    switch (stereoMode) {
        case Material::StereoMode::TOP_BOTTOM:
            return eye ? bottomM : topM;

        case Material::StereoMode::BOTTOM_TOP:
            return eye ? topM : bottomM;

        case Material::StereoMode::LEFT_RIGHT:
            return eye ? rightM : leftM;

        case Material::StereoMode::RIGHT_LEFT:
            return eye ? leftM : rightM;

        case Material::StereoMode::TOP_ONLY:
            return topM;

        case Material::StereoMode::BOTTOM_ONLY:
            return bottomM;

        case Material::StereoMode::LEFT_ONLY:
            return leftM;

        case Material::StereoMode::RIGHT_ONLY:
            return rightM;

        case Material::StereoMode::NORMAL:
        default:
            return normalM;
    }
}
}
;
