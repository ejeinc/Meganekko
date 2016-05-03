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
    sceneObject->attachRenderData(sceneObject, render_data);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachRenderData(JNIEnv * env, jobject obj, jlong jsceneObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->detachRenderData();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_addChildObject(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jchild) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    SceneObject* child = reinterpret_cast<SceneObject*>(jchild);
    sceneObject->addChildObject(sceneObject, child);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_removeChildObject(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jchild) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    SceneObject* child = reinterpret_cast<SceneObject*>(jchild);
    sceneObject->removeChildObject(child);
}

JNIEXPORT bool JNICALL
Java_com_eje_1c_meganekko_SceneObject_isColliding(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jotherObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    SceneObject* other_object = reinterpret_cast<SceneObject*>(jotherObject);
    return sceneObject->isColliding(other_object);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setLODRange(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat minRange, jfloat maxRange) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->setLODRange(minRange, maxRange);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMinRange(JNIEnv * env, jobject obj, jlong jsceneObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    return sceneObject->getLODMinRange();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMaxRange(JNIEnv * env, jobject obj, jlong jsceneObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    return sceneObject->getLODMaxRange();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setPosition(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat x, jfloat y, jfloat z){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetPosition(Vector3f(x, y, z));
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_SceneObject_getPosition(JNIEnv * env, jobject obj, jlong jsceneObject){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    return ToJava(env, sceneObject->GetPosition());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setScale(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat x, jfloat y, jfloat z){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetScale(Vector3f(x, y, z));
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_SceneObject_getScale(JNIEnv * env, jobject obj, jlong jsceneObject){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    return ToJava(env, sceneObject->GetScale());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setRotation(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat x, jfloat y, jfloat z, jfloat w){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->SetRotation(Quatf(x, y, z, w));
}

JNIEXPORT jobject JNICALL
Java_com_eje_1c_meganekko_SceneObject_getRotation(JNIEnv * env, jobject obj, jlong jsceneObject){
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    return ToJava(env, sceneObject->GetRotation());
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
