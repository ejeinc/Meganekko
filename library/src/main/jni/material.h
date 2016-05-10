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

#include "util/GL.h"
#include "HybridObject.h"

using namespace OVR;

namespace mgn {
class Color;

class Material: public HybridObject {
public:

    enum Side: int {
        FrontSide = 0, BackSide, DoubleSide
    };
    
    enum StereoMode {
        NORMAL = 0, TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT,
        TOP_ONLY, BOTTOM_ONLY, LEFT_ONLY, RIGHT_ONLY
    };

    explicit Material(JNIEnv * jni) {
        Mode = NORMAL;
        side = FrontSide;
        surfaceTexture = new SurfaceTexture(jni);
        color = Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        opacity = 1.0f;
    }

    ~Material() {
        delete surfaceTexture;
        surfaceTexture = NULL;
    }

    GLuint GetTextureId() const {
        return surfaceTexture->GetTextureId();
    }

    jobject GetSurfaceTexture() {
        return surfaceTexture->GetJavaObject();
    }

    StereoMode GetStereoMode() const {
        return Mode;
    }

    void SetStereoMode(StereoMode stereoMode) {
        Mode = stereoMode;
    }

    const Vector4f & GetColor() const {
        return color;
    }

    const Vector4f & GetColor() {
        return color;
    }

    void SetColor(const Vector4f & color) {
        this->color = color;
    }

    float GetOpacity() const {
        return opacity;
    }

    float GetOpacity() {
        return opacity;
    }

    void SetOpacity(const float opacity) {
        this->opacity = opacity;
    }
    
    int GetSide() const {
        return side;
    }
    
    int GetSide() {
        return side;
    }
    
    void SetSide(int side) {
        this->side = side;
    }

private:
    Material(const Material& material);
    Material(Material&& material);
    Material& operator=(const Material& material);
    Material& operator=(Material&& material);

private:
    SurfaceTexture *surfaceTexture;
    Vector4f color;
    float opacity;
    StereoMode Mode;
    int side;
};
}
#endif
