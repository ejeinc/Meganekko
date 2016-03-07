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

public class Texture {

    private final SurfaceTexture surfaceTexture;
    private CanvasRenderer renderer;
    private boolean continuesUpdate;

    Texture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
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
        this.continuesUpdate = false;
        this.renderer = renderer;
    }

    /**
     * Render with {@code MediaPlayer}.
     *
     * @param mediaPlayer
     */
    public void set(MediaPlayer mediaPlayer) {
        this.continuesUpdate = true;

        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);
        surface.release();
    }

    /**
     * Called in every frame for update texture image.
     *
     * @param vrFrame
     */
    public void update(Frame vrFrame) {
        if (renderer != null) {

            if (renderer.isDirty()) {

                surfaceTexture.setDefaultBufferSize(renderer.getWidth(), renderer.getHeight());

                Surface surface = new Surface(surfaceTexture);

                try {
                    Canvas canvas = surface.lockCanvas(null);
                    renderer.render(canvas, vrFrame);
                    surface.unlockCanvasAndPost(canvas);
                } finally {
                    surface.release();
                }

                surfaceTexture.updateTexImage();
            }

        } else if (continuesUpdate) {
            surfaceTexture.updateTexImage();
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

        private final Drawable drawable;
        private boolean dirty = true;

        private DrawableRenderer(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public void render(Canvas canvas, Frame vrFrame) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            dirty = false;
        }

        @Override
        public int getWidth() {
            return drawable.getIntrinsicWidth();
        }

        @Override
        public int getHeight() {
            return drawable.getIntrinsicHeight();
        }

        @Override
        public boolean isDirty() {
            return dirty;
        }
    }

    /**
     * Basic renderer for View.
     */
    public static class ViewRenderer implements CanvasRenderer {

        private final View view;

        private ViewRenderer(View view) {
            this.view = view;
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
            view.draw(canvas);
        }

        @Override
        public int getWidth() {
            return view.getMeasuredWidth();
        }

        @Override
        public int getHeight() {
            return view.getMeasuredHeight();
        }

        @Override
        public boolean isDirty() {
            return isDirty(view);
        }
    }
}
