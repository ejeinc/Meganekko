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
        "uniform highp mat4 Texm[NUM_VIEWS];\n"
        "out highp vec2 oTexCoord;\n"
        "void main() {\n"
        "  oTexCoord = vec2(Texm[VIEW_ID] * vec4(TexCoord, 0, 1));\n"
        "  gl_Position = TransformVertex(Position);\n"
        "}\n";

static const char FRAGMENT_SHADER[] =
        "precision highp float;\n"
        "uniform samplerExternalOES Texture0;\n"
        "uniform float Opacity;\n"
        "in highp vec2 oTexCoord;\n"
        "void main() {\n"
        "  vec4 texel = texture2D(Texture0, oTexCoord) * Opacity;\n"
        "  if (texel.a < 0.1)\n"
        "    discard;\n"
        "  gl_FragColor = texel;\n"
        "}\n";

RenderData::RenderData() : HybridObject(),
                            material(nullptr),
                            mesh(nullptr),
                            visible(true),
                            renderingOrder(DEFAULT_RENDERING_ORDER),
                            opacity(1.0f) {

    ovrProgramParm parms[] = {
        {"Texture0",     ovrProgramParmType::TEXTURE_SAMPLED},
        {"Opacity",      ovrProgramParmType::FLOAT},
        {"Texm",         ovrProgramParmType::FLOAT_MATRIX4}
    };
    GlProgram program = GlProgram::Build(NULL, VERTEX_SHADER, ImageExternalDirectives, FRAGMENT_SHADER,
                               parms, sizeof(parms) / sizeof(ovrProgramParm));
    
    surfaceDef.graphicsCommand.Program = program;

    // Enable alpha blending
    surfaceDef.graphicsCommand.GpuState.blendMode = GL_FUNC_ADD;
    surfaceDef.graphicsCommand.GpuState.blendSrc = GL_ONE;
    surfaceDef.graphicsCommand.GpuState.blendDst = GL_ONE_MINUS_SRC_ALPHA;
    surfaceDef.graphicsCommand.GpuState.blendEnable = ovrGpuState::BLEND_ENABLE;
}

void RenderData::SetMesh(Mesh* mesh) {
    this->mesh = mesh;

    if (mesh) {
        surfaceDef.geo = mesh->GetGeometry();
    }
}

void RenderData::SetMaterial(Material* material) {
    this->material = material;
}

Material* RenderData::GetMaterial() const {
    return material;
}

Material* RenderData::GetMaterial() {
    return material;
}

bool RenderData::IsVisible() const {
    return visible;
}

bool RenderData::IsVisible() {
    return visible;
}

void RenderData::SetVisible(bool visible) {
    this->visible = visible;
}

int RenderData::GetRenderingOrder() const {
    return renderingOrder;
}

void RenderData::SetRenderingOrder(int renderingOrder) {
    this->renderingOrder = renderingOrder;
}

bool RenderData::GetDepthTest() const {
    return surfaceDef.graphicsCommand.GpuState.depthEnable;
}

void RenderData::SetDepthTest(bool depthTest) {
    surfaceDef.graphicsCommand.GpuState.depthEnable = depthTest;
}

void RenderData::UpdateSurfaceDef() {

    // Texture0
    programTexture = material->GetGlTexture();
    surfaceDef.graphicsCommand.UniformData[0].Data = &programTexture;

    // Opacity
    surfaceDef.graphicsCommand.UniformData[1].Data = &opacity;

    // Texm
    programMatrices[0] = TexmForVideo(material->GetStereoMode(), 0);
    programMatrices[1] = TexmForVideo(material->GetStereoMode(), 1);
    surfaceDef.graphicsCommand.UniformData[2].Data = &programMatrices[0];
    surfaceDef.graphicsCommand.UniformData[2].Count = 2;
}

void RenderData::SetOpacity(float opacity) {
    this->opacity = opacity;
}

float RenderData::GetOpacity() {
    return opacity;
}

const ovrSurfaceDef & RenderData::GetSurfaceDef() {
    return surfaceDef;
}

void RenderData::SetModelMatrix(const Matrix4f & modelMatrix) {
    this->modelMatrix = modelMatrix;
}

const Matrix4f & RenderData::GetModelMatrix() {
    return modelMatrix;
}

const Matrix4f & RenderData::GetModelMatrix() const {
    return modelMatrix;
}

inline const Matrix4f & RenderData::TexmForVideo(const Material::StereoMode stereoMode, const int eye ) const {
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
