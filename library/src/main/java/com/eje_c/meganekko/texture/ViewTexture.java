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
import android.view.View;
import android.view.ViewGroup;

import com.eje_c.meganekko.Frame;

/**
 * Texture which uses {@link View} for rendering.
 */
public class ViewTexture extends CanvasTexture {
    private View mView;

    public ViewTexture() {
    }

    public ViewTexture(View view) {
        setView(view);
    }

    public void setView(View view) {
        this.mView = view;

        view.measure(0, 0);
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();

        view.layout(0, 0, measuredWidth, measuredHeight);

        setWidth(measuredWidth);
        setHeight(measuredHeight);
    }

    public View getView() {
        return mView;
    }

    @Override
    public void update(Frame vrFrame) {

        if (isDirty(mView)) {
            invalidate();
        }

        super.update(vrFrame);
    }

    @Override
    protected void render(Canvas canvas, Frame vrFrame) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mView.draw(canvas);
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
}
