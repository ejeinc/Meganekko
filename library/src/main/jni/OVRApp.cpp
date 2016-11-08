/*
 * Copyright 2016 eje inc.
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

// Wrapper for VrAppFramework's OVR::App

#include <jni.h>
#include "Android/JniUtils.h"
#include "App.h"

using namespace OVR;

extern "C" {

void Java_org_meganekkovr_ovrjni_OVRApp_recenterYaw(JNIEnv *jni, jclass clazz,
                                                    jlong appPtr,
                                                    jboolean showBlack) {
  App *app = reinterpret_cast<App *>(appPtr);
  app->RecenterYaw(showBlack);
}

void Java_org_meganekkovr_ovrjni_OVRApp_startSystemActivity(JNIEnv *jni,
                                                            jclass clazz,
                                                            jlong appPtr,
                                                            jstring command) {
  // App *app = reinterpret_cast<App *>(appPtr);
  // JavaUTFChars str(jni, command);
  // app->StartSystemActivity(str.ToStr());
}

jint Java_org_meganekkovr_ovrjni_OVRApp_getCpuLevel(JNIEnv *jni, jclass clazz,
                                                    jlong appPtr) {
  App *app = reinterpret_cast<App *>(appPtr);
  return app->GetCpuLevel();
}

void Java_org_meganekkovr_ovrjni_OVRApp_setCpuLevel(JNIEnv *jni, jclass clazz,
                                                    jlong appPtr,
                                                    jint cpuLevel) {
  App *app = reinterpret_cast<App *>(appPtr);
  app->SetCpuLevel(cpuLevel);
}

jint Java_org_meganekkovr_ovrjni_OVRApp_getGpuLevel(JNIEnv *jni, jclass clazz,
                                                    jlong appPtr) {
  App *app = reinterpret_cast<App *>(appPtr);
  return app->GetGpuLevel();
}

void Java_org_meganekkovr_ovrjni_OVRApp_setGpuLevel(JNIEnv *jni, jclass clazz,
                                                    jlong appPtr,
                                                    jint gpuLevel) {
  App *app = reinterpret_cast<App *>(appPtr);
  app->SetGpuLevel(gpuLevel);
}

jint Java_org_meganekkovr_ovrjni_OVRApp_getMinimumVsyncs(JNIEnv *jni,
                                                         jclass clazz,
                                                         jlong appPtr) {
  App *app = reinterpret_cast<App *>(appPtr);
  return app->GetMinimumVsyncs();
}

void Java_org_meganekkovr_ovrjni_OVRApp_setMinimumVsyncs(JNIEnv *jni,
                                                         jclass clazz,
                                                         jlong appPtr,
                                                         jint minimumVsyncs) {
  App *app = reinterpret_cast<App *>(appPtr);
  app->SetMinimumVsyncs(minimumVsyncs);
}

void Java_org_meganekkovr_ovrjni_OVRApp_setShowFPS(JNIEnv *jni, jclass clazz,
                                                   jlong appPtr,
                                                   jboolean show) {
  // App *app = reinterpret_cast<App *>(appPtr);
  // app->SetShowFPS(show);
}

jboolean Java_org_meganekkovr_ovrjni_OVRApp_getShowFPS(JNIEnv *jni,
                                                       jclass clazz,
                                                       jlong appPtr) {
  // App *app = reinterpret_cast<App *>(appPtr);
  // return app->GetShowFPS();
  return false;
}

void Java_org_meganekkovr_ovrjni_OVRApp_showInfoText(JNIEnv *jni, jclass clazz,
                                                     jlong appPtr,
                                                     jfloat duration,
                                                     jstring text) {
  // App *app = reinterpret_cast<App *>(appPtr);
  // JavaUTFChars str(jni, text);
  // app->ShowInfoText(duration, str.ToStr());
}

} // extern "C"