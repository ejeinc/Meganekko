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
#include "Material.h"
#include "util/convert.h"

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Material_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new Material(env));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setColor(JNIEnv * env, jobject obj, jlong jmaterial, jfloat r, jfloat g, jfloat b, jfloat a) {
    Material* material = reinterpret_cast<Material*>(jmaterial);
    material->SetColor(Vector4f(r, g, b, a));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_getColor(JNIEnv * env, jobject obj, jlong jmaterial, jfloatArray values) {
    Material* material = reinterpret_cast<Material*>(jmaterial);
    Vector4f color = material->GetColor();
    FillElementsUnSafe(env, values, color);
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Material_getSurfaceTexture(JNIEnv * env, jobject obj, jlong jmaterial) {
    Material* material = reinterpret_cast<Material*>(jmaterial);
    return material->GetSurfaceTexture();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setStereoMode(JNIEnv * env, jobject obj, jlong jmaterial, jint jstereoMode) {
    Material* material = reinterpret_cast<Material*>(jmaterial);
    material->SetStereoMode(static_cast<Material::StereoMode>(jstereoMode));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setSide(JNIEnv* env, jobject obj, jlong jmaterial, jint jside) {
    Material* material = reinterpret_cast<Material*>(jmaterial);
    material->SetSide(jside);
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
