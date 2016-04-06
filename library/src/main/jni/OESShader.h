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
 * Renders a GL_TEXTURE_EXTERNAL_OES texture.
 ***************************************************************************/

#ifndef OES_SHADER_H_
#define OES_SHADER_H_

#include <memory>
#include "GlProgram.h"
#include "Kernel/OVR_GlUtils.h"

#define __gl2_h_
#ifndef GL_ES_VERSION_3_0
#include "GLES3/gl3.h"
#endif
#include <GLES2/gl2ext.h>
#include "Kernel/OVR_Math.h"

#include "Material.h"

using namespace OVR;

namespace mgn {
class RenderData;

    class OESShader {
public:
    OESShader();
    ~OESShader();
    void render(const Matrix4f& mvpMatrix, RenderData* renderData, Material* material, const int eye);

private:
    OESShader(const OESShader& oes_shader);
    OESShader(OESShader&& oes_shader);
    OESShader& operator=(const OESShader& oes_shader);
    OESShader& operator=(OESShader&& oes_shader);

    const Matrix4f & TexmForVideo(const Material::StereoMode stereoMode, const int eye);
private:
    GlProgram program;
    GLuint opacity;

    Matrix4f normalM = Matrix4f::Identity();
    Matrix4f topM = Matrix4f(
            1, 0, 0, 0,
            0, 0.5f, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1 );
    Matrix4f bottomM = Matrix4f(
            1, 0, 0, 0,
            0, 0.5f, 0, 0.5f,
            0, 0, 1, 0,
            0, 0, 0, 1 );
    Matrix4f leftM = Matrix4f(
            0.5f, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1 );
    Matrix4f rightM = Matrix4f(
            0.5f, 0, 0, 0.5f,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1 );
};

}

#endif
