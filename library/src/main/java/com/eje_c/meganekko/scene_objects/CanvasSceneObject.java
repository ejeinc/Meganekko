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

package com.eje_c.meganekko.scene_objects;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrFrame;

/**
 * A {@linkplain SceneObject scene object} that renders like a standard Android
 * Views. You have to call {@link #setCanvasSize(int, int)} to set Canvas size.
 * Actual drawing is done with subclass of {@link OnDrawListener}.
 */
public class CanvasSceneObject extends SceneObject {

    private final SurfaceTexture mSurfaceTexture;
    private OnDrawListener mOnDrawListener;

    public CanvasSceneObject() {

        Material material = new Material();
        mSurfaceTexture = material.getSurfaceTexture();

        RenderData renderData = new RenderData();
        renderData.setMaterial(material);

        attachRenderData(renderData);
    }

    /**
     * Must be called before start drawing.
     *
     * @param width  Internal texture width. This value can be get with
     *               {@code Canvas#getWidth()} on drawing.
     * @param height Internal texture height. This value can be get with
     *               {@code Canvas#getHeight()} on drawing.
     */
    public void setCanvasSize(int width, int height) {
        mSurfaceTexture.setDefaultBufferSize(width, height);
    }

    @SuppressLint("WrongCall")
    @Override
    public void update(VrFrame vrFrame) {

        if (mOnDrawListener != null && mOnDrawListener.isDirty()) {

            Canvas canvas = null;
            Surface surface = null;

            try {
                surface = new Surface(mSurfaceTexture);
                canvas = surface.lockCanvas(null);
                mOnDrawListener.onDraw(this, canvas, vrFrame);
            } finally {
                if (surface != null) {
                    if (canvas != null) {
                        surface.unlockCanvasAndPost(canvas);
                    }
                    surface.release();
                }
            }

            mSurfaceTexture.updateTexImage();
        }

        super.update(vrFrame);
    }

    /**
     * Get an {@code OnDrawListener} for rendering.
     *
     * @return
     */
    public OnDrawListener getOnDrawListener() {
        return mOnDrawListener;
    }

    /**
     * Set an {@code OnDrawListener} for rendering. Pass null to unregister
     * listener.
     *
     * @param onDrawListener
     */
    public void setOnDrawListener(OnDrawListener onDrawListener) {
        this.mOnDrawListener = onDrawListener;
    }

    /**
     * Equivalent call to {@code setOnDrawListener(null)}.
     */
    public void clearOnDrawListener() {
        setOnDrawListener(null);
    }

    public interface OnDrawListener {
        /**
         * Called before onDraw is called. If this method returns true, canvas
         * is refreshed with onDraw method. If this method returns false, onDraw
         * will not be called.
         *
         * @return Refreshing canvas is required.
         */
        boolean isDirty();

        /**
         * Called at each frame update. Use {@code VrFrame#getDeltaSeconds()}
         * and {@code VrFrame#getPredictedDisplayTimeInSeconds()} to implement
         * time based animation.
         *
         * @param object  Target object.
         * @param canvas  Rendering canvas.
         * @param vrFrame Current frame information passed from native framework.
         */
        void onDraw(CanvasSceneObject object, Canvas canvas, VrFrame vrFrame);
    }
}
