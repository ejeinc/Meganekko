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

#include "includes.h"

#ifndef TEXTURE_H_
#define TEXTURE_H_

#include "HybridObject.h"

using namespace OVR;

namespace mgn {

class Texture: public HybridObject {
public:

    explicit Texture(JNIEnv * jni) {
        surfaceTexture = new SurfaceTexture(jni);
    }

    ~Texture() {
        delete surfaceTexture;
        surfaceTexture = nullptr;
    }

    GLuint GetTextureId() const {
        return surfaceTexture->GetTextureId();
    }

    GLuint GetTextureId() {
        return surfaceTexture->GetTextureId();
    }

    jobject GetSurfaceTexture() const {
        return surfaceTexture->GetJavaObject();
    }

    jobject GetSurfaceTexture() {
        return surfaceTexture->GetJavaObject();
    }

private:
    Texture(const Texture& material);
    Texture(Texture&& material);
    Texture& operator=(const Texture& material);
    Texture& operator=(Texture&& material);

private:
    SurfaceTexture *surfaceTexture;
};
}
#endif
