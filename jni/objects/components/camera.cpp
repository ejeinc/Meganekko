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
 * Holds left, right cameras and reacts to the rotation sensor.
 ***************************************************************************/

#include "camera.h"

#include "objects/scene_object.h"

namespace mgn {

Camera::Camera() :
        SceneObject(){
}

Camera::~Camera() {
}

OVR::Vector3f Camera::getLookAt() const {

    OVR::Vector3f forward = OVR::Vector3f(0.0f, 0.0f, -1.0f);
    return transform()->getModelMatrix().Transform(forward);

}

}
