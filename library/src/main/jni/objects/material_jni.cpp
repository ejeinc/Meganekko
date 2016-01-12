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
 * JNI
 ***************************************************************************/

#include "material.h"

#include "util/gvr_jni.h"

namespace mgn {
extern "C" {

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Material_ctor(JNIEnv * env, jobject obj, jint shader_type) {
    return reinterpret_cast<jlong>(new Material(static_cast<Material::ShaderType>(shader_type)));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setShaderType(JNIEnv * env, jobject obj, jlong jmaterial, jint shader_type) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    return material->set_shader_type(static_cast<Material::ShaderType>(shader_type));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setTexture(JNIEnv * env, jobject obj, jlong jmaterial, jstring key, jlong jtexture) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    Texture* texture = reinterpret_cast<Texture*>(jtexture);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    material->setTexture(native_key, texture);

    env->ReleaseStringUTFChars(key, char_key);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Material_getFloat(JNIEnv * env, jobject obj, jlong jmaterial, jstring key) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    jfloat material_float = material->getFloat(native_key);

    env->ReleaseStringUTFChars(key, char_key);

    return material_float;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setMat4(JNIEnv * env,
        jobject obj, jlong jmaterial, jstring key, jfloat x1, jfloat y1,
        jfloat z1, jfloat w1, jfloat x2, jfloat y2, jfloat z2, jfloat w2,
        jfloat x3, jfloat y3, jfloat z3, jfloat w3, jfloat x4, jfloat y4,
        jfloat z4, jfloat w4);
JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Material_setShaderFeatureSet(JNIEnv * env, jobject obj,
        jlong jmaterial, jint feature_set);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setFloat(JNIEnv * env, jobject obj, jlong jmaterial, jstring key, jfloat value) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    material->setFloat(native_key, value);

    env->ReleaseStringUTFChars(key, char_key);

}

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_Material_getVec2(JNIEnv * env, jobject obj, jlong jmaterial, jstring key) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    OVR::Vector2f material_vec2 = material->getVec2(native_key);
    jfloatArray jvec2 = env->NewFloatArray(2);
    env->SetFloatArrayRegion(jvec2, 0, 2, reinterpret_cast<jfloat*>(&material_vec2));

    env->ReleaseStringUTFChars(key, char_key);

    return jvec2;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setVec2(JNIEnv * env, jobject obj, jlong jmaterial, jstring key, jfloat x, jfloat y) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    material->setVec2(native_key, OVR::Vector2f(x, y));

    env->ReleaseStringUTFChars(key, char_key);
}

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_Material_getVec3(JNIEnv * env, jobject obj, jlong jmaterial, jstring key) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    OVR::Vector3f material_vec3 = material->getVec3(native_key);
    jfloatArray jvec3 = env->NewFloatArray(3);
    env->SetFloatArrayRegion(jvec3, 0, 3, reinterpret_cast<jfloat*>(&material_vec3));

    env->ReleaseStringUTFChars(key, char_key);

    return jvec3;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setVec3(JNIEnv * env, jobject obj, jlong jmaterial, jstring key, jfloat x, jfloat y, jfloat z) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);
    material->setVec3(native_key, OVR::Vector3f(x, y, z));
    env->ReleaseStringUTFChars(key, char_key);

}

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_Material_getVec4(JNIEnv * env, jobject obj, jlong jmaterial, jstring key) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    OVR::Vector4f material_vec4 = material->getVec4(native_key);
    jfloatArray jvec4 = env->NewFloatArray(4);
    env->SetFloatArrayRegion(jvec4, 0, 4, reinterpret_cast<jfloat*>(&material_vec4));

    env->ReleaseStringUTFChars(key, char_key);

    return jvec4;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setVec4(JNIEnv * env, jobject obj, jlong jmaterial, jstring key, jfloat x, jfloat y, jfloat z, jfloat w) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    material->setVec4(native_key, OVR::Vector4f(x, y, z, w));

    env->ReleaseStringUTFChars(key, char_key);

}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Material_setMat4(JNIEnv * env,
    jobject obj, jlong jmaterial, jstring key, jfloat x1, jfloat y1,
    jfloat z1, jfloat w1, jfloat x2, jfloat y2, jfloat z2, jfloat w2,
    jfloat x3, jfloat y3, jfloat z3, jfloat w3, jfloat x4, jfloat y4,
    jfloat z4, jfloat w4) {

    Material* material = reinterpret_cast<Material*>(jmaterial);
    const char* char_key = env->GetStringUTFChars(key, 0);
    std::string native_key = std::string(char_key);

    OVR::Matrix4f mat(x1, y1, z1, w1, x2, y2, z2, w2, x3, y3, z3, w3, x4, y4, z4, w4);
    material->setMat4(native_key, mat);

    env->ReleaseStringUTFChars(key, char_key);
}

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Material_setShaderFeatureSet(JNIEnv * env, jobject obj,
    jlong jmaterial, jint feature_set) {
Material* material = reinterpret_cast<Material*>(jmaterial);
material->set_shader_feature_set(feature_set);
}

}

}
