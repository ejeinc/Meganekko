/* Copyright 2015 Samsung Electronics Co., LTD
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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;

import com.eje_c.meganekko.utility.Colors;

/**
 * This is one of the key Meganekko classes: it holds texture, color, opacity information.
 */
public class Material extends HybridObject {

    private Texture mTexture;
    private CullFace mCullFace;

    /**
     * Create {@link Material} from {@code View}.
     *
     * @param view Textured {@code View}.
     * @return New {@link Material}.
     */
    public static Material from(View view) {
        Material material = new Material();
        material.texture().set(view);
        return material;
    }

    /**
     * Create {@link Material} from {@code Drawable}.
     *
     * @param drawable Textured {@code Drawable}.
     * @return New {@link Material}.
     */
    public static Material from(Drawable drawable) {
        Material material = new Material();
        material.texture().set(drawable);
        return material;
    }

    /**
     * Create {@link Material} from {@code Bitmap}.
     *
     * @param bitmap Textured {@code Bitmap}.
     * @return New {@link Material}.
     */
    public static Material from(Bitmap bitmap) {
        Material material = new Material();
        material.texture().set(bitmap);
        return material;
    }

    /**
     * Create {@link Material} from {@code MediaPlayer}.
     *
     * @param mediaPlayer Textured {@code MediaPlayer}.
     * @return New {@link Material}.
     */
    public static Material from(MediaPlayer mediaPlayer) {
        Material material = new Material();
        material.texture().set(mediaPlayer);
        return material;
    }

    private static native void setColor(long material, float r, float g, float b, float a);

    private static native void getColor(long material, float[] val);

    private static native void setOpacity(long material, float opacity);

    private static native float getOpacity(long material);

    private static native void setStereoMode(long material, int stereoMode);

    private static native void setSide(long material, int side);

    private static native void setTexture(long material, long texture);

    @Override
    protected native long initNativeInstance();

    /**
     * Get the {@code color} uniform.
     *
     * @return The current {@code vec4 color} as a four-element array [r, g, b, a]
     */
    public float[] getColor() {
        float[] result = new float[4];
        getColor(getNative(), result);
        return result;
    }

    /**
     * A convenience overload of {@link #setColor(float, float, float, float)} that
     * lets you use familiar Android {@link Color} values.
     *
     * @param color Any Android {@link Color};
     */
    public void setColor(int color) {
        setColor(Colors.byteToGl(Color.red(color)), //
                Colors.byteToGl(Color.green(color)), //
                Colors.byteToGl(Color.blue(color)), //
                Colors.byteToGl(Color.alpha(color)));
    }

    /**
     * Set the {@code color} uniform.
     * By convention, Meganekko shaders can use a {@code vec4} uniform named
     * {@code UniformColor}. With the default shader, this allows you to add an overlay color on top of the mTexture.
     * Values are between {@code 0.0f} and {@code 1.0f}, inclusive.
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param a Alpha
     */
    public void setColor(float r, float g, float b, float a) {
        setColor(getNative(), r, g, b, a);
    }

    /**
     * Get the opacity.
     *
     * @return The {@code opacity} uniform used to render this material
     */
    public float getOpacity() {
        return getOpacity(getNative());
    }

    /**
     * Set the opacity, in a complicated way.
     *
     * @param opacity Value between {@code 0.0f} and {@code 1.0f}, inclusive.
     */
    public void setOpacity(float opacity) {
        setOpacity(getNative(), opacity);
    }

    /**
     * Get {@code SurfaceTexture} for direct rendering.
     * Use {@link Material#getTexture()} more simple rendering.
     *
     * @return SurfaceTexture
     */
    public SurfaceTexture getSurfaceTexture() {
        return texture().getSurfaceTexture();
    }

    /**
     * Use this to render stereo texture.
     *
     * @param stereoMode Stereo mode.
     */
    public void setStereoMode(StereoMode stereoMode) {
        setStereoMode(getNative(), stereoMode.ordinal());
    }

    public void setTexture(Texture texture) {
        mTexture = texture;
        if (texture != null) {
            setTexture(getNative(), mTexture.getNative());
        } else {
            setTexture(getNative(), 0);
        }
    }

    public Texture getTexture() {
        return mTexture;
    }

    public Texture texture() {
        if (mTexture == null) {
            setTexture(new Texture());
        }
        return getTexture();
    }

    public void update(Frame vrFrame) {
        if (mTexture != null) {
            mTexture.update(vrFrame);
        }
    }

    public void setSide(Side side) {
        setSide(getNative(), side.ordinal());
    }

    @Deprecated
    public CullFace getCullFace() {
        return mCullFace;
    }

    @Deprecated
    public void setCullFace(CullFace cullFace) {
        mCullFace = cullFace;
        setSide(getNative(), cullFace.ordinal());
    }

    public enum StereoMode {
        NORMAL, TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT,
        TOP_ONLY, BOTTOM_ONLY, LEFT_ONLY, RIGHT_ONLY
    }

    @Deprecated
    public enum CullFace {
        CullBack, CullFront, CullNone
    }

    public enum Side {
        FrontSide, BackSide, DoubleSide
    }
}
