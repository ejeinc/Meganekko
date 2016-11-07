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
#ifndef SHADER_H
#define SHADER_H

#include "GlProgram.h"

using namespace OVR;

namespace mgn {
class Shader {
public:
  Shader();
  ~Shader();

  GlProgram GetProgram();

  static const int PARM_TEXM = 0;
  static const int PARM_OPACITY = 1;
  static const int PARM_TEXTURE = 2;
  static const int PARM_USE_CHROMA_KEY = 3;
  static const int PARM_CHROMA_KEY_COLOR = 4;
  static const int PARM_CHROMA_KEY_THRESHOLD = 5;
  static const int PARM_CHROMA_KEY_BLEND = 6;

private:
  GlProgram program;
};
}
#endif