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

package com.eje_c.meganekko;

import android.graphics.SurfaceTexture;

public class SurfaceTextureTexture extends Texture {
    public SurfaceTextureTexture(VrContext vrContext) {
        super(vrContext);
    }

    public SurfaceTexture getSurfaceTexture() {
        return getSurfaceTexture(getNative());
    }

    @Override
    protected native long initNativeInstance();

    private native SurfaceTexture getSurfaceTexture(long nativePtr);
}
