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

#include "transform.h"

#include "util/gvr_jni.h"
#include "util/gvr_log.h"

namespace mgn {
extern "C" {
JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Transform_initNativeInstance(JNIEnv * env,
        jobject obj);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionX(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionY(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPosition(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationW(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationX(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationY(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationZ(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationYaw(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationPitch(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationRoll(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotation(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleX(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleY(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScale(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z);
JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_Transform_getModelMatrix(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setModelMatrix(JNIEnv * env,
        jobject obj, jlong jtransform, jfloatArray mat);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_translate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotationByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotateByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotateByAxisWithPivot(
        JNIEnv * env, jobject obj, jlong jtransform, jfloat angle,
        jfloat axis_x, jfloat axis_y, jfloat axis_z, jfloat pivot_x,
        jfloat pivot_y, jfloat pivot_z);
}
;

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Transform_initNativeInstance(JNIEnv * env,
        jobject obj) {
    return reinterpret_cast<jlong>(new Transform());
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionX(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getPosition().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionY(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getPosition().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getPosition().z;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPosition(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPosition(OVR::Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPositionX(x);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPositionY(y);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPositionZ(z);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationW(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().w;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationX(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationY(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationZ(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().z;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationYaw(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotationYaw();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationPitch(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotationPitch();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationRoll(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotationRoll();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotation(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setRotation(OVR::Quatf(x, y, z, w));
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleX(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getScale().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleY(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getScale().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getScale().z;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScale(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScale(OVR::Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScaleX(x);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScaleY(y);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScaleZ(z);
}

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_Transform_getModelMatrix(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    OVR::Matrix4f matrix = transform->getModelMatrix();
    jsize size = sizeof(matrix) / sizeof(jfloat);
    if (size != 16) {
        LOGE("sizeof(matrix) / sizeof(jfloat) != 16");
        throw "sizeof(matrix) / sizeof(jfloat) != 16";
    }
    jfloatArray jmatrix = env->NewFloatArray(size);
    env->SetFloatArrayRegion(jmatrix, 0, size, matrix.M[0]);
    return jmatrix;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setModelMatrix(JNIEnv * env,
		jobject obj, jlong jtransform, jfloatArray mat){
	Transform* transform = reinterpret_cast<Transform*>(jtransform);
	jfloat* mat_arr = env->GetFloatArrayElements(mat, 0);

	OVR::Matrix4f matrix(
	        mat_arr[0], mat_arr[1], mat_arr[2], mat_arr[3],
	        mat_arr[4], mat_arr[5], mat_arr[6], mat_arr[7],
            mat_arr[8], mat_arr[9], mat_arr[10], mat_arr[11],
            mat_arr[12], mat_arr[13], mat_arr[14], mat_arr[15]);

	transform->setModelMatrix(matrix);

	env->ReleaseFloatArrayElements(mat, mat_arr, 0);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_translate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->translate(x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotationByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setRotationByAxis(angle, x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotate(w, x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotateByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotateByAxis(angle, x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotateByAxisWithPivot(
        JNIEnv * env, jobject obj, jlong jtransform, jfloat angle,
        jfloat axis_x, jfloat axis_y, jfloat axis_z, jfloat pivot_x,
        jfloat pivot_y, jfloat pivot_z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotateByAxisWithPivot(angle, axis_x, axis_y, axis_z, pivot_x,
            pivot_y, pivot_z);
}

}
