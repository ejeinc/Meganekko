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
#include "Scene.h"
#include "util/convert.h"

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Scene_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new Scene());
}

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_Scene_isLookingAt(JNIEnv * env, jobject obj, jlong jscene, jlong jsceneObject) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    IntersectRayBoundsResult result = scene->IntersectRayBounds(sceneObject, false);
    return result.intersected;
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_getLookingPoint(JNIEnv * env, jobject obj, jlong jscene, jlong jsceneObject, jboolean axisInWorld, jfloatArray values) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);
    IntersectRayBoundsResult result = scene->IntersectRayBounds(sceneObject, axisInWorld);
    FillElementsUnSafe(env, values, result.first);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_setViewPosition(JNIEnv * jni, jobject obj, jlong jscene, jfloat x, jfloat y, jfloat z) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    scene->SetViewPosition(Vector3f(x, y, z));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_getViewPosition(JNIEnv * jni, jobject obj, jlong jscene, jfloatArray values) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    Vector3f pos = scene->GetViewPosition();
    FillElementsUnSafe(jni, values, pos);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_getViewOrientation(JNIEnv * jni, jobject obj, jlong jscene, jfloatArray values) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    Quatf orientation = Quatf(scene->GetCenterViewMatrix().InvertedHomogeneousTransform());
    FillElementsUnSafe(jni, values, orientation);
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
