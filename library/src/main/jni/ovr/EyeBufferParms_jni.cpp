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

#include "includes.h"

using namespace OVR;

namespace mgn
{

extern "C"
{

jint Java_ovr_EyeBufferParms_getResolutionWidth(JNIEnv * jni, jclass clazz, jlong nativePtr)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	return eyeBufferParms->resolutionWidth;
}

void Java_ovr_EyeBufferParms_setResolutionWidth(JNIEnv * jni, jclass clazz, jlong nativePtr, jint resolutionWidth)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	eyeBufferParms->resolutionWidth = resolutionWidth;
}

jint Java_ovr_EyeBufferParms_getResolutionHeight(JNIEnv * jni, jclass clazz, jlong nativePtr)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	return eyeBufferParms->resolutionHeight;
}

void Java_ovr_EyeBufferParms_setResolutionHeight(JNIEnv * jni, jclass clazz, jlong nativePtr, jint resolutionHeight)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	eyeBufferParms->resolutionHeight = resolutionHeight;
}

jint Java_ovr_EyeBufferParms_getMultisamples(JNIEnv * jni, jclass clazz, jlong nativePtr)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	return eyeBufferParms->multisamples;
}

void Java_ovr_EyeBufferParms_setMultisamples(JNIEnv * jni, jclass clazz, jlong nativePtr, jint multisamples)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	eyeBufferParms->multisamples = multisamples;
}

jboolean Java_ovr_EyeBufferParms_getResolveDepth(JNIEnv * jni, jclass clazz, jlong nativePtr)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	return eyeBufferParms->resolveDepth;
}

void Java_ovr_EyeBufferParms_setResolveDepth(JNIEnv * jni, jclass clazz, jlong nativePtr, jboolean resolveDepth)
{
	ovrEyeBufferParms * eyeBufferParms = reinterpret_cast<ovrEyeBufferParms *>(nativePtr);
	eyeBufferParms->resolveDepth = resolveDepth;
}

} // extern "C"

} // namespace mgn
