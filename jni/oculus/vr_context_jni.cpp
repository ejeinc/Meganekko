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

#include <jni.h>
#include "vr_context.h"

namespace mgn
{

extern "C"
{

jlong Java_com_eje_1c_meganekko_VrContext_nativeInit(JNIEnv * jni, jobject object)
{
    return (jlong) new Context();
}

void Java_com_eje_1c_meganekko_VrContext_nativeSetMainScene(JNIEnv * jni, jclass clazz,
        jlong contextPtr, jlong jscene)
{
    Context* context = reinterpret_cast<Context*>(contextPtr);
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    context->scene = scene;
}

void Java_com_eje_1c_meganekko_VrContext_nativeSetShaderManager(JNIEnv * jni,
        jclass clazz, jlong contextPtr, jlong jshader_manager)
{
    Context* context = reinterpret_cast<Context*>(contextPtr);
    ShaderManager* shaderManager =
            reinterpret_cast<ShaderManager*>(jshader_manager);
    context->shaderManager = shaderManager;
}

} // namespace mgn

} // extern "C"
