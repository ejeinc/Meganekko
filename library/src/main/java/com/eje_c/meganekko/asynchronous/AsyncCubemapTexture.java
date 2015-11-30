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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.AndroidResource.CancelableCallback;
import com.eje_c.meganekko.CubemapTexture;
import com.eje_c.meganekko.HybridObject;
import com.eje_c.meganekko.Texture;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.asynchronous.Throttler.AsyncLoader;
import com.eje_c.meganekko.asynchronous.Throttler.AsyncLoaderFactory;
import com.eje_c.meganekko.asynchronous.Throttler.GlConverter;

import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Async resource loading: cube map textures.
 * <p/>
 * Since ZipInputStream does not support mark() and reset(), we directly use
 * BitmapFactory .decodeStream() in loadResource().
 */
abstract class AsyncCubemapTexture {

    /*
     * The API
     */

    private static final Class<? extends HybridObject> TEXTURE_CLASS = CubemapTexture.class;

    /*
     * Static constants
     */

    // private static final String TAG = Log.tag(AsyncCubemapTexture.class);
    private static Map<String, Integer> faceIndexMap;

    /*
     * Asynchronous loader
     */

    static {
        Throttler.registerDatatype(TEXTURE_CLASS,
                new AsyncLoaderFactory<CubemapTexture, Bitmap[]>() {

                    @Override
                    AsyncLoadCubemapTextureResource threadProc(
                            VrContext vrContext, AndroidResource request,
                            CancelableCallback<HybridObject> callback,
                            int priority) {
                        return new AsyncLoadCubemapTextureResource(vrContext,
                                request, callback, priority);
                    }
                });
    }

    static void loadTexture(VrContext vrContext,
                            CancelableCallback<Texture> callback,
                            AndroidResource resource, int priority, Map<String, Integer> map) {
        faceIndexMap = map;
        Throttler.registerCallback(vrContext, TEXTURE_CLASS, callback,
                resource, priority);
    }

    private static class AsyncLoadCubemapTextureResource extends
            AsyncLoader<CubemapTexture, Bitmap[]> {

        private static final GlConverter<CubemapTexture, Bitmap[]> sConverter = new GlConverter<CubemapTexture, Bitmap[]>() {

            @Override
            public CubemapTexture convert(VrContext vrContext,
                                          Bitmap[] bitmapArray) {
                return new CubemapTexture(vrContext, bitmapArray);
            }
        };

        protected AsyncLoadCubemapTextureResource(VrContext vrContext,
                                                  AndroidResource request,
                                                  CancelableCallback<HybridObject> callback, int priority) {
            super(vrContext, sConverter, request, callback);
        }

        @Override
        protected Bitmap[] loadResource() {
            Bitmap[] bitmapArray = new Bitmap[6];
            ZipInputStream zipInputStream = new ZipInputStream(
                    resource.getStream());

            try {
                ZipEntry zipEntry = null;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String imageName = zipEntry.getName();
                    Integer imageIndex = faceIndexMap.get(imageName);
                    if (imageIndex == null) {
                        throw new IllegalArgumentException("Name of image ("
                                + imageName + ") is not set!");
                    }
                    bitmapArray[imageIndex] = BitmapFactory
                            .decodeStream(zipInputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resource.closeStream();
            return bitmapArray;
        }
    }
}
