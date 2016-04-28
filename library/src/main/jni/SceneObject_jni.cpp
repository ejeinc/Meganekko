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

#include "SceneObject.h"

namespace mgn {
extern "C" {
JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_SceneObject_initNativeInstance(JNIEnv * env, jobject obj);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachTransform(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachTransform(JNIEnv * env, jobject obj, jlong jsceneObject);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachRenderData(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jrenderData);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachRenderData(JNIEnv * env, jobject obj, jlong jsceneObject);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_addChildObject(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jchild);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_removeChildObject(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jchild);

JNIEXPORT bool JNICALL
Java_com_eje_1c_meganekko_SceneObject_isColliding(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jotherObject);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setLODRange(JNIEnv * env, jobject obj, jlong jsceneObject, jfloat minRange, jfloat maxRange);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMinRange(JNIEnv * env, jobject obj, jlong jsceneObject);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMaxRange(JNIEnv * env, jobject obj, jlong jsceneObject);

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_SceneObject_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new SceneObject());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachTransform(JNIEnv * env, jobject obj, jlong jsceneObject, jlong jtransform) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    sceneObject->attachTransform(sceneObject, transform);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachTransform(JNIEnv * env, jobject obj, jlong jsceneObject) {
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    sceneObject->detachTransform();
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

} // extern "C"

} // namespace mgn
