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
 * Renders a bounding box for occlusion query.
 ***************************************************************************/

#ifndef BOUNDING_BOX_SHADER_H_
#define BOUNDING_BOX_SHADER_H_

#include <memory>
#include "Kernel/OVR_GlUtils.h"

#ifndef GL_ES_VERSION_3_0
#include "GLES3/gl3.h"
#endif
#include "Kernel/OVR_Math.h"
#include "GlProgram.h"

#include "objects/recyclable_object.h"

using namespace OVR;

namespace mgn {
class RenderData;
class Material;

class BoundingBoxShader: public RecyclableObject {
public:
    BoundingBoxShader();
    ~BoundingBoxShader();
    void recycle();
    void render(const OVR::Matrix4f& mvp_matrix, RenderData* render_data, Material* material);

private:
    BoundingBoxShader(const BoundingBoxShader& bounding_box_shader);
    BoundingBoxShader(BoundingBoxShader&& bounding_box_shader);
    BoundingBoxShader& operator=(const BoundingBoxShader& bounding_box_shader);
    BoundingBoxShader& operator=(BoundingBoxShader&& bounding_box_shader);

private:
    GlProgram program_;
    GLuint a_position_;
    GLuint u_mvp_;
};

}

#endif
