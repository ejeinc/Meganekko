/*
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
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

#include "MeganekkoActivity.h"

#include <jni.h>
#include <VrApi.h>
#include <VrApi_LocalPrefs.h>
#include "objects/Scene.h"
#include "objects/SceneObject.h"
#include "objects/components/RenderData.h"
#include "Kernel/OVR_Geometry.h"

namespace mgn
{

extern "C"
{

long Java_com_eje_1c_meganekko_MeganekkoActivity_nativeSetAppInterface(
        JNIEnv * jni, jclass clazz, jobject activity,
        jstring fromPackageName, jstring commandString,
        jstring uriString)
{
    return (new MeganekkoActivity())->SetActivity( jni, clazz, activity, fromPackageName, commandString, uriString );
}

void Java_com_eje_1c_meganekko_MeganekkoActivity_nativeHideGazeCursor(JNIEnv * jni, jclass clazz, jlong appPtr)
{
    MeganekkoActivity* activity = (MeganekkoActivity*)((App *)appPtr)->GetAppInterface();
    activity->GuiSys->GetGazeCursor().HideCursor();
}

void Java_com_eje_1c_meganekko_MeganekkoActivity_nativeShowGazeCursor(JNIEnv * jni, jclass clazz, jlong appPtr)
{
    MeganekkoActivity* activity = (MeganekkoActivity*)((App *)appPtr)->GetAppInterface();
    activity->GuiSys->GetGazeCursor().ShowCursor();
}

void Java_com_eje_1c_meganekko_MeganekkoActivity_setDebugOptionEnable(JNIEnv * jni, jclass clazz, jboolean enable)
{
    ovr_SetLocalPreferenceValueForKey(LOCAL_PREF_APP_DEBUG_OPTIONS, enable ? "1" : "0");
}

void Java_com_eje_1c_meganekko_MeganekkoActivity_recenterPose(JNIEnv * jni, jclass clazz, jlong appPtr)
{
    ovrMobile * mobile = ((App *)appPtr)->GetOvrMobile();
    vrapi_RecenterPose(mobile);
}

void Java_com_eje_1c_meganekko_MeganekkoActivity_setScene(JNIEnv * jni, jclass clazz, jlong appPtr, jlong jscene)
{
    MeganekkoActivity* activity = (MeganekkoActivity*)((App *)appPtr)->GetAppInterface();
    Scene* scene = reinterpret_cast<Scene*>(jscene);
    activity->scene = scene;
}

void Java_com_eje_1c_meganekko_MeganekkoActivity_setShaderManager(JNIEnv * jni, jclass clazz, jlong appPtr, jlong jshaderManager)
{
    MeganekkoActivity* activity = (MeganekkoActivity*)((App *)appPtr)->GetAppInterface();
    ShaderManager* shaderManager = reinterpret_cast<ShaderManager*>(jshaderManager);
    activity->shaderManager = shaderManager;
}

jboolean Java_com_eje_1c_meganekko_MeganekkoActivity_isLookingAt(JNIEnv * jni, jclass clazz, jlong appPtr, jlong jsceneObject)
{
    MeganekkoActivity* activity = (MeganekkoActivity*)((App *)appPtr)->GetAppInterface();
    SceneObject* sceneObject = reinterpret_cast<SceneObject*>(jsceneObject);

    Matrix4f m = sceneObject->transform()->getModelMatrix().InvertedHomogeneousTransform();
    const Vector3f rayStart = m.Transform(activity->scene->main_camera()->transform()->getPosition());
    const Vector3f rayDir = activity->scene->main_camera()->getLookAt();
    const float* boundingBoxInfo = sceneObject->render_data()->mesh()->getBoundingBoxInfo();
    const Vector3f mins(boundingBoxInfo[0], boundingBoxInfo[1], boundingBoxInfo[2]);
    const Vector3f maxs(boundingBoxInfo[3], boundingBoxInfo[4], boundingBoxInfo[5]);
    float t0 = 0.0f;
    float t1 = 0.0f;

    return Intersect_RayBounds(rayStart, rayDir, mins, maxs, t0, t1);
//    if (Intersect_RayBounds(rayStart, rayDir, mins, maxs, t0, t1)) {
//        return rayStart + t0 * rayDir;
//    }
}

} // extern "C"

} // namespace mgn
