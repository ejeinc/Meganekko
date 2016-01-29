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
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

public class Texture {

    private final SurfaceTexture surfaceTexture;
    private CanvasRenderer renderer;

    Texture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    public void set(Bitmap bitmap) {
        set(new DrawableRenderer(new BitmapDrawable(Resources.getSystem(), bitmap)));
    }

    public void set(Drawable drawable) {
        set(new DrawableRenderer(drawable));
    }

    public void set(View view) {
        set(new ViewRenderer(view));
    }

    public void set(CanvasRenderer renderer) {
        this.renderer = renderer;
    }

    public void update(Frame vrFrame) {
        if (renderer == null) return;
        
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

    public boolean isDirty() {
        return renderer != null && renderer.isDirty();
    }

    public interface CanvasRenderer {
        void render(Canvas canvas, Frame vrFrame);

        int getWidth();

        int getHeight();

        boolean isDirty();
    }

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
