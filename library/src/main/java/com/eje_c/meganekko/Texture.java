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

public class Texture {

    private final SurfaceTexture surfaceTexture;

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

        surfaceTexture.setDefaultBufferSize(renderer.getWidth(), renderer.getHeight());

        Surface surface = new Surface(surfaceTexture);

        try {
            Canvas canvas = surface.lockCanvas(null);
            renderer.render(canvas);
            surface.unlockCanvasAndPost(canvas);
        } finally {
            surface.release();
        }

        update();
    }

    public void update() {
        surfaceTexture.updateTexImage();
    }

    public interface CanvasRenderer {
        void render(Canvas canvas);

        int getWidth();

        int getHeight();
    }

    private static class DrawableRenderer implements CanvasRenderer {

        private final Drawable drawable;

        private DrawableRenderer(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }

        @Override
        public int getWidth() {
            return drawable.getIntrinsicWidth();
        }

        @Override
        public int getHeight() {
            return drawable.getIntrinsicHeight();
        }
    }

    private static class ViewRenderer implements CanvasRenderer {

        private final View view;

        private ViewRenderer(View view) {
            this.view = view;
            view.measure(0, 0);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        @Override
        public void render(Canvas canvas) {
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
    }
}
