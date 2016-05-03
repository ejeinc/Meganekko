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

#include "includes.h"
#include "RenderPass.h"
#include "Material.h"

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_RenderPass_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new RenderPass());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderPass_setMaterial(JNIEnv* env, jobject obj, jlong jrenderPass, jlong jmaterial) {
    RenderPass* pass = reinterpret_cast<RenderPass*>(jrenderPass);
    Material* material = reinterpret_cast<Material*>(jmaterial);
    pass->set_material(material);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderPass_setCullFace(JNIEnv* env, jobject obj, jlong jrenderPass, jint jcullFace) {
    RenderPass* pass = reinterpret_cast<RenderPass*>(jrenderPass);
    pass->set_cull_face(static_cast<int>(jcullFace));
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
