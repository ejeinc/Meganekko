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

#ifndef DUMMY_TEXTURE_H_
#define DUMMY_TEXTURE_H_

#include <jni.h>
#include "objects/textures/texture.h"
#include "SurfaceTexture.h"

#define __gl2_h_
#ifndef GL_ES_VERSION_3_0

#include <GLES3/gl3.h>

#endif

#include <GLES2/gl2ext.h>

using namespace OVR;

namespace mgn {

    class SurfaceTextureTexture : public Texture {
    public:
        SurfaceTextureTexture(JNIEnv *jni);

        ~SurfaceTextureTexture();

        GLuint getId() const {
            return MovieTexture->GetTextureId();
        }

        GLenum getTarget() const {
            return GL_TEXTURE_EXTERNAL_OES;
        }

        jobject getSurfaceTexture() {
            return MovieTexture->GetJavaObject();
        }

    private:
        SurfaceTextureTexture(const SurfaceTextureTexture &another);

        SurfaceTextureTexture(SurfaceTextureTexture &&another);

        SurfaceTextureTexture &operator=(const SurfaceTextureTexture &another);

        SurfaceTextureTexture &operator=(SurfaceTextureTexture &&another);

        SurfaceTexture *MovieTexture;
    };
}
#endif
