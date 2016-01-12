/* Copyright 2015 eje inc.
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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.eje_c.meganekko.texture.BitmapTexture;
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.texture.Texture;
import com.eje_c.meganekko.VrContext;

/**
 * A {@linkplain SceneObject scene object} for rendering simple text.
 */
public class TextSceneObject extends SceneObject {

    private float mTextScale = 0.005f; // Scale for Bitmap size to Object size
    private String mText;
    private float mFixedWidth;
    private float mFixedHeight;
    private float mWidth;
    private float mHeight;
    private Paint mPaint = new Paint();
    private boolean mAutoUpdate = true;
    private Paint mAntiAliasPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);

    public TextSceneObject(VrContext vrContext) {
        super(vrContext);
        mPaint.setTextSize(20.0f);
    }

    /**
     * String to Bitmap based on
     * http://stackoverflow.com/questions/8799290/convert-string-text-to-bitmap
     *
     * @param text  String
     * @param paint Paint
     * @return Text image Bitmap
     */
    private static Bitmap textAsBitmap(String text, Paint paint) {

        paint.setTextAlign(Paint.Align.LEFT);

        int width = 1;
        int height = 1;
        String[] lines = text.split("\n");
        int lineCount = lines.length;
        int[] heights = new int[lineCount];
        float[] baselines = new float[lineCount];

        for (int i = 0; i < lineCount; i++) {

            int w = (int) (paint.measureText(lines[i]) + 0.5f); // round
            width = Math.max(w, width);

            baselines[i] = (int) (-paint.ascent() + 0.5f); // ascent() is
            // negative
            int h = (int) (baselines[i] + paint.descent() + 0.5f);

            height += h;
            heights[i] = h;
        }

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        int y = 0;
        for (int i = 0; i < lineCount; i++) {
            String line = lines[i];

            canvas.drawText(line, 0, y + baselines[i], paint);
            y += heights[i];
        }

        return image;
    }

    public void update() {

        if (mText == null)
            return;

        Bitmap textImage = textAsBitmap(mText, mPaint);
        int textWidth = textImage.getWidth();
        int textHeight = textImage.getHeight();
        float scaledWidth = textWidth * mTextScale;
        float scaledHeight = textHeight * mTextScale;
        Bitmap textureImage;
        float bmpAspect = scaledWidth / scaledHeight;

        if (mFixedWidth > 0.0f && mFixedHeight <= 0.0f) {

            // auto height
            mWidth = mFixedWidth;
            mHeight = mFixedWidth / bmpAspect;
            textureImage = textImage;

        } else if (mFixedHeight > 0.0f && mFixedWidth <= 0.0f) {

            // auto width
            mWidth = mFixedHeight * bmpAspect;
            mHeight = mFixedHeight;
            textureImage = textImage;

        } else if (mFixedWidth <= 0.0f && mFixedHeight <= 0.0f) {

            // auto width and height
            mWidth = scaledWidth;
            mHeight = scaledHeight;
            textureImage = textImage;

        } else {

            // width and height are specified
            mWidth = mFixedWidth;
            mHeight = mFixedHeight;

            int textureWidth = (int) (mFixedWidth / mTextScale);
            int textureHeight = (int) (mFixedHeight / mTextScale);
            textureImage = Bitmap.createBitmap(textureWidth, textureHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(textureImage);

            // text bitmap is smaller than texture bitmap
            if (textWidth < textureWidth && textHeight < textureHeight) {

                canvas.drawBitmap(textImage, 0, 0, null);

            } else { // text bitmap is larger than texture bitmap

                Matrix m = new Matrix();
                float scaleX = (float) textureWidth / (float) textWidth;
                float scaleY = (float) textureHeight / (float) textHeight;
                float scale = Math.min(scaleX, scaleY);
                m.setScale(scale, scale);
                canvas.drawBitmap(textImage, m, mAntiAliasPaint);

            }
        }

        RenderData renderData = getRenderData();
        Material material;

        if (renderData == null) {
            renderData = new RenderData(getVrContext());

            material = new Material(getVrContext());
            renderData.setMaterial(material);

            attachRenderData(renderData);

        } else {
            material = renderData.getMaterial();
        }

        Mesh mesh = getVrContext().createQuad(mWidth, mHeight);
        renderData.setMesh(mesh);

        Texture texture = new BitmapTexture(getVrContext(), textureImage);
        material.setMainTexture(texture);
    }

    public void setFixedHeight(float fixedHeight) {

        if (mFixedHeight == fixedHeight)
            return;

        this.mFixedHeight = fixedHeight;

        if (mAutoUpdate)
            update();
    }

    public void setFixedWidth(float fixedWidth) {

        if (mFixedWidth == fixedWidth)
            return;

        this.mFixedWidth = fixedWidth;

        if (mAutoUpdate)
            update();
    }

    public void resetFixedSize() {

        if (mFixedWidth == 0.0f && mFixedHeight == 0.0f)
            return;

        mFixedWidth = mFixedHeight = 0.0f;

        if (mAutoUpdate)
            update();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {

        // Do nothing if same text is passed twice for performance.
        if (mText != null && mText.equals(text))
            return;

        this.mText = text;

        if (mAutoUpdate)
            update();
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint paint) {

        if (mPaint == paint)
            return;

        this.mPaint = paint;

        if (mAutoUpdate)
            update();
    }

    public float getTextScale() {
        return mTextScale;
    }

    public void setTextScale(float scale) {

        if (mTextScale == scale)
            return;

        this.mTextScale = scale;

        if (mAutoUpdate)
            update();
    }

    public boolean isAutoUpdate() {
        return mAutoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.mAutoUpdate = autoUpdate;

        if (autoUpdate)
            update();
    }

    public float getWidth() {
        return mWidth;
    }

    /*
     * Handy methods for text appearance.
     */

    public float getHeight() {
        return mHeight;
    }

    public float getTextSize() {
        return mPaint.getTextSize();
    }

    public void setTextSize(float textSize) {
        mPaint.setTextSize(textSize);

        if (mAutoUpdate)
            update();
    }

    public float getColor() {
        return mPaint.getColor();
    }

    public void setColor(int color) {
        mPaint.setColor(color);

        if (mAutoUpdate)
            update();
    }
}
