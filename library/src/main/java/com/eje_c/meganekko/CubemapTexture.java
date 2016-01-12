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

import java.util.HashMap;
import java.util.Map;

/**
 * Cube map texture.
 */
public class CubemapTexture extends Texture {
    final static Map<String, Integer> faceIndexMap = new HashMap<>(6);

    static {
        faceIndexMap.put("posx.png", 0);
        faceIndexMap.put("negx.png", 1);
        faceIndexMap.put("posy.png", 2);
        faceIndexMap.put("negy.png", 3);
        faceIndexMap.put("posz.png", 4);
        faceIndexMap.put("negz.png", 5);
    }

    /**
     * Constructs a cube map texture using six pre-existing {@link Bitmap}s.
     *
     * @param vrContext   Current {@link VrContext}
     * @param bitmapArray A {@link Bitmap} array which contains six {@link Bitmap}s. The
     *                    six bitmaps correspond to +x, -x, +y, -y, +z, and -z faces of
     *                    the cube map texture respectively. The default names of the
     *                    six images are "posx.png", "negx.png", "posy.png", "negx.png",
     *                    "posz.png", and "negz.png", which can be changed by calling
     *                    {@link CubemapTexture#setFaceNames(String[])}.
     */
    public CubemapTexture(VrContext vrContext, Bitmap[] bitmapArray) {
        this(vrContext, bitmapArray, vrContext.DEFAULT_TEXTURE_PARAMETERS);
    }

    /**
     * Constructs a cube map texture using six pre-existing {@link Bitmap}s and
     * the user defined filters {@link TextureParameters}.
     *
     * @param vrContext         Current {@link VrContext}
     * @param bitmapArray       A {@link Bitmap} array which contains six {@link Bitmap}s. The
     *                          six bitmaps correspond to +x, -x, +y, -y, +z, and -z faces of
     *                          the cube map texture respectively. The default names of the
     *                          six images are "posx.png", "negx.png", "posy.png", "negx.png",
     *                          "posz.png", and "negz.png", which can be changed by calling
     *                          {@link CubemapTexture#setFaceNames(String[])}.
     * @param textureParameters User defined object for {@link TextureParameters} which may
     *                          also contain default values.
     */
    public CubemapTexture(VrContext vrContext, Bitmap[] bitmapArray, TextureParameters textureParameters) {
        super(vrContext, bitmapArrayConstructor(
                bitmapArray, textureParameters.getCurrentValuesArray()));
    }

    /**
     * Set the names of six images in the zip file. The default names of the six
     * images are "posx.png", "negx.png", "posy.png", "negx.png", "posz.png",
     * and "negz.png". If the names of the six images in the zip file are
     * different to the default ones, this function must be called before load
     * the zip file.
     *
     * @param nameArray An array containing six strings which are names of images
     *                  corresponding to +x, -x, +y, -y, +z, and -z faces of the cube
     *                  map texture respectively.
     */
    public static void setFaceNames(String[] nameArray) {
        if (nameArray.length != 6) {
            throw new IllegalArgumentException("nameArray length is not 6.");
        }

        for (int i = 0; i < 6; i++) {
            faceIndexMap.put(nameArray[i], i);
        }
    }

    private static native long bitmapArrayConstructor(Bitmap[] bitmapArray, int[] textureParameterValues);
}
