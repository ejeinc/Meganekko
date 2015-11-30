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

package com.eje_c.meganekko.asynchronous;

import com.eje_c.meganekko.Texture;
import com.eje_c.meganekko.TextureParameters;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.utility.Log;
import com.eje_c.meganekko.utility.RuntimeAssertion;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_NEAREST;
import static android.opengl.GLES20.GL_NEAREST_MIPMAP_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glTexParameteri;

/**
 * A GL compressed texture; you get it from
 * {@linkplain VrContext#loadCompressedTexture(com.eje_c.meganekko.GVRAndroidResource.CompressedTextureCallback, com.eje_c.meganekko.GVRAndroidResource)
 * GVRContext.loadCompressedTexture()}.
 * <p/>
 * This is mostly an internal, implementation class: You <em>may</em> find
 * {@link #mLevels} and/or {@link #mQuality} useful.
 */
public class GVRCompressedTexture extends Texture {

    /**
     * Optimize for render speed
     */
    public static final int SPEED = -1;
    /**
     * Strike a balance between speed and quality
     */
    public static final int BALANCED = 0;

    /*
     * Texture field(s) and constructors
     */
    /**
     * Optimize for render quality
     */
    public static final int QUALITY = 1;
    protected static final int DEFAULT_QUALITY = SPEED;
    static final int GL_TARGET = GL_TEXTURE_2D;
    private static final String TAG = Log.tag(GVRCompressedTexture.class);
    /**
     * Number of texture levels. 1 means a single image, with no mipmap chain;
     * values higher than 1 mean the texture has a mipmap chain.
     */
    public final int mLevels;
    /**
     * The speed/quality parameter passed to
     * {@link VrContext#loadCompressedTexture(com.eje_c.meganekko.GVRAndroidResource.CompressedTextureCallback, com.eje_c.meganekko.GVRAndroidResource, int)
     * GVRContext.loadCompressedTexture()}.
     * <p/>
     * This copy has been 'clamped' to one of the
     * {@linkplain GVRCompressedTexture#SPEED public constants} in
     * {@link GVRCompressedTexture}.
     */
    public final int mQuality;

    GVRCompressedTexture(VrContext vrContext, int internalFormat, int width,
                         int height, int imageSize, byte[] data, int levels, int quality) {
        this(vrContext, internalFormat, width, height, imageSize, data,
                levels, quality, vrContext.DEFAULT_TEXTURE_PARAMETERS);
    }

    // Texture parameters
    GVRCompressedTexture(VrContext vrContext, int internalFormat, int width,
                         int height, int imageSize, byte[] data, int levels, int quality,
                         TextureParameters textureParameters) {
        super(vrContext, NativeCompressedTexture.normalConstructor(GL_TARGET,
                internalFormat, width, height, imageSize, data,
                textureParameters.getCurrentValuesArray()));
        mLevels = levels;
        mQuality = GVRCompressedTexture.clamp(quality);

        updateMinification();
    }

    GVRCompressedTexture(VrContext vrContext, int target, int levels,
                         int quality) {
        super(vrContext, NativeCompressedTexture.mipmappedConstructor(target));
        mLevels = levels;
        mQuality = GVRCompressedTexture.clamp(quality);

        updateMinification();
    }

    /*
     * Quality tradeoff constants
     */

    private static int selectMipMapMinification(int quality) {
        switch (quality) {
            case SPEED:
                Log.d(TAG, "quality == %s, GL_TEXTURE_MIN_FILTER = %s", "SPEED",
                        "GL_NEAREST_MIPMAP_NEAREST");
                return GL_NEAREST_MIPMAP_NEAREST;
            case BALANCED:
                Log.d(TAG, "quality == %s, GL_TEXTURE_MIN_FILTER = %s", "BALANCED",
                        "GL_LINEAR_MIPMAP_NEAREST");
                return GL_LINEAR_MIPMAP_NEAREST;
            case QUALITY:
                Log.d(TAG, "quality == %s, GL_TEXTURE_MIN_FILTER = %s", "QUALITY",
                        "GL_LINEAR_MIPMAP_LINEAR");
                return GL_LINEAR_MIPMAP_LINEAR;
            default:
                throw new RuntimeAssertion(
                        "The quality parameter should have been clamped");
        }
    }

    private static int clamp(int quality) {
        if (quality < 0) {
            return SPEED;
        } else if (quality > 0) {
            return QUALITY;
        } else {
            return BALANCED;
        }
    }

    private void updateMinification() {
        boolean rebound = true; // in 2 out of 3 branches ...
        if (mLevels > 1) {
            rebind();
            glTexParameteri(GL_TARGET, GL_TEXTURE_MIN_FILTER,
                    selectMipMapMinification(mQuality));
        } else if (mQuality == QUALITY) {
            Log.d(TAG, "quality == %s, GL_TEXTURE_MIN_FILTER = %s", "QUALITY",
                    "GL_LINEAR");
            rebind();
            glTexParameteri(GL_TARGET, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        } else {
            rebound = false;
        }
        if (rebound) {
            unbind();
        }
    }

    protected void rebind() {
        glBindTexture(GL_TARGET, getId());
    }

    protected void unbind() {
        glBindTexture(GL_TARGET, 0);
    }
}

class NativeCompressedTexture {
    static native long normalConstructor(int target, int internalFormat,
                                         int width, int height, int imageSize, byte[] data,
                                         int[] textureParameterValues);

    static native long mipmappedConstructor(int target);
}