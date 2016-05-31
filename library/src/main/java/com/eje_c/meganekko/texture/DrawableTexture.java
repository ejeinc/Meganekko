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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.eje_c.meganekko.Frame;

/**
 * Texture which uses {@link Drawable} for rendering.
 */
public class DrawableTexture extends CanvasTexture {
    private Drawable drawable;

    public DrawableTexture() {
    }

    public DrawableTexture(Drawable drawable) {
        setDrawable(drawable);
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        setWidth(drawable.getIntrinsicWidth());
        setHeight(drawable.getIntrinsicHeight());
    }

    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    protected void render(Canvas canvas, Frame vrFrame) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
    }
}
