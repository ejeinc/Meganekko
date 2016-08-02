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
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;

/**
 * This is one of the key Meganekko classes: it holds texture, color, opacity information.
 */
public class Material extends HybridObject {

    private Texture mTexture;

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

    private static native void setStereoMode(long material, int stereoMode);

    @Override
    protected native long initNativeInstance();

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
     * @param stereoMode Stereo mode.
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

    public enum StereoMode {
        NORMAL, TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT,
        TOP_ONLY, BOTTOM_ONLY, LEFT_ONLY, RIGHT_ONLY
    }
}
