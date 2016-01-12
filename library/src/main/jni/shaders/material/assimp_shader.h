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
 * Shader for model loaded with Assimp
 ***************************************************************************/

#ifndef ASSIMP_SHADER_H_
#define ASSIMP_SHADER_H_

#include <memory>
#include "Kernel/OVR_Math.h"
#include "Kernel/OVR_GlUtils.h"

#ifndef GL_ES_VERSION_3_0
#include "GLES3/gl3.h"
#endif

#include "objects/recyclable_object.h"

#define SETBIT(num, i)                   num = (num | (1 << i))
#define ISSET(num, i)                    ((num & (1 << i)) != 0)
#define CLEARBIT(num, i)                 num = (num & ~(1 << i))

#define AS_DIFFUSE_TEXTURE                0x00000000
#define AS_SPECULAR_TEXTURE               0x00000001

/*
 * As the features are incremented, need to increase AS_TOTAL_FEATURE_COUNT
 * as well.
 *
 * Also the AS_TOTAL_GL_PROGRAM_COUNT is the total number of combinations
 * possible with these feature set i.e for AS_TOTAL_FEATURE_COUNT = 3
 * AS_TOTAL_GL_PROGRAM_COUNT = 8
 *
 */
#define AS_TOTAL_FEATURE_COUNT            2
#define AS_TOTAL_GL_PROGRAM_COUNT         4

namespace mgn {
class GLProgram;
class RenderData;
class Material;

class AssimpShader: public RecyclableObject {
public:
    AssimpShader();
    ~AssimpShader();
    void recycle();
    void render(const OVR::Matrix4f& model_matrix, const OVR::Matrix4f& model_it_matrix,
            const OVR::Matrix4f& mvp_matrix, RenderData* render_data,
            Material* material);

private:
    AssimpShader(const AssimpShader& assimp_shader);
    AssimpShader(AssimpShader&& assimp_shader);
    AssimpShader& operator=(const AssimpShader& assimp_shader);
    AssimpShader& operator=(AssimpShader&& assimp_shader);

private:
    GLProgram* program_;
    GLProgram** program_list_;

    GLuint a_position_;
    GLuint u_mvp_;
    GLuint a_tex_coord_;
    GLuint u_texture_;
    GLuint u_diffuse_color_;
    GLuint u_ambient_color_;
    GLuint u_color_;
    GLuint u_opacity_;
};

}

#endif
