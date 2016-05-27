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

package com.eje_c.meganekko;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

public class Texture extends HybridObject {

    private final SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private CanvasRenderer mRenderer;
    private boolean mContinuesUpdate;

    public Texture() {
        this.mSurfaceTexture = getSurfaceTexture(getNative());
    }

    @Override
    protected native long initNativeInstance();

    private static native SurfaceTexture getSurfaceTexture(long nativePtr);

    public SurfaceTexture getSurfaceTexture() {
        return getSurfaceTexture(getNative());
    }

    void release() {
        mSurfaceTexture.release();
    }

    /**
     * Render with {@code Bitmap}.
     *
     * @param bitmap
     */
    public void set(Bitmap bitmap) {
        set(new DrawableRenderer(new BitmapDrawable(Resources.getSystem(), bitmap)));
    }

    /**
     * Render with {@code Drawable}.
     *
     * @param drawable
     */
    public void set(Drawable drawable) {
        set(new DrawableRenderer(drawable));
    }

    /**
     * Render with {@code View}.
     *
     * @param view
     */
    public void set(View view) {
        set(new ViewRenderer(view));
    }

    /**
     * Render with custom {@linkplain com.eje_c.meganekko.Texture.CanvasRenderer renderer}.
     *
     * @param renderer
     */
    public void set(CanvasRenderer renderer) {
        this.mContinuesUpdate = false;
        this.mRenderer = renderer;
    }

    /**
     * Render with {@code MediaPlayer}.
     *
     * @param mediaPlayer
     */
    public void set(MediaPlayer mediaPlayer) {
        this.mContinuesUpdate = true;

        Surface surface = new Surface(mSurfaceTexture);
        mediaPlayer.setSurface(surface);
        surface.release();
    }

    /**
     * Retrieve CanvasRenderer.
     *
     * @return CanvasRenderer
     */
    public CanvasRenderer getRenderer() {
        return mRenderer;
    }

    /**
     * Called in every frame for update texture image.
     *
     * @param vrFrame
     */
    public void update(Frame vrFrame) {
        if (mRenderer != null) {

            if (mRenderer.isDirty()) {

                mSurfaceTexture.setDefaultBufferSize(mRenderer.getWidth(), mRenderer.getHeight());

                if (mSurface == null) {
                    mSurface = new Surface(mSurfaceTexture);
                }

                Canvas canvas = mSurface.lockCanvas(null);
                if (canvas != null) {
                    mRenderer.render(canvas, vrFrame);
                    mSurface.unlockCanvasAndPost(canvas);
                }

                mSurfaceTexture.updateTexImage();
            }

        } else if (mContinuesUpdate) {
            mSurfaceTexture.updateTexImage();
        }
    }

    /**
     * Interface for custom texture rendering.
     */
    public interface CanvasRenderer {
        /**
         * Do rendering with {@code Canvas}.
         *
         * @param canvas
         * @param vrFrame
         */
        void render(Canvas canvas, Frame vrFrame);

        /**
         * @return {@code Canvas}'s width
         */
        int getWidth();

        /**
         * @return {@code Canvas}'s height
         */
        int getHeight();

        /**
         * Get if redrawing is required.
         *
         * @return return true if redrawing is required.
         */
        boolean isDirty();
    }

    /**
     * Basic renderer for Drawable.
     */
    public static class DrawableRenderer implements CanvasRenderer {

        private final Drawable mDrawable;
        private boolean mDirty = true;

        private DrawableRenderer(Drawable drawable) {
            this.mDrawable = drawable;
        }

        @Override
        public void render(Canvas canvas, Frame vrFrame) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
            mDrawable.draw(canvas);
            mDirty = false;
        }

        @Override
        public int getWidth() {
            return mDrawable.getIntrinsicWidth();
        }

        @Override
        public int getHeight() {
            return mDrawable.getIntrinsicHeight();
        }

        @Override
        public boolean isDirty() {
            return mDirty;
        }

        public Drawable getDrawable() {
            return mDrawable;
        }
    }

    /**
     * Basic renderer for View.
     */
    public static class ViewRenderer implements CanvasRenderer {

        private final View mView;

        private ViewRenderer(View view) {
            this.mView = view;
            view.measure(0, 0);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        /**
         * Check dirty state of view recursively.
         *
         * @param view Checked View.
         * @return Returns true if at least one View is dirty in hierarchy.
         */
        private static boolean isDirty(View view) {

            if (view.isDirty()) return true;

            // Apply this method to all children of view if view is ViewGroup
            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;

                for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
                    if (isDirty(viewGroup.getChildAt(i))) return true;
                }
            }

            return false;
        }

        @Override
        public void render(Canvas canvas, Frame vrFrame) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            mView.draw(canvas);
        }

        @Override
        public int getWidth() {
            return mView.getMeasuredWidth();
        }

        @Override
        public int getHeight() {
            return mView.getMeasuredHeight();
        }

        @Override
        public boolean isDirty() {
            return isDirty(mView);
        }

        public View getView() {
            return mView;
        }
    }
}
