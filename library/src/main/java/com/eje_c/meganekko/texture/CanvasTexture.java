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

import android.graphics.Canvas;
import android.view.Surface;

import com.eje_c.meganekko.Frame;

/**
 * Texture which uses Android's standard {@link Canvas} for rendering.
 */
public abstract class CanvasTexture extends Texture {
    private boolean dirty = true;
    private boolean widthHeightChanged = true;
    private int width;
    private int height;
    private Surface mSurface;

    @Override
    public void update(Frame vrFrame) {
        if (dirty) {

            if (widthHeightChanged) {
                getSurfaceTexture().setDefaultBufferSize(width, height);
                widthHeightChanged = false;
            }

            if (mSurface == null) {
                mSurface = new Surface(getSurfaceTexture());
            }

            Canvas canvas = mSurface.lockCanvas(null);
            if (canvas != null) {
                render(canvas, vrFrame);
                mSurface.unlockCanvasAndPost(canvas);
            }

            getSurfaceTexture().updateTexImage();

            dirty = false;
        }
    }

    protected abstract void render(Canvas canvas, Frame vrFrame);

    public boolean isDirty() {
        return dirty;
    }

    public void invalidate() {
        dirty = true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
        widthHeightChanged = true;
    }

    public void setHeight(int height) {
        this.height = height;
        widthHeightChanged = true;
    }
}
