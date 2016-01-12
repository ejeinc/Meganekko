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

package com.eje_c.meganekko.texture;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;

import com.eje_c.meganekko.TextureParameters;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.texture.Texture;

import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetError;

/**
 * Bitmap-based texture.
 */
public class BitmapTexture extends Texture {
    /**
     * Constructs a texture using a pre-existing {@link Bitmap}.
     *
     * @param vrContext Current {@link VrContext}
     * @param bitmap    A non-null {@link Bitmap} instance.
     */
    public BitmapTexture(VrContext vrContext, Bitmap bitmap) {
        this(vrContext, bitmap, vrContext.DEFAULT_TEXTURE_PARAMETERS);
    }

    /**
     * Constructs a texture using a pre-existing {@link Bitmap} and the user
     * defined filters {@link TextureParameters}.
     *
     * @param vrContext         Current {@link VrContext}
     * @param bitmap            A non-null {@link Bitmap} instance.
     * @param textureParameters User defined object for {@link TextureParameters} which may
     *                          also contain default values.
     */
    public BitmapTexture(VrContext vrContext, Bitmap bitmap, TextureParameters textureParameters) {
        super(vrContext, bareConstructor(textureParameters.getCurrentValuesArray()));
        update(bitmap);
    }

    /**
     * Constructs a texture by loading a bitmap from a PNG file in (or under)
     * the {@code assets} directory.
     * <p/>
     * This method uses a native code path to create a texture directly from a
     * {@code .png} file; it does not create an Android {@link Bitmap}. It may
     * thus be slightly faster than loading a {@link Bitmap} and creating a
     * texture with {@link #GVRBitmapTexture(VrContext, Bitmap)}, and it should
     * reduce memory pressure, a bit.
     *
     * @param vrContext        Current {@link VrContext}
     * @param pngAssetFilename The name of a {@code .png} file, relative to the assets
     *                         directory. The assets directory may contain an arbitrarily
     *                         complex tree of subdirectories; the file name can specify any
     *                         location in or under the assets directory.
     */
    public BitmapTexture(VrContext vrContext, String pngAssetFilename) {
        this(vrContext, pngAssetFilename, vrContext.DEFAULT_TEXTURE_PARAMETERS);
    }

    /**
     * Constructs a texture by loading a bitmap from a PNG file in (or under)
     * the {@code assets} directory and the user defined filters
     * {@link TextureParameters}.
     * <p/>
     * This method uses a native code path to create a texture directly from a
     * {@code .png} file along with the filters defined by the calling API; it
     * does not create an Android {@link Bitmap}. It may thus be slightly faster
     * than loading a {@link Bitmap} and creating a texture with
     * {@link #BitmapTexture(VrContext, Bitmap)}, and it should reduce
     * memory pressure, a bit.
     *
     * @param vrContext         Current {@link VrContext}
     * @param pngAssetFilename  The name of a {@code .png} file, relative to the assets
     *                          directory. The assets directory may contain an arbitrarily
     *                          complex tree of subdirectories; the file name can specify any
     *                          location in or under the assets directory.
     * @param textureParameters User defined object for {@link TextureParameters} which may
     *                          also contain default values.
     */
    public BitmapTexture(VrContext vrContext, String pngAssetFilename, TextureParameters textureParameters) {
        super(vrContext, fileConstructor(vrContext
                .getContext().getAssets(), pngAssetFilename, textureParameters
                .getCurrentValuesArray()));
    }

    /**
     * Create a new, grayscale texture, from an array of luminance bytes.
     *
     * @param vrContext     Current {@link VrContext}
     * @param width         Texture width, in pixels
     * @param height        Texture height, in pixels
     * @param grayscaleData {@code width * height} bytes of gray scale data
     * @throws IllegalArgumentException If {@code width} or {@code height} is {@literal <= 0,} or if
     *                                  {@code grayScaleData} is {@code null}, or if
     *                                  {@code grayscaleData.length < height * width}
     */
    public BitmapTexture(VrContext vrContext, int width, int height, byte[] grayscaleData) throws IllegalArgumentException {
        this(vrContext, width, height, grayscaleData, vrContext.DEFAULT_TEXTURE_PARAMETERS);
    }

    /**
     * Create a new, grayscale texture, from an array of luminance bytes and the
     * user defined filters {@link TextureParameters}.
     *
     * @param vrContext         Current {@link VrContext}
     * @param width             Texture width, in pixels
     * @param height            Texture height, in pixels
     * @param grayscaleData     {@code width * height} bytes of gray scale data
     * @param textureParameters User defined object for {@link TextureParameters} which may
     *                          also contain default values.
     * @throws IllegalArgumentException If {@code width} or {@code height} is {@literal <= 0,} or if
     *                                  {@code grayScaleData} is {@code null}, or if
     *                                  {@code grayscaleData.length < height * width}
     */
    public BitmapTexture(VrContext vrContext, int width, int height, byte[] grayscaleData, TextureParameters textureParameters)
            throws IllegalArgumentException {
        super(vrContext, bareConstructor(textureParameters.getCurrentValuesArray()));
        update(width, height, grayscaleData);
    }

    /**
     * Copy new luminance data to a grayscale texture.
     * <p/>
     * Creating a new {@link Texture} is pretty cheap, but it's still not a
     * totally trivial operation: it does involve some memory management and
     * some GL hardware handshaking. Reusing the texture reduces this overhead
     * (primarily by delaying garbage collection).
     *
     * @param width         Texture width, in pixels
     * @param height        Texture height, in pixels
     * @param grayscaleData {@code width * height} bytes of gray scale data
     * @return {@code true} if the update succeeded, and {@code false} if it
     * failed. Updating a texture requires that the {@code bitmap}
     * parameter has the exact same size and {@linkplain Config bit
     * depth} as the original bitmap. In particular, you can't update a
     * 'normal' {@linkplain Config#ARGB_8888 32-bit} texture with
     * grayscale data!
     * @throws IllegalArgumentException If {@code width} or {@code height} is {@literal <= 0,} or if
     *                                  {@code grayScaleData} is {@code null}, or if
     *                                  {@code grayscaleData.length < height * width}
     */
    public boolean update(int width, int height, byte[] grayscaleData) throws IllegalArgumentException {
        if (width <= 0 || height <= 0 || grayscaleData == null || grayscaleData.length < height * width) {
            throw new IllegalArgumentException();
        }
        return update(getNative(), width, height, grayscaleData);
    }

    /**
     * Copy a new {@link Bitmap} to the GL texture.
     * <p/>
     * Creating a new {@link Texture} is pretty cheap, but it's still not a
     * totally trivial operation: it does involve some memory management and
     * some GL hardware handshaking. Reusing the texture reduces this overhead
     * (primarily by delaying garbage collection).
     *
     * @param bitmap A standard Android {@link Bitmap}
     * @return {@code true} if the update succeeded, and {@code false} if it
     * failed. Updating a texture requires that the {@code bitmap}
     * parameter has the exact same size and {@linkplain Config bit
     * depth} as the original bitmap. In particular, you can't update a
     * grayscale texture with 'normal' {@linkplain Config#ARGB_8888
     * 32-bit} data!
     */
    public boolean update(Bitmap bitmap) {
        glBindTexture(GL_TEXTURE_2D, getId());
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        return (glGetError() == GL_NO_ERROR);
    }

    private static native long fileConstructor(AssetManager assetManager, String filename, int[] textureParameterValues);

    private static native long bareConstructor(int[] textureParameterValues);

    private static native boolean update(long pointer, int width, int height, byte[] grayscaleData);
}
