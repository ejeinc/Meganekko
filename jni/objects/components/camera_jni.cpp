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


#include "camera.h"
#include "util/gvr_jni.h"

#include "util/gvr_java_stack_trace.h"

namespace gvr {

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_NativeCamera_ctor(JNIEnv * env, jobject obj);

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_NativeCamera_getLookAt(JNIEnv * env, jobject obj, jlong jcamera);

};

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_NativeCamera_ctor(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new Camera());
}

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_NativeCamera_getLookAt(JNIEnv * env, jobject obj, jlong jcamera) {
    Camera* camera = reinterpret_cast<Camera*>(jcamera);
    OVR::Vector3f look_at_vector = camera->getLookAt();
    jsize size = sizeof(OVR::Vector3f) / sizeof(jfloat);
    if (size != 3) {
        LOGE("sizeof(OVR::Vector3f) / sizeof(jfloat) != 3");
        throw "sizeof(OVR::Vector3f) / sizeof(jfloat) != 3";
    }
    jfloatArray look_at_array = env->NewFloatArray(size);
    env->SetFloatArrayRegion(look_at_array, 0, size, reinterpret_cast<jfloat *>(&look_at_vector));
    return look_at_array;
}

}
