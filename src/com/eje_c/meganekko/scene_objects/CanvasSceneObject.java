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

import com.eje_c.meganekko.ExternalTexture;
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Material.ShaderType;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.VrFrame;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.view.Surface;

/**
 * A {@linkplain SceneObject scene object} that renders like a standard Android
 * Views.
 */
public class CanvasSceneObject extends SceneObject {

    private final Surface mSurface;
    private final SurfaceTexture mSurfaceTexture;
    private OnDrawListener mOnDrawListener;

    public CanvasSceneObject(VrContext vrContext) {
        super(vrContext);

        // Initialize an OpenGL texture
        ExternalTexture texture = new ExternalTexture(vrContext);

        Material material = new Material(vrContext, ShaderType.OES.ID);
        material.setMainTexture(texture);

        RenderData renderData = new RenderData(vrContext);
        renderData.setMaterial(material);

        attachRenderData(renderData);

        mSurfaceTexture = new SurfaceTexture(texture.getId());
        mSurface = new Surface(mSurfaceTexture);
    }

    /**
     * Must be called before start drawing.
     * 
     * @param width
     *            Internal texture width. This value can be get with
     *            {@code Canvas#getWidth()} on drawing.
     * @param height
     *            Internal texture height. This value can be get with
     *            {@code Canvas#getHeight()} on drawing.
     */
    public void setCanvasSize(int width, int height) {
        mSurfaceTexture.setDefaultBufferSize(width, height);
    }

    @SuppressLint("WrongCall")
    @Override
    public void onEvent(VrFrame vrFrame) {

        if (mOnDrawListener != null) {

            Canvas canvas = null;

            try {
                canvas = mSurface.lockCanvas(null);
                mOnDrawListener.onDraw(this, canvas, vrFrame);
            } finally {
                if (canvas != null)
                    mSurface.unlockCanvasAndPost(canvas);
            }

            mSurfaceTexture.updateTexImage();
        }

        super.onEvent(vrFrame);
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
     * Get an {@code OnDrawListener} for rendering.
     * 
     * @return
     */
    public OnDrawListener getOnDrawListener() {
        return mOnDrawListener;
    }

    /**
     * Equivalent call to {@code setOnDrawListener(null)}.
     */
    public void clearOnDrawListener() {
        setOnDrawListener(null);
    }

    public interface OnDrawListener {
        /**
         * Called at each frame update. Use {@code VrFrame#getDeltaSeconds()}
         * and {@code VrFrame#getPredictedDisplayTimeInSeconds()} to implement
         * time based animation.
         * 
         * @param object
         *            Target object.
         * @param canvas
         *            Rendering canvas.
         * @param vrFrame
         *            Current frame information passed from native framework.
         */
        void onDraw(CanvasSceneObject object, Canvas canvas, VrFrame vrFrame);
    }
}
