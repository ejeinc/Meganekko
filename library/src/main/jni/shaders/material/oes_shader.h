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

#define __gl2_h_
#ifndef GL_ES_VERSION_3_0
#include "GLES3/gl3.h"
#endif
#include <GLES2/gl2ext.h>
#include "Kernel/OVR_Math.h"

#include "objects/recyclable_object.h"

using namespace OVR;

namespace mgn {
class RenderData;
class Material;

class OESShader: public RecyclableObject {
public:
    OESShader();
    ~OESShader();
    void recycle();
    void render(const OVR::Matrix4f& mvp_matrix, RenderData* render_data, Material* material);

private:
    OESShader(const OESShader& oes_shader);
    OESShader(OESShader&& oes_shader);
    OESShader& operator=(const OESShader& oes_shader);
    OESShader& operator=(OESShader&& oes_shader);

private:
    GlProgram program_;
    GLuint a_position_;
    GLuint a_tex_coord_;
    GLuint u_mvp_;
    GLuint u_texture_;
    GLuint u_color_;
    GLuint u_opacity_;
};

}

#endif
