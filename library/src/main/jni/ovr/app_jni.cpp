/*
 * Copyright 2015 eje inc.
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

#include <jni.h>
#include <Android/JniUtils.h>
#include "App.h"

using namespace OVR;

namespace mgn
{

extern "C"
{

void Java_ovr_App_startSystemActivity(JNIEnv * jni, jclass clazz, jlong appPtr, jstring command)
{
	JavaUTFChars str(jni, command);
    return ((App *) appPtr)->StartSystemActivity(str.ToStr());
}

//void Java_ovr_App_returnToHome(JNIEnv * jni, jclass clazz, jlong appPtr)
//{
//    return ((App *) appPtr)->ReturnToHome();
//}

//jint Java_ovr_App_getSystemBrightness(JNIEnv * jni, jclass clazz, jlong appPtr)
//{
//    return ((App *) appPtr)->GetSystemBrightness();
//}

//void Java_ovr_App_setSystemBrightness(JNIEnv * jni, jclass clazz, jlong appPtr, jint brightness)
//{
//    ((App *) appPtr)->SetSystemBrightness(brightness);
//}

//jboolean Java_ovr_App_getComfortMode(JNIEnv * jni, jclass clazz, jlong appPtr)
//{
//    return ((App *) appPtr)->GetComfortMode();
//}

//void Java_ovr_App_setComfortMode(JNIEnv * jni, jclass clazz, jlong appPtr, jboolean enable)
//{
//    ((App *) appPtr)->SetComfortMode(enable);
//}

//jboolean Java_ovr_App_getDoNotDisturbMode(JNIEnv * jni, jclass clazz, jlong appPtr)
//{
//    return ((App *) appPtr)->GetDoNotDisturbMode();
//}

//void Java_ovr_App_setDoNotDisturbMode(JNIEnv * jni, jclass clazz, jlong appPtr, jboolean enable)
//{
//    ((App *) appPtr)->SetDoNotDisturbMode(enable);
//}

jlong Java_ovr_App_getEyeBufferParms(JNIEnv * jni, jclass clazz, jlong appPtr)
{
	const ovrEyeBufferParms & eyeBufferParms = ((App *) appPtr)->GetEyeBufferParms();
	return (jlong)(intptr_t)&eyeBufferParms;
}

void Java_ovr_App_setEyeBufferParms(JNIEnv * jni, jclass clazz, jlong appPtr, jlong nativePtr)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	((App *) appPtr)->SetEyeBufferParms(*eyeBufferParms);
}

jint Java_ovr_App_getCpuLevel(JNIEnv * jni, jclass clazz, jlong appPtr)
{
    return ((App *) appPtr)->GetCpuLevel();
}

void Java_ovr_App_setCpuLevel(JNIEnv * jni, jclass clazz, jlong appPtr, jint cpuLevel)
{
    ((App *) appPtr)->SetCpuLevel(cpuLevel);
}

jint Java_ovr_App_getGpuLevel(JNIEnv * jni, jclass clazz, jlong appPtr)
{
    return ((App *) appPtr)->GetGpuLevel();
}

void Java_ovr_App_setGpuLevel(JNIEnv * jni, jclass clazz, jlong appPtr, jint gpuLevel)
{
    ((App *) appPtr)->SetGpuLevel(gpuLevel);
}

jboolean Java_ovr_App_getShowFPS(JNIEnv * jni, jclass clazz, jlong appPtr)
{
    ((App *) appPtr)->GetShowFPS();
}

void Java_ovr_App_setShowFPS(JNIEnv * jni, jclass clazz, jlong appPtr, jboolean show)
{
    ((App *) appPtr)->SetShowFPS(show);
}

jint Java_ovr_App_getMinimumVsyncs(JNIEnv * jni, jclass clazz, jlong appPtr)
{
    ((App *) appPtr)->GetMinimumVsyncs();
}

void Java_ovr_App_setMinimumVsyncs(JNIEnv * jni, jclass clazz, jlong appPtr, jint mininumVsyncs)
{
    ((App *) appPtr)->SetMinimumVsyncs(mininumVsyncs);
}

void Java_ovr_App_showInfoText(JNIEnv * jni, jclass clazz, jlong appPtr, jfloat duration, jstring fmt)
{
	JavaUTFChars str(jni, fmt);
    ((App *) appPtr)->ShowInfoText(duration, str.ToStr());
}



} // extern "C"

} // namespace mgn
