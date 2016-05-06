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

import android.graphics.Color;
import android.graphics.SurfaceTexture;

import com.eje_c.meganekko.utility.Colors;

/**
 * This is one of the key Meganekko classes: it holds texture, color, opacity information.
 */
public class Material extends HybridObject {

    private Texture mTexture;
    private CullFace cullFace;

    private static native void setColor(long material, float r, float g, float b, float a);

    private static native float[] getColor(long material);

    private static native void setOpacity(long material, float opacity);

    private static native float getOpacity(long material);

    private static native void setStereoMode(long material, int stereoMode);

    private static native void setCullFace(long material, int cullFace);

    @Override
    protected native long initNativeInstance();

//    @Override
//    protected void delete() {
//
//        if (mTexture != null) {
//            mTexture.release();
//            mTexture = null;
//        }
//
//        super.delete();
//    }

    /**
     * Get the {@code color} uniform.
     *
     * @return The current {@code vec4 color} as a four-element array [r, g, b, a]
     */
    public float[] getColor() {
        return getColor(getNative());
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
     * <p/>
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
        return getSurfaceTexture(getNative());
    }

    private native SurfaceTexture getSurfaceTexture(long nativePtr);

    /**
     * Use this to render stereo texture.
     *
     * @param stereoMode
     */
    public void setStereoMode(StereoMode stereoMode) {
        setStereoMode(getNative(), stereoMode.ordinal());
    }

    public Texture getTexture() {
        return texture();
    }

    public Texture texture() {
        if (mTexture == null) {
            mTexture = new Texture(getSurfaceTexture());
        }
        return mTexture;
    }

    public void update(Frame vrFrame) {
        if (mTexture != null) {
            mTexture.update(vrFrame);
        }
    }

    public void setCullFace(CullFace cullFace) {
        this.cullFace = cullFace;
        setCullFace(getNative(), cullFace.ordinal());
    }

    public CullFace getCullFace() {
        return cullFace;
    }

    public enum StereoMode {
        NORMAL, TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT,
        TOP_ONLY, BOTTOM_ONLY, LEFT_ONLY, RIGHT_ONLY
    }

    public enum CullFace {
        CullBack, CullFront, CullNone
    }
}
