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

/***************************************************************************
 * JNI
 ***************************************************************************/

#include <jni.h>

#ifndef GL_EXT_texture_filter_anisotropic
#define GL_EXT_texture_filter_anisotropic 1
#define GL_TEXTURE_MAX_ANISOTROPY_EXT     0x84FE
#define GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT 0x84FF
#endif /* GL_EXT_texture_filter_anisotropic */

#ifndef GL_ES_VERSION_3_0
#include "GLES3/gl3.h"
#endif
#include "util/gvr_log.h"

#include "engine/memory/gl_delete.h"

namespace mgn {
extern "C" {
JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_TextureParameters_getMaxAnisotropicValue(JNIEnv * env, jobject obj);
}
;

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_TextureParameters_getMaxAnisotropicValue(JNIEnv * env, jobject obj) {
    float aniso_max_value = 0.0f;
    glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &aniso_max_value);
    return (int)aniso_max_value;
}
}
