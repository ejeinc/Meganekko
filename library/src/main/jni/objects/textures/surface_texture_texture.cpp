/*
 * Copyright 2015 eje inc.
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

#include <jni.h>
#include "objects/textures/surface_texture_texture.h"

namespace mgn {

    // JNI API
    extern "C" {

    jlong Java_com_eje_1c_meganekko_SurfaceTextureTexture_initNativeInstance(JNIEnv *jni, jobject obj) {
        return reinterpret_cast<jlong>(new SurfaceTextureTexture(jni));
    }

    jobject Java_com_eje_1c_meganekko_SurfaceTextureTexture_getSurfaceTexture(
            JNIEnv *env, jobject obj, jlong nativePtr) {
        SurfaceTextureTexture *texture = reinterpret_cast<SurfaceTextureTexture *>(nativePtr);
        return texture->getSurfaceTexture();
    }

    } // extern "C"

    SurfaceTextureTexture::SurfaceTextureTexture(JNIEnv * jni) : Texture(NULL) {
        MovieTexture = new SurfaceTexture(jni);
    }

    SurfaceTextureTexture::~SurfaceTextureTexture() {
        delete MovieTexture;
        MovieTexture = NULL;
    }
}
