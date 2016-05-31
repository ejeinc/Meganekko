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

package com.eje_c.meganekko.texture;

import android.graphics.SurfaceTexture;

import com.eje_c.meganekko.Frame;
import com.eje_c.meganekko.HybridObject;

/**
 * Texture is surface image of {@link com.eje_c.meganekko.Material}.
 */
public abstract class Texture extends HybridObject {

    private final SurfaceTexture mSurfaceTexture;

    public Texture() {
        this.mSurfaceTexture = getSurfaceTexture(getNative());
    }

    @Override
    protected native long initNativeInstance();

    private static native SurfaceTexture getSurfaceTexture(long nativePtr);

    /**
     * Get a SurfaceTexture for rendering.
     *
     * @return SurfaceTexture.
     */
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    /**
     * Release native resources.
     */
    public void release() {
        mSurfaceTexture.release();
    }

    /**
     * Called in every frame for update texture image.
     *
     * @param vrFrame Frame information.
     */
    public abstract void update(Frame vrFrame);
}
