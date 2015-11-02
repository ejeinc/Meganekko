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
Java_com_eje_1c_meganekko_NativeTransform_ctor(JNIEnv * env,
        jobject obj);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getPositionX(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getPositionY(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPosition(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPositionX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPositionY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationW(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationX(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationY(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationZ(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationYaw(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationPitch(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationRoll(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setRotation(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getScaleX(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getScaleY(JNIEnv * env,
        jobject obj, jlong jtransform);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScale(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScaleX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScaleY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z);
JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getModelMatrix(JNIEnv * env,
        jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setModelMatrix(JNIEnv * env,
        jobject obj, jlong jtransform, jfloatArray mat);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_translate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setRotationByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_rotate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_rotateByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_rotateByAxisWithPivot(
        JNIEnv * env, jobject obj, jlong jtransform, jfloat angle,
        jfloat axis_x, jfloat axis_y, jfloat axis_z, jfloat pivot_x,
        jfloat pivot_y, jfloat pivot_z);
}
;

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_NativeTransform_ctor(JNIEnv * env,
        jobject obj) {
    return reinterpret_cast<jlong>(new Transform());
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getPositionX(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->position().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getPositionY(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->position().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->position().z;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPosition(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_position(OVR::Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPositionX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_position_x(x);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPositionY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_position_y(y);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setPositionZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_position_z(z);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationW(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->rotation().w;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationX(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->rotation().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationY(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->rotation().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationZ(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->rotation().z;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationYaw(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->rotation_yaw();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationPitch(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->rotation_pitch();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getRotationRoll(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->rotation_roll();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setRotation(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_rotation(OVR::Quatf(x, y, z, w));
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getScaleX(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->scale().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getScaleY(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->scale().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->scale().z;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScale(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_scale(OVR::Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScaleX(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_scale_x(x);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScaleY(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat y) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_scale_y(y);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setScaleZ(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->set_scale_z(z);
}

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_NativeTransform_getModelMatrix(JNIEnv * env,
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
Java_com_eje_1c_meganekko_NativeTransform_setModelMatrix(JNIEnv * env,
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
Java_com_eje_1c_meganekko_NativeTransform_translate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->translate(x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_setRotationByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setRotationByAxis(angle, x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_rotate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotate(w, x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_rotateByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y,
        jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotateByAxis(angle, x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_NativeTransform_rotateByAxisWithPivot(
        JNIEnv * env, jobject obj, jlong jtransform, jfloat angle,
        jfloat axis_x, jfloat axis_y, jfloat axis_z, jfloat pivot_x,
        jfloat pivot_y, jfloat pivot_z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotateByAxisWithPivot(angle, axis_x, axis_y, axis_z, pivot_x,
            pivot_y, pivot_z);
}

}
