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

/***************************************************************************
 * Links textures and shaders.
 ***************************************************************************/

#ifndef MATERIAL_H_
#define MATERIAL_H_

#include "HybridObject.h"

using namespace OVR;

namespace mgn {

class Material : public HybridObject {
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

  explicit Material(JNIEnv *jni);

  jobject GetSurfaceTexture();

  StereoMode GetStereoMode() const;

  void SetStereoMode(StereoMode stereoMode);

  const GlTexture &GetGlTexture();

  const GlTexture &GetGlTexture() const;

private:
  Material(const Material &material);
  Material(Material &&material);
  Material &operator=(const Material &material);
  Material &operator=(Material &&material);

private:
  SurfaceTexture surfaceTexture;
  StereoMode Mode;
  GlTexture glTexture;
};
}
#endif
