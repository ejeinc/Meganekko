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
#include "SurfaceRendererComponent.h"
#include "Shader.h"
#include "Kernel/OVR_LogUtils.h"

namespace mgn {

SurfaceRendererComponent::SurfaceRendererComponent(JNIEnv *jni)
    : jni(jni), surfaceTexture(jni), opacity(1.0f),
     useChromaKey(false), chromaKeyColor(0.0f) {

  // Reference: SurfaceTexture.h
  // Get Surface class
  static const char *className = "android/view/Surface";
  const jclass surfaceClass = jni->FindClass(className);
  if (surfaceClass == nullptr) {
    FAIL("FindClass( %s ) failed", className);
  }

  // Get constructor
  const jmethodID constructor = jni->GetMethodID(
      surfaceClass, "<init>", "(Landroid/graphics/SurfaceTexture;)V");
  if (constructor == nullptr) {
    FAIL("GetMethodID( <init> ) failed");
  }

  // Create instance
  jobject obj =
      jni->NewObject(surfaceClass, constructor, surfaceTexture.GetJavaObject());
  if (obj == nullptr) {
    FAIL("NewObject() failed");
  }

  // Store to field
  surface = jni->NewGlobalRef(obj);
  if (surface == nullptr) {
    FAIL("NewGlobalRef() failed");
  }

  // Now that we have a globalRef, we can free the localRef
  jni->DeleteLocalRef(obj);

  texture =
      GlTexture(surfaceTexture.GetTextureId(), GL_TEXTURE_EXTERNAL_OES, 0, 0);

  programMatrices[0] = Matrix4f::Identity();
  programMatrices[1] = Matrix4f::Identity();
}

SurfaceRendererComponent::~SurfaceRendererComponent() {
  LOG("Delete SurfaceRendererComponent");

  if (surface) {

    // Call Surface.release()
    static const char *className = "android/view/Surface";
    const jclass surfaceClass = jni->FindClass(className);
    if (surfaceClass == nullptr) {
      FAIL("FindClass( %s ) failed", className);
    }

    const jmethodID releaseMethodID =
        jni->GetMethodID(surfaceClass, "release", "()V");
    if (releaseMethodID == nullptr) {
      FAIL("GetMethodID( release ) failed");
    }

    jni->CallVoidMethod(surface, releaseMethodID);

    // Delete reference for garbage collection
    jni->DeleteGlobalRef(surface);
    surface = nullptr;
  }
}

jobject SurfaceRendererComponent::GetSurfaceTexture() {
  return surfaceTexture.GetJavaObject();
}

jobject SurfaceRendererComponent::GetSurface() { return surface; }

GlTexture &SurfaceRendererComponent::GetTexture() { return texture; }
void SurfaceRendererComponent::SetOpacity(float opacity) {
  this->opacity = opacity;
}
float &SurfaceRendererComponent::GetOpacity() { return opacity; }

void SurfaceRendererComponent::SetUseChromaKey(bool useChromaKey) {
  this->useChromaKey = useChromaKey;
}

bool &SurfaceRendererComponent::GetUseChromaKey() {
   return useChromaKey;
}

void SurfaceRendererComponent::SetChromaKeyColor(const Vector3f &chromaKeyColor) {
  this->chromaKeyColor = chromaKeyColor;
}

Vector3f &SurfaceRendererComponent::GetChromaKeyColor() {
  return chromaKeyColor;
}

void SurfaceRendererComponent::SetStereoMode(StereoMode stereoMode) {
  this->stereoMode = stereoMode;

  switch (stereoMode) {
  case NORMAL:
    programMatrices[0] = Matrix4f::Identity();
    programMatrices[1] = Matrix4f::Identity();
    break;
  case TOP_BOTTOM:
    programMatrices[0] = texM_topHalf;
    programMatrices[1] = texM_bottomHalf;
    break;
  case BOTTOM_TOP:
    programMatrices[0] = texM_bottomHalf;
    programMatrices[1] = texM_topHalf;
    break;
  case LEFT_RIGHT:
    programMatrices[0] = texM_leftHalf;
    programMatrices[1] = texM_rightHalf;
    break;
  case RIGHT_LEFT:
    programMatrices[0] = texM_rightHalf;
    programMatrices[1] = texM_leftHalf;
    break;
  case TOP_ONLY:
    programMatrices[0] = texM_topHalf;
    programMatrices[1] = texM_topHalf;
    break;
  case BOTTOM_ONLY:
    programMatrices[0] = texM_bottomHalf;
    programMatrices[1] = texM_bottomHalf;
    break;
  case LEFT_ONLY:
    programMatrices[0] = texM_leftHalf;
    programMatrices[1] = texM_leftHalf;
    break;
  case RIGHT_ONLY:
    programMatrices[0] = texM_rightHalf;
    programMatrices[1] = texM_rightHalf;
    break;
  }
}

SurfaceRendererComponent::StereoMode SurfaceRendererComponent::GetStereoMode() {
  return stereoMode;
}
}

/*
 * JNI methods
 */

extern "C" {

jlong Java_org_meganekkovr_SurfaceRendererComponent_newInstance(JNIEnv *jni,
                                                                jobject thiz) {
  return reinterpret_cast<jlong>(new mgn::SurfaceRendererComponent(jni));
}

jobject Java_org_meganekkovr_SurfaceRendererComponent_getSurfaceTexture(
    JNIEnv *jni, jobject thiz, jlong nativePtr) {

  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);

  return sur->GetSurfaceTexture();
}

jobject Java_org_meganekkovr_SurfaceRendererComponent_getSurface(
    JNIEnv *jni, jobject thiz, jlong nativePtr) {

  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);

  return sur->GetSurface();
}

void Java_org_meganekkovr_SurfaceRendererComponent_setEntityTexture(
    JNIEnv *jni, jobject thiz, jlong entityPtr, jlong nativePtr) {

  mgn::Entity *entity = reinterpret_cast<mgn::Entity *>(entityPtr);
  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);

  ovrSurfaceDef *surfaceDef = entity->GetOrCreateSurfaceDef();
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_TEXM].Data =
      &sur->programMatrices[0];
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_TEXM].Count = 2;
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_OPACITY].Data =
      &sur->GetOpacity();
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_TEXTURE].Data =
      &sur->GetTexture();
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_USE_CHROMA_KEY].Data =
      &sur->GetUseChromaKey();
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_CHROMA_KEY_COLOR].Data =
      &sur->GetChromaKeyColor();
}

void Java_org_meganekkovr_SurfaceRendererComponent_removeEntityTexture(
    JNIEnv *jni, jobject thiz, jlong entityPtr, jlong nativePtr) {

  mgn::Entity *entity = reinterpret_cast<mgn::Entity *>(entityPtr);
  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);

  ovrSurfaceDef *surfaceDef = entity->GetOrCreateSurfaceDef();
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_TEXM].Data =
      nullptr;
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_OPACITY].Data =
      nullptr;
  surfaceDef->graphicsCommand.UniformData[mgn::Shader::PARM_TEXTURE].Data =
      nullptr;
}

void Java_org_meganekkovr_SurfaceRendererComponent_setOpacity(JNIEnv *jni,
                                                              jobject thiz,
                                                              jlong nativePtr,
                                                              jfloat opacity) {

  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);
  sur->SetOpacity(opacity);
}

void Java_org_meganekkovr_SurfaceRendererComponent_setStereoMode(
    JNIEnv *jni, jobject thiz, jlong nativePtr, jint stereoMode) {

  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);
  sur->SetStereoMode(
      static_cast<mgn::SurfaceRendererComponent::StereoMode>(stereoMode));
}

void Java_org_meganekkovr_SurfaceRendererComponent_setUseChromaKey(
    JNIEnv *jni, jobject thiz, jlong nativePtr, jboolean useChromaKey) {

  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);
  sur->SetUseChromaKey(useChromaKey);
}

jboolean Java_org_meganekkovr_SurfaceRendererComponent_getUseChromaKey(
    JNIEnv *jni, jobject thiz, jlong nativePtr) {

  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);
  return sur->GetUseChromaKey();
}

void Java_org_meganekkovr_SurfaceRendererComponent_setChromaKeyColor(
    JNIEnv *jni, jobject thiz, jlong nativePtr, jfloat r, jfloat g, jfloat b) {

  mgn::SurfaceRendererComponent *sur =
      reinterpret_cast<mgn::SurfaceRendererComponent *>(nativePtr);
  sur->SetChromaKeyColor(Vector3f(r, g, b));
}
} // extern "C"