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
#include "Shader.h"

namespace mgn {
static const char *ImageExternalDirectives =
    "#extension GL_OES_EGL_image_external : enable\n"
    "#extension GL_OES_EGL_image_external_essl3 : enable\n";

static const char *VertexShaderSrc =
    "uniform highp mat4 Texm[NUM_VIEWS];\n"
    "attribute vec4 Position;\n"
    "attribute vec2 TexCoord;\n"
    "varying  highp vec2 oTexCoord;\n"
    "void main() {\n"
    "   gl_Position = TransformVertex( Position );\n"
    "   oTexCoord = vec2( Texm[VIEW_ID] * vec4( TexCoord, 0, 1 ) );\n"
    "}\n";

static const char *FragmentShaderSrc =
    "uniform samplerExternalOES Texture0;\n"
    "uniform lowp float Opacity;\n"
    "varying highp vec2 oTexCoord;\n"
    "void main() {\n"
    "  gl_FragColor = Opacity * texture2D( Texture0, oTexCoord );\n"
    "  if (gl_FragColor.a < 0.001) {\n"
    "    discard;\n"
    "  }\n"
    "}\n";

Shader::Shader() {

  static ovrProgramParm parms[] = {
      {"Texm", ovrProgramParmType::FLOAT_MATRIX4},       // PARM_TEXM
      {"Opacity", ovrProgramParmType::FLOAT},            // PARM_OPACITY
      {"Texture0", ovrProgramParmType::TEXTURE_SAMPLED}, // PARM_TEXTURE
  };

  program = GlProgram::Build(NULL, VertexShaderSrc, ImageExternalDirectives,
                             FragmentShaderSrc, parms,
                             sizeof(parms) / sizeof(ovrProgramParm));
}

Shader::~Shader() { GlProgram::Free(program); }

GlProgram Shader::GetProgram() { return program; }
}