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
 * Manages instances of shaders.
 ***************************************************************************/

#ifndef SHADER_MANAGER_H_
#define SHADER_MANAGER_H_

#include "objects/HybridObject.h"
#include "shaders/material/OESHorizontalStereoShader.h"
#include "shaders/material/OESShader.h"
#include "shaders/material/OESVerticalStereoShader.h"
#include "util/gvr_log.h"

namespace mgn {
class ShaderManager: public HybridObject {
public:
    ShaderManager() :
            HybridObject(),
            oes_shader_(), oes_horizontal_stereo_shader_(), oes_vertical_stereo_shader_(),
            latest_custom_shader_id_(INITIAL_CUSTOM_SHADER_INDEX) {
    }
    ~ShaderManager() {
        delete oes_shader_;
        delete oes_horizontal_stereo_shader_;
        delete oes_vertical_stereo_shader_;
        // We don't delete the custom shaders, as their Java owner-objects will do that for us.
    }
    OESShader* getOESShader() {
        if (!oes_shader_) {
            oes_shader_ = new OESShader();
        }
        return oes_shader_;
    }
    OESHorizontalStereoShader* getOESHorizontalStereoShader() {
        if (!oes_horizontal_stereo_shader_) {
            oes_horizontal_stereo_shader_ = new OESHorizontalStereoShader();
        }
        return oes_horizontal_stereo_shader_;
    }
    OESVerticalStereoShader* getOESVerticalStereoShader() {
        if (!oes_vertical_stereo_shader_) {
            oes_vertical_stereo_shader_ = new OESVerticalStereoShader();
        }
        return oes_vertical_stereo_shader_;
    }

private:
    ShaderManager(const ShaderManager& shader_manager);
    ShaderManager(ShaderManager&& shader_manager);
    ShaderManager& operator=(const ShaderManager& shader_manager);
    ShaderManager& operator=(ShaderManager&& shader_manager);

private:
    static const int INITIAL_CUSTOM_SHADER_INDEX = 1000;
    OESShader* oes_shader_;
    OESHorizontalStereoShader* oes_horizontal_stereo_shader_;
    OESVerticalStereoShader* oes_vertical_stereo_shader_;
    int latest_custom_shader_id_;
};

}
#endif
