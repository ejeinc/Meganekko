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
#include "SceneObject.h"
#include "util/convert.h"

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_SceneObject_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new SceneObject());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachRenderData(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jrenderData) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    sceneObject->AttachRenderData(sceneObject, render_data);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachRenderData(JNIEnv * env, jobject obj, jlong jsceneObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->DetachRenderData();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_addChildObject(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jchild) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    SceneObject* child = reinterpret_cast<SceneObject*>(jchild);
    sceneObject->AddChildObject(sceneObject, child);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_removeChildObject(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jchild) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    SceneObject* child = reinterpret_cast<SceneObject*>(jchild);
    sceneObject->RemoveChildObject(child);
}

JNIEXPORT bool JNICALL
Java_com_eje_1c_meganekko_SceneObject_isColliding(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jotherObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    SceneObject* other_object = reinterpret_cast<SceneObject*>(jotherObject);
    return sceneObject->IsColliding(other_object);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setLODRange(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat minRange, jfloat maxRange) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetLODRange(minRange, maxRange);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMinRange(JNIEnv * env, jobject obj, jlong jsceneObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    return sceneObject->GetLODMinRange();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMaxRange(JNIEnv * env, jobject obj, jlong jsceneObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    return sceneObject->GetLODMaxRange();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setPosition(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat x, jfloat y, jfloat z){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetPosition(Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_getPosition(JNIEnv * env, jobject obj, jlong jsceneObject, jfloatArray values){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    Vector3f pos = sceneObject->GetPosition();
    FillElementsUnSafe(env, values, pos);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setScale(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat x, jfloat y, jfloat z){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetScale(Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_getScale(JNIEnv * env, jobject obj, jlong jsceneObject, jfloatArray values) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    Vector3f scale = sceneObject->GetScale();
    FillElementsUnSafe(env, values, scale);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setRotation(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat x, jfloat y, jfloat z, jfloat w){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetRotation(Quatf(x, y, z, w));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_getRotation(JNIEnv * env, jobject obj, jlong jsceneObject, jfloatArray values) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    Quatf rotation = sceneObject->GetRotation();
    FillElementsUnSafe(env, values, rotation);
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_SceneObject_setModelMatrix(JNIEnv * env, jobject obj, jlong jsceneObject,
        float m11, float m12, float m13, float m14,
        float m21, float m22, float m23, float m24,
        float m31, float m32, float m33, float m34,
        float m41, float m42, float m43, float m44) {

    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetModelMatrix(Matrix4f(
        m11, m12, m13, m14,
        m21, m22, m23, m24,
        m31, m32, m33, m34,
        m41, m42, m43, m44
    ));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_getModelMatrix(JNIEnv * env, jobject obj, jlong jsceneObject, jfloatArray values) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    Matrix4f m = sceneObject->GetModelMatrix();
    FillElementsUnSafe(env, values, m);
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
