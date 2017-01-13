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
#include "GearVRActivity.h"
#include "Android/JniUtils.h"
#include "Entity.h"
#include "GeometryComponent.h"
#include "util/convert.h"
#include <jni.h>

using namespace OVR;

namespace mgn {

GearVRActivity::GearVRActivity()
    : hmdMounted(false), clearColorBuffer(true), clearColor(0, 0, 0, 1) {}

GearVRActivity::~GearVRActivity() { delete shader; }

jmethodID GearVRActivity::GetMethodID(const char *name, const char *signature) {
  return app->GetJava()->Env->GetMethodID(app->GetAppInterface()->ActivityClass,
                                          name, signature);
}

void GearVRActivity::Configure(ovrSettings &settings) {
  settings.RenderMode = RENDERMODE_MULTIVIEW;
  settings.EyeBufferParms.multisamples = 4;
}

void GearVRActivity::EnteredVrMode(const ovrIntentType intentType,
                                   const char *intentFromPackage,
                                   const char *intentJSON,
                                   const char *intentURI) {
  OVR_UNUSED(intentFromPackage);
  OVR_UNUSED(intentJSON);
  OVR_UNUSED(intentURI);

  // Init
  if (intentType == INTENT_LAUNCH) {
    JNIEnv *jni = app->GetJava()->Env;
    jmethodID initMethodID = GetMethodID("init", "()V");
    jni->CallVoidMethod(app->GetJava()->ActivityObject, initMethodID);

    updateMethodID = GetMethodID("update", "(J)V");
    collectSurfaceDefsMethodID = GetMethodID("collectSurfaceDefs", "(J)V");
    onKeyEventMethodID = GetMethodID("onKeyEvent", "(III)Z");
    onHmdMountedMethodID = GetMethodID("onHmdMounted", "()V");
    onHmdUnmountedMethodID = GetMethodID("onHmdUnmounted", "()V");

    shader = new Shader();
  }

  // GearVRActivity.enteredVrMode()
  JNIEnv *jni = app->GetJava()->Env;
  jni->CallVoidMethod(app->GetJava()->ActivityObject,
                      GetMethodID("enteredVrMode", "()V"));
}

void GearVRActivity::LeavingVrMode() {
  // GearVRActivity.leavingVrMode()
  JNIEnv *jni = app->GetJava()->Env;
  jni->CallVoidMethod(app->GetJava()->ActivityObject,
                      GetMethodID("leavingVrMode", "()V"));
}

ovrFrameResult GearVRActivity::Frame(const ovrFrameInput &frame) {

  const ovrJava *java = app->GetJava();
  JNIEnv *jni = java->Env;

  // onHmdMounted, onHmdUnmounted
  const bool headsetIsMounted = frame.DeviceStatus.HeadsetIsMounted;
  if (!hmdMounted && headsetIsMounted) {
    jni->CallVoidMethod(app->GetJava()->ActivityObject, onHmdMountedMethodID);
  } else if (hmdMounted && !headsetIsMounted) {
    jni->CallVoidMethod(app->GetJava()->ActivityObject, onHmdUnmountedMethodID);
  }
  hmdMounted = headsetIsMounted;

  // Input handling
  HandleInput(frame.Input);

  // Update frame
  centerEyeViewMatrix = vrapi_GetCenterEyeViewMatrix(&app->GetHeadModelParms(),
                                                     &frame.Tracking, nullptr);

  jni->CallVoidMethod(java->ActivityObject, updateMethodID, (jlong)&frame);

  // Create frame result
  ovrFrameResult res;
  res.ClearColorBuffer = clearColorBuffer;
  res.ClearColor = clearColor;
  res.FrameMatrices.CenterView = centerEyeViewMatrix;

  // Collect ovrDrawSurfaces from Scene
  jni->CallVoidMethod(java->ActivityObject, collectSurfaceDefsMethodID,
                      (jlong)&res.Surfaces);

  // Set all program
  for (int i = 0; i < res.Surfaces.GetSizeI(); i++) {
    ovrDrawSurface drawSurface = res.Surfaces[i];
    ovrSurfaceDef *surfaceDef =
        const_cast<ovrSurfaceDef *>(drawSurface.surface);
    surfaceDef->graphicsCommand.Program = shader->GetProgram();
  }

  frameParms = vrapi_DefaultFrameParms(java, VRAPI_FRAME_INIT_DEFAULT,
                                       vrapi_GetTimeInSeconds(), nullptr);
  ovrFrameLayer &layer = frameParms.Layers[VRAPI_FRAME_LAYER_TYPE_WORLD];

  for (int eye = 0; eye < VRAPI_FRAME_LAYER_EYE_MAX; eye++) {
    res.FrameMatrices.EyeView[eye] = vrapi_GetEyeViewMatrix(
        &app->GetHeadModelParms(), &centerEyeViewMatrix, eye);
    res.FrameMatrices.EyeProjection[eye] = ovrMatrix4f_CreateProjectionFov(
        frame.FovX, frame.FovY, 0.0f, 0.0f, 1.0f, 0.0f);

    layer.Textures[eye].ColorTextureSwapChain =
        frame.ColorTextureSwapChain[eye];
    layer.Textures[eye].DepthTextureSwapChain =
        frame.DepthTextureSwapChain[eye];
    layer.Textures[eye].TextureSwapChainIndex = frame.TextureSwapChainIndex;

    layer.Textures[eye].TexCoordsFromTanAngles = frame.TexCoordsFromTanAngles;
    layer.Textures[eye].HeadPose = frame.Tracking.HeadPose;
  }
  layer.Flags |= VRAPI_FRAME_LAYER_FLAG_CHROMATIC_ABERRATION_CORRECTION;

  res.FrameParms = (ovrFrameParmsExtBase *)&frameParms;

  return res;
}

const ovrMatrix4f &GearVRActivity::GetCenterEyeViewMatrix() {
  return centerEyeViewMatrix;
}

void GearVRActivity::HandleInput(const VrInput &input) {

  // process input events first because this mirrors the behavior when
  // OnKeyEvent was
  // a virtual function on VrAppInterface and was called by VrAppFramework.
  for (int i = 0; i < input.NumKeyEvents; i++) {

    const int keyCode = input.KeyEvents[i].KeyCode;
    const int repeatCount = input.KeyEvents[i].RepeatCount;
    const KeyEventType eventType = input.KeyEvents[i].EventType;

    // Key event handling
    if (OnKeyEvent(keyCode, repeatCount, eventType))
      continue; // consumed the event

    // If nothing consumed the key and it's a short-press of the back key, then
    // exit the application to OculusHome.
    if (keyCode == OVR_KEY_BACK && eventType == KEY_EVENT_SHORT_PRESS) {
      vrapi_ShowSystemUI(app->GetJava(), VRAPI_SYS_UI_CONFIRM_QUIT_MENU);
    }
  }
}

bool GearVRActivity::OnKeyEvent(const int keyCode, const int repeatCount,
                                const KeyEventType eventType) {
  JNIEnv *jni = app->GetJava()->Env;
  return jni->CallBooleanMethod(app->GetJava()->ActivityObject,
                                onKeyEventMethodID, keyCode, repeatCount,
                                eventType);
}
} // namespace mgn

/*
 * JNI methods
 */

extern "C" {

jlong Java_org_meganekkovr_GearVRActivity_nativeSetAppInterface(
    JNIEnv *jni, jclass clazz, jobject activity, jstring fromPackageName,
    jstring commandString, jstring uriString) {

  LOG("nativeSetAppInterface");
  return (new mgn::GearVRActivity())
      ->SetActivity(jni, clazz, activity, fromPackageName, commandString,
                    uriString);
}

void Java_org_meganekkovr_GearVRActivity_setClearColorBuffer(
    JNIEnv *jni, jclass clazz, jlong appPtr, jboolean clearColorBuffer) {
  mgn::GearVRActivity *activity =
      (mgn::GearVRActivity *)((App *)appPtr)->GetAppInterface();
  activity->SetClearColorBuffer(clearColorBuffer);
}

jboolean Java_org_meganekkovr_GearVRActivity_getClearColorBuffer(JNIEnv *jni,
                                                                 jclass clazz,
                                                                 jlong appPtr) {
  mgn::GearVRActivity *activity =
      (mgn::GearVRActivity *)((App *)appPtr)->GetAppInterface();
  return activity->GetClearColorBuffer();
}

void Java_org_meganekkovr_GearVRActivity_setClearColor(JNIEnv *jni,
                                                       jclass clazz,
                                                       jlong appPtr, jfloat r,
                                                       jfloat g, jfloat b,
                                                       jfloat a) {
  mgn::GearVRActivity *activity =
      (mgn::GearVRActivity *)((App *)appPtr)->GetAppInterface();
  activity->SetClearColor(Vector4f(r, g, b, a));
}

void Java_org_meganekkovr_GearVRActivity_getClearColor(JNIEnv *jni,
                                                       jclass clazz,
                                                       jlong appPtr,
                                                       jfloatArray clearColor) {
  mgn::GearVRActivity *activity =
      (mgn::GearVRActivity *)((App *)appPtr)->GetAppInterface();
  Vector4f color = activity->GetClearColor();
  mgn::FillElementsUnSafe(jni, clearColor, color);
}

void Java_org_meganekkovr_GearVRActivity_addSurfaceDef(JNIEnv *jni,
                                                       jclass clazz,
                                                       jlong entityPtr,
                                                       jlong surfacesPtr) {

  mgn::Entity *entity = reinterpret_cast<mgn::Entity *>(entityPtr);
  Array<ovrDrawSurface> *surfaces =
      reinterpret_cast<Array<ovrDrawSurface> *>(surfacesPtr);
  ovrSurfaceDef *surfaceDef = entity->GetSurfaceDef();

  // Only draw if surfaceDef is valid
  if (surfaceDef && surfaceDef->graphicsCommand.UniformData[0].Data &&
      surfaceDef->graphicsCommand.UniformData[1].Data &&
      surfaceDef->graphicsCommand.UniformData[2].Data) {
    surfaces->PushBack(
        ovrDrawSurface(entity->GetWorldModelMatrix(), surfaceDef));
  }
}

} // extern "C"