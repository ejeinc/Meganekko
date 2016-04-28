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

#include <jni.h>

#include "Transform.h"
#include "util/convert.h"

namespace mgn {
extern "C" {

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Transform_initNativeInstance(JNIEnv * env, jobject obj);

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Transform_getPosition(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionX(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionY(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionZ(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPosition(JNIEnv * env, jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionX(JNIEnv * env, jobject obj, jlong jtransform, jfloat x);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionY(JNIEnv * env, jobject obj, jlong jtransform, jfloat y);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionZ(JNIEnv * env, jobject obj, jlong jtransform, jfloat z);

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Transform_getRotation(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationW(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationX(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationY(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationZ(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationYaw(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationPitch(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationRoll(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotation(JNIEnv * env, jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z);

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Transform_getScale(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleX(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleY(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleZ(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScale(JNIEnv * env, jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleX(JNIEnv * env, jobject obj, jlong jtransform, jfloat x);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleY(JNIEnv * env, jobject obj, jlong jtransform, jfloat y);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleZ(JNIEnv * env, jobject obj, jlong jtransform, jfloat z);

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_Transform_getModelMatrix(JNIEnv * env, jobject obj, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setModelMatrix(JNIEnv * env, jobject obj, jlong jtransform, jfloatArray mat);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_translate(JNIEnv * env, jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotationByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotate(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotateByAxis(JNIEnv * env,
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotateByAxisWithPivot(
        JNIEnv * env, jobject obj, jlong jtransform, jfloat angle,
        jfloat axisX, jfloat axisY, jfloat axisZ, jfloat pivotX,
        jfloat pivotY, jfloat pivotZ);
}

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Transform_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new Transform());
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Transform_getPosition(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return ToJava(env, transform->getPosition());
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionX(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getPosition().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionY(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getPosition().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getPositionZ(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getPosition().z;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPosition(JNIEnv * env, jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPosition(OVR::Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionX(JNIEnv * env, jobject obj, jlong jtransform, jfloat x) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPositionX(x);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionY(JNIEnv * env, jobject obj, jlong jtransform, jfloat y) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPositionY(y);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setPositionZ(JNIEnv * env, jobject obj, jlong jtransform, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setPositionZ(z);
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Transform_getRotation(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return ToJava(env, transform->getRotation());
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationW(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().w;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationX(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationY(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationZ(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotation().z;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationYaw(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotationYaw();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationPitch(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotationPitch();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getRotationRoll(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getRotationRoll();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotation(JNIEnv * env, jobject obj, jlong jtransform, jfloat w, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setRotation(OVR::Quatf(x, y, z, w));
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_Transform_getScale(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return ToJava(env, transform->getScale());
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleX(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getScale().x;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleY(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getScale().y;
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_Transform_getScaleZ(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    return transform->getScale().z;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScale(JNIEnv * env, jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScale(OVR::Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleX(JNIEnv * env, jobject obj, jlong jtransform, jfloat x) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScaleX(x);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleY(JNIEnv * env, jobject obj, jlong jtransform, jfloat y) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScaleY(y);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setScaleZ(JNIEnv * env, jobject obj, jlong jtransform, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->setScaleZ(z);
}

JNIEXPORT jfloatArray JNICALL
Java_com_eje_1c_meganekko_Transform_getModelMatrix(JNIEnv * env, jobject obj, jlong jtransform) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    OVR::Matrix4f matrix = transform->getModelMatrix();
    jsize size = sizeof(matrix) / sizeof(jfloat);
    if (size != 16) {
        throw "sizeof(matrix) / sizeof(jfloat) != 16";
    }
    jfloatArray jmatrix = env->NewFloatArray(size);
    env->SetFloatArrayRegion(jmatrix, 0, size, matrix.M[0]);
    return jmatrix;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setModelMatrix(JNIEnv * env, jobject obj, jlong jtransform, jfloatArray mat){
	Transform* transform = reinterpret_cast<Transform*>(jtransform);
	jfloat* matArr = env->GetFloatArrayElements(mat, 0);

	OVR::Matrix4f matrix(
	        matArr[0], matArr[1], matArr[2], matArr[3],
	        matArr[4], matArr[5], matArr[6], matArr[7],
                matArr[8], matArr[9], matArr[10], matArr[11],
                matArr[12], matArr[13], matArr[14], matArr[15]);

	transform->setModelMatrix(matrix);

	env->ReleaseFloatArrayElements(mat, matArr, 0);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_translate(JNIEnv * env, jobject obj, jlong jtransform, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->translate(x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_setRotationByAxis(JNIEnv * env, jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y, jfloat z) {
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
        jobject obj, jlong jtransform, jfloat angle, jfloat x, jfloat y, jfloat z) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotateByAxis(angle, x, y, z);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Transform_rotateByAxisWithPivot(
        JNIEnv * env, jobject obj, jlong jtransform, jfloat angle,
        jfloat axisX, jfloat axisY, jfloat axisZ, jfloat pivotX,
        jfloat pivotY, jfloat pivotZ) {
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    transform->rotateByAxisWithPivot(angle, axisX, axisY, axisZ, pivotX, pivotY, pivotZ);
}

}
