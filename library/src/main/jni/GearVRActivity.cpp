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
#include "LookDetection.h"
#include "util/convert.h"
#include <jni.h>

using namespace OVR;

namespace mgn {

GearVRActivity::GearVRActivity() : hmdMounted(false) {}

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
}

void GearVRActivity::LeavingVrMode() {
  // LeavingVrMode
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
                                                     &frame.Tracking, NULL);

  jni->CallVoidMethod(java->ActivityObject, updateMethodID, (jlong)&frame);

  // Create frame result
  ovrFrameResult res;
  res.ClearColorBuffer = true;
  res.ClearColor = Vector4f(0, 0, 0, 1);
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
                                       vrapi_GetTimeInSeconds(), NULL);
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
      app->StartSystemActivity(PUI_CONFIRM_QUIT);
    } else if (keyCode == OVR_KEY_BACK && eventType == KEY_EVENT_LONG_PRESS) {
      app->StartSystemActivity(PUI_GLOBAL_MENU);
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

jboolean Java_org_meganekkovr_GearVRActivity_isLookingAt(JNIEnv *jni,
                                                         jclass clazz,
                                                         jlong appPtr,
                                                         jlong entityPtr,
                                                         jlong geoCompPtr) {
  mgn::GearVRActivity *activity =
      (mgn::GearVRActivity *)((App *)appPtr)->GetAppInterface();
  Matrix4f centerM = activity->GetCenterEyeViewMatrix();

  mgn::Entity *entity = reinterpret_cast<mgn::Entity *>(entityPtr);
  Matrix4f m = entity->GetWorldModelMatrix();

  mgn::GeometryComponent *geoComp =
      reinterpret_cast<mgn::GeometryComponent *>(geoCompPtr);
  GlGeometry geo = geoComp->GetGeometry();

  mgn::IntersectRayBoundsResult result =
      mgn::IntersectRayBounds(centerM, m, geo, false);
  return result.intersected;
}

void Java_org_meganekkovr_GearVRActivity_getCenterViewRotation(
    JNIEnv *jni, jclass clazz, jlong appPtr, jfloatArray values) {
  mgn::GearVRActivity *activity =
      (mgn::GearVRActivity *)((App *)appPtr)->GetAppInterface();
  Matrix4f centerM = activity->GetCenterEyeViewMatrix();
  Quatf orientation = Quatf(centerM.InvertedHomogeneousTransform());
  mgn::FillElementsUnSafe(jni, values, orientation);
}

void Java_org_meganekkovr_GearVRActivity_setCpuLevel(JNIEnv *jni, jclass clazz,
                                                     jlong appPtr,
                                                     jint cpuLevel) {
  App *app = reinterpret_cast<App *>(appPtr);
  app->SetCpuLevel(cpuLevel);
}

void Java_org_meganekkovr_GearVRActivity_setGpuLevel(JNIEnv *jni, jclass clazz,
                                                     jlong appPtr,
                                                     jint gpuLevel) {
  App *app = reinterpret_cast<App *>(appPtr);
  app->SetGpuLevel(gpuLevel);
}

void Java_org_meganekkovr_GearVRActivity_setShowFPS(JNIEnv *jni, jclass clazz,
                                                    jlong appPtr,
                                                    jboolean show) {
  App *app = reinterpret_cast<App *>(appPtr);
  app->SetShowFPS(show);
}

void Java_org_meganekkovr_GearVRActivity_showInfoText(JNIEnv *jni, jclass clazz,
                                                      jlong appPtr,
                                                      jfloat duration,
                                                      jstring text) {
  App *app = reinterpret_cast<App *>(appPtr);
  JavaUTFChars str(jni, text);
  app->ShowInfoText(duration, str.ToStr());
}
} // extern "C"