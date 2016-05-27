/*
 * Copyright 2016 eje inc.
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
#include "Texture.h"

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Texture_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new Texture(env));
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Texture_getSurfaceTexture(JNIEnv * env, jobject obj, jlong jtexture) {
    Texture* texture = reinterpret_cast<Texture*>(jtexture);
    return texture->GetSurfaceTexture();
}

#ifdef __cplusplus
} // extern C
#endif
} // namespace mgn
