/* Copyright 2015 eje inc.
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

#include <jni.h>
#include <VrApi.h>
#include <Kernel/OVR_Math.h>
#include "MeganekkoActivity.h"

using namespace OVR;

namespace mgn
{

extern "C"
{

static const Quatf COORDINATE_QUATERNION = Quatf(0.0f, 0.0f, -sqrtf(0.5), sqrtf(0.5f));
static const Quatf OFFSET_QUATERNION     = Quatf(0.0f, sqrtf(0.5f), 0.0f, sqrtf(0.5f));
static const Quatf CONSTANT_EXPRESSION   = COORDINATE_QUATERNION.Inverted() * OFFSET_QUATERNION;

void Java_com_eje_1c_meganekko_InternalSensorManager_setSensorValues(const JNIEnv * jni, const jclass clazz, const jlong appPtr, const jfloat x, const jfloat y, const jfloat z, const jfloat w)
{
    const Quatf quaternion = CONSTANT_EXPRESSION * Quatf(x, y, z, w) * COORDINATE_QUATERNION;

    MeganekkoActivity* activity = (MeganekkoActivity*) ((App *) appPtr)->GetAppInterface();
    Camera * camera = const_cast<Camera *>(activity->scene->main_camera());
    camera->transform()->set_rotation(quaternion);
}

} // extern "C"

} // namespace mgn
