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
#include "util/gvr_log.h"

namespace mgn {
extern "C" {
JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_SceneObject_initNativeInstance(JNIEnv * env,
        jobject obj);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachTransform(JNIEnv * env,
        jobject obj, jlong jscene_object, jlong jtransform);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachTransform(JNIEnv * env,
        jobject obj, jlong jscene_object);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachRenderData(
        JNIEnv * env, jobject obj, jlong jscene_object, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachRenderData(
        JNIEnv * env, jobject obj, jlong jscene_object);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachEyePointeeHolder(
        JNIEnv * env, jobject obj, jlong jscene_object,
        jlong jeye_pointee_holder);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachEyePointeeHolder(
        JNIEnv * env, jobject obj, jlong jscene_object);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_addChildObject(JNIEnv * env,
        jobject obj, jlong jscene_object, jlong jchild);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_removeChildObject(
        JNIEnv * env, jobject obj, jlong jscene_object, jlong jchild);

JNIEXPORT bool JNICALL
Java_com_eje_1c_meganekko_SceneObject_isColliding(
        JNIEnv * env, jobject obj, jlong jscene_object, jlong jother_object);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setLODRange(
        JNIEnv * env, jobject obj, jlong jscene_object, jfloat min_range, jfloat max_range);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMinRange(
        JNIEnv * env, jobject obj, jlong jscene_object);

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMaxRange(
        JNIEnv * env, jobject obj, jlong jscene_object);



JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_SceneObject_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new SceneObject(env, obj));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachTransform(JNIEnv * env,
        jobject obj, jlong jscene_object, jlong jtransform) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    Transform* transform = reinterpret_cast<Transform*>(jtransform);
    scene_object->attachTransform(scene_object, transform);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachTransform(JNIEnv * env,
        jobject obj, jlong jscene_object) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    scene_object->detachTransform();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_attachRenderData(
        JNIEnv * env, jobject obj, jlong jscene_object, jlong jrender_data) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
    scene_object->attachRenderData(scene_object, render_data);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_detachRenderData(
        JNIEnv * env, jobject obj, jlong jscene_object) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    scene_object->detachRenderData();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_addChildObject(JNIEnv * env,
        jobject obj, jlong jscene_object, jlong jchild) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    SceneObject* child = reinterpret_cast<SceneObject*>(jchild);
    scene_object->addChildObject(scene_object, child);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_removeChildObject(
        JNIEnv * env, jobject obj, jlong jscene_object, jlong jchild) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    SceneObject* child = reinterpret_cast<SceneObject*>(jchild);
    scene_object->removeChildObject(child);
}

JNIEXPORT bool JNICALL
Java_com_eje_1c_meganekko_SceneObject_isColliding(
        JNIEnv * env, jobject obj, jlong jscene_object, jlong jother_object) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    SceneObject* other_object = reinterpret_cast<SceneObject*>(jother_object);
    return scene_object->isColliding(other_object);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_SceneObject_setLODRange(
        JNIEnv * env, jobject obj, jlong jscene_object, jfloat min_range, jfloat max_range) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    scene_object->setLODRange(min_range, max_range);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMinRange(
        JNIEnv * env, jobject obj, jlong jscene_object) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    return scene_object->getLODMinRange();
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_SceneObject_getLODMaxRange(
        JNIEnv * env, jobject obj, jlong jscene_object) {
    SceneObject* scene_object = reinterpret_cast<SceneObject*>(jscene_object);
    return scene_object->getLODMaxRange();
}

} // extern "C"

} // namespace mgn
