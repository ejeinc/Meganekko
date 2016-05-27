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

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_setFrustumCulling(JNIEnv * env, jobject obj, jlong jscene, jboolean flag) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    scene->SetFrustumCulling(static_cast<bool>(flag));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_setOcclusionQuery(JNIEnv * env, jobject obj, jlong jscene, jboolean flag) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    scene->SetOcclusionCulling(static_cast<bool>(flag));
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
Java_com_eje_1c_meganekko_Scene_setViewMatrix(JNIEnv * jni, jobject obj, jlong jscene, jfloatArray jarray) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    jfloat tmp[16];
    jni->GetFloatArrayRegion(jarray, 0, 16, tmp);

    scene->SetViewMatrix(Matrix4f(
        tmp[0], tmp[1], tmp[2], tmp[3],
        tmp[4], tmp[5], tmp[6], tmp[7],
        tmp[8], tmp[9], tmp[10], tmp[11],
        tmp[12], tmp[13], tmp[14], tmp[15]
    ));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_setProjectionMatrix(JNIEnv * jni, jobject obj, jlong jscene, jfloatArray jarray) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    jfloat tmp[16];
    jni->GetFloatArrayRegion(jarray, 0, 16, tmp);

    scene->SetProjectionMatrix(Matrix4f(
        tmp[0], tmp[1], tmp[2], tmp[3],
        tmp[4], tmp[5], tmp[6], tmp[7],
        tmp[8], tmp[9], tmp[10], tmp[11],
        tmp[12], tmp[13], tmp[14], tmp[15]
    ));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_render(JNIEnv * jni, jobject obj, jlong jscene, jint eye) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    scene->Render(eye);
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

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_invalidateChildObjects(JNIEnv * jni, jobject obj, jlong jscene) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    scene->InvalidateChildObjects();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Scene_setBackgroundColor(JNIEnv * jni, jobject obj, jlong jscene, jfloat r, jfloat g, jfloat b, jfloat a) {
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    scene->SetBackgroundColor(Vector4f(r, g, b, a));
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
