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

#include "includes.h"

/***************************************************************************
 * Renders a GL_TEXTURE_EXTERNAL_OES texture.
 ***************************************************************************/

#ifndef OES_SHADER_H_
#define OES_SHADER_H_

#include "util/GL.h"
#include "Material.h"

using namespace OVR;

namespace mgn {
class RenderData;

    class OESShader {
public:
    OESShader();
    ~OESShader();
    void Render(const Matrix4f & mvpMatrix, const GlGeometry & geometry, const Material * material, const int eye);

private:
    OESShader(const OESShader& oesShader);
    OESShader(OESShader&& oesShader);
    OESShader& operator=(const OESShader& oesShader);
    OESShader& operator=(OESShader&& oesShader);

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
