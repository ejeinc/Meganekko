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
#ifndef GEARVR_ACTIVITY_H
#define GEARVR_ACTIVITY_H

#include "App.h"
#include "Shader.h"

using namespace OVR;

namespace mgn {
class GearVRActivity : public VrAppInterface {
public:
  GearVRActivity();
  ~GearVRActivity();

  virtual void Configure(ovrSettings &settings);
  virtual void EnteredVrMode(const ovrIntentType intentType,
                             const char *intentFromPackage,
                             const char *intentJSON, const char *intentURI);
  virtual void LeavingVrMode();
  virtual ovrFrameResult Frame(const ovrFrameInput &vrFrame);
  const ovrMatrix4f &GetCenterEyeViewMatrix();

private:
  bool hmdMounted;
  ovrMatrix4f centerEyeViewMatrix;
  ovrFrameParms frameParms;
  jmethodID updateMethodID;
  jmethodID collectSurfaceDefsMethodID;
  jmethodID onKeyEventMethodID;
  jmethodID onHmdMountedMethodID;
  jmethodID onHmdUnmountedMethodID;
  Shader *shader;

  inline jmethodID GetMethodID(const char *name, const char *signature);
  inline bool OnKeyEvent(const int keyCode, const int repeatCount,
                         const KeyEventType eventType);
  inline void HandleInput(const VrInput &input);
};
}
#endif