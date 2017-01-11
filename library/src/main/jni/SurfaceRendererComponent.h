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
#ifndef SURFACE_RENDERER_COMPONENT_H
#define SURFACE_RENDERER_COMPONENT_H

#include "Entity.h"
#include "HybridObject.h"
#include "SurfaceTexture.h"
#include "JavaSurface.h"

using namespace OVR;

namespace mgn {

class SurfaceRendererComponent : public HybridObject {
public:
  enum StereoMode {
    NORMAL = 0,
    TOP_BOTTOM,
    BOTTOM_TOP,
    LEFT_RIGHT,
    RIGHT_LEFT,
    TOP_ONLY,
    BOTTOM_ONLY,
    LEFT_ONLY,
    RIGHT_ONLY
  };

  SurfaceRendererComponent(JNIEnv *jni);
  ~SurfaceRendererComponent();

  jobject GetSurfaceTexture();
  jobject GetSurface();
  GlTexture &GetTexture();
  void SetOpacity(float opacity);
  float &GetOpacity();

  void SetUseChromaKey(bool useChromaKey);
  bool &GetUseChromaKey();
  
  void SetChromaKeyThreshold(float chromaKeyThreshold);
  float &GetChromaKeyThreshold();
  
  void SetChromaKeyBlend(float chromaKeyBlend);
  float &GetChromaKeyBlend();

  void SetChromaKeyColor(const Vector3f &chromaKeyColor);
  Vector3f &GetChromaKeyColor();

  void SetStereoMode(StereoMode stereoMode);
  StereoMode GetStereoMode();
  Matrix4f programMatrices[2]; // 0: For left eye, 1: For right eye
private:
  SurfaceTexture surfaceTexture;
  JavaSurface surface;
  GlTexture texture;
  float opacity;
  StereoMode stereoMode;
  bool useChromaKey;
  float chromaKeyThreshold;
  float chromaKeyBlend;
  Vector3f chromaKeyColor;
};

static Matrix4f texM_topHalf = Matrix4f( //
    1.0f, 0.0f, 0.0f, 0.0f,              //
    0.0f, 0.5f, 0.0f, 0.0f,              //
    0.0f, 0.0f, 1.0f, 0.0f,              //
    0.0f, 0.0f, 0.0f, 1.0f);
static Matrix4f texM_bottomHalf = Matrix4f( //
    1.0f, 0.0f, 0.0f, 0.0f,                 //
    0.0f, 0.5f, 0.0f, 0.5f,                 //
    0.0f, 0.0f, 1.0f, 0.0f,                 //
    0.0f, 0.0f, 0.0f, 1.0f);
static Matrix4f texM_leftHalf = Matrix4f( //
    0.5f, 0.0f, 0.0f, 0.0f,               //
    0.0f, 1.0f, 0.0f, 0.0f,               //
    0.0f, 0.0f, 1.0f, 0.0f,               //
    0.0f, 0.0f, 0.0f, 1.0f);
static Matrix4f texM_rightHalf = Matrix4f( //
    0.5f, 0.0f, 0.0f, 0.5f,                //
    0.0f, 1.0f, 0.0f, 0.0f,                //
    0.0f, 0.0f, 1.0f, 0.0f,                //
    0.0f, 0.0f, 0.0f, 1.0f);
}

#endif
