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

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.AndroidResource.BitmapTextureCallback;
import com.eje_c.meganekko.AndroidResource.CancelableCallback;
import com.eje_c.meganekko.AndroidResource.CompressedTextureCallback;
import com.eje_c.meganekko.FutureWrapper;
import com.eje_c.meganekko.HybridObject;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.Shaders;
import com.eje_c.meganekko.texture.Texture;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.utility.Log;
import com.eje_c.meganekko.utility.ResourceCache;
import com.eje_c.meganekko.utility.Threads;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Internal API for asynchronous resource loading.
 * <p/>
 * You will normally call into this class through
 * {@link VrContext#loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
 * or
 * {@link VrContext#loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)}
 * or
 * {@link VrContext#loadMesh(AndroidResource.MeshCallback, AndroidResource)}
 * .
 */
public class AsynchronousResourceLoader {

    /**
     * Load a compressed texture asynchronously.
     * <p/>
     * This is the implementation of
     * {@link VrContext#loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * : it will usually be more convenient (and more efficient) to call that
     * directly.
     *
     * @param vrContext    The Meganekko context
     * @param textureCache Texture cache - may be {@code null}
     * @param callback     Asynchronous notifications
     * @param resource     Stream containing a compressed texture
     * @throws IllegalArgumentException If {@code vrContext} or {@code callback} parameters are
     *                                  {@code null}
     */
    public static void loadCompressedTexture(final VrContext vrContext,
                                             ResourceCache<Texture> textureCache,
                                             final CompressedTextureCallback callback,
                                             final AndroidResource resource) throws IllegalArgumentException {
        loadCompressedTexture(vrContext, textureCache, callback, resource,
                GVRCompressedTexture.DEFAULT_QUALITY);
    }

    /**
     * Load a compressed texture asynchronously.
     * <p/>
     * This is the implementation of
     * {@link VrContext#loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * : it will usually be more convenient (and more efficient) to call that
     * directly.
     *
     * @param vrContext    The Meganekko context
     * @param textureCache Texture cache - may be {@code null}
     * @param callback     Asynchronous notifications
     * @param resource     Basically, a stream containing a compressed texture. Taking a
     *                     {@link AndroidResource} parameter eliminates six overloads.
     * @param quality      Speed/quality tradeoff: should be one of
     *                     {@link GVRCompressedTexture#SPEED},
     *                     {@link GVRCompressedTexture#BALANCED}, or
     *                     {@link GVRCompressedTexture#QUALITY}, but other values are
     *                     'clamped' to one of the recognized values.
     * @throws IllegalArgumentException If {@code vrContext} or {@code callback} parameters are
     *                                  {@code null}
     */
    public static void loadCompressedTexture(final VrContext vrContext,
                                             final ResourceCache<Texture> textureCache,
                                             final CompressedTextureCallback callback,
                                             final AndroidResource resource, final int quality)
            throws IllegalArgumentException {
        validateCallbackParameters(vrContext, callback, resource);

        final Texture cached = textureCache == null ? null : textureCache
                .get(resource);
        if (cached != null) {
            vrContext.runOnGlThread(new Runnable() {

                @Override
                public void run() {
                    callback.loaded(cached, resource);
                }
            });
        } else {
            // Load the bytes on a background thread
            Threads.spawn(new Runnable() {

                @Override
                public void run() {
                    try {
                        final CompressedTexture compressedTexture = CompressedTexture
                                .load(resource.getStream(), false);
                        resource.closeStream();
                        // Create texture on GL thread
                        vrContext.runOnGlThread(new Runnable() {

                            @Override
                            public void run() {
                                Texture texture = compressedTexture
                                        .toTexture(vrContext, quality);
                                if (textureCache != null) {
                                    textureCache.put(resource, texture);
                                }
                                callback.loaded(texture, resource);
                            }
                        });
                    } catch (Exception e) {
                        callback.failed(e, resource);
                    }
                }
            });
        }
    }


    /**
     * Load a GL mesh asynchronously.
     * <p/>
     * This is the implementation of
     * {@link VrContext#loadMesh(AndroidResource.MeshCallback, AndroidResource, int)}
     * - it will usually be more convenient to call that directly.
     *
     * @param vrContext The Meganekko context
     * @param callback  Asynchronous notifications
     * @param resource  Basically, a stream containing a 3D model. Taking a
     *                  {@link AndroidResource} parameter eliminates six overloads.
     * @param priority  A value {@literal >=} {@link VrContext#LOWEST_PRIORITY} and
     *                  {@literal <=} {@link VrContext#HIGHEST_PRIORITY}
     * @throws IllegalArgumentException If {@code priority} {@literal <}
     *                                  {@link VrContext#LOWEST_PRIORITY} or {@literal >}
     *                                  {@link VrContext#HIGHEST_PRIORITY}, or any of the other
     *                                  parameters are {@code null}.
     */
    // This method does not take a ResourceCache<GVRMeh> parameter because it
    // (indirectly) calls GVRContext.loadMesh() which 'knows about' the cache
    public static void loadMesh(VrContext vrContext,
                                CancelableCallback<Mesh> callback, AndroidResource resource,
                                int priority) {
        validatePriorityCallbackParameters(vrContext, callback, resource,
                priority);

        AsyncMesh.loadMesh(vrContext, callback, resource, priority);
    }

    /**
     * Load a GL mesh asynchronously.
     * <p/>
     * This is the implementation of
     * {@link VrContext#loadFutureMesh(AndroidResource, int)} - it will
     * usually be more convenient to call that directly.
     *
     * @param vrContext The Meganekko context
     * @param resource  Basically, a stream containing a 3D model. The
     *                  {@link AndroidResource} class has six constructors to
     *                  handle a wide variety of Android resource types. Taking a
     *                  {@code GVRAndroidResource} here eliminates six overloads.
     * @param priority  This request's priority. Please see the notes on asynchronous
     *                  priorities in the <a href="package-summary.html#async">package
     *                  description</a>.
     * @return A {@link Future} that you can pass to
     * {@link RenderData#setMesh(Future)}
     */
    public static Future<Mesh> loadFutureMesh(VrContext vrContext,
                                              AndroidResource resource, int priority) {
        FutureResource<Mesh> result = new FutureResource<Mesh>();

        loadMesh(vrContext, result.callback, resource, priority);

        return result;
    }

    private static <T extends HybridObject> void validateCallbackParameters(
            VrContext vrContext, AndroidResource.Callback<T> callback,
            AndroidResource resource) {
        if (vrContext == null) {
            throw new IllegalArgumentException("vrContext == null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback == null");
        }
        if (resource == null) {
            throw new IllegalArgumentException("resource == null");
        }
    }

    private static <T extends HybridObject> void validatePriorityCallbackParameters(
            VrContext vrContext, AndroidResource.Callback<T> callback,
            AndroidResource resource, int priority) {
        validateCallbackParameters(vrContext, callback, resource);
        if (priority < VrContext.LOWEST_PRIORITY
                || priority > VrContext.HIGHEST_PRIORITY) {
            throw new IllegalArgumentException(
                    "Priority < GVRContext.LOWEST_PRIORITY or > GVRContext.HIGHEST_PRIORITY");
        }
    }

    private static class FutureResource<T extends HybridObject> implements
            Future<T> {

        private static final String TAG = Log.tag(FutureResource.class);

        /**
         * Do all our synchronization on data private to this instance
         */
        private final Object[] lock = new Object[0];

        private T result = null;
        private Throwable error = null;
        private boolean pending = true;
        private boolean canceled = false;

        private final CancelableCallback<T> callback = new CancelableCallback<T>() {

            @Override
            public void loaded(T data, AndroidResource androidResource) {
                synchronized (lock) {
                    result = data;
                    pending = false;
                    lock.notifyAll();
                }
            }

            @Override
            public void failed(Throwable t, AndroidResource androidResource) {
                Log.d(TAG, "failed(%s), %s", androidResource, t);
                synchronized (lock) {
                    error = t;
                    pending = false;
                    lock.notifyAll();
                }
            }

            @Override
            public boolean stillWanted(AndroidResource androidResource) {
                return canceled == false;
            }
        };

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            canceled = true;
            return pending;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            return get(0);
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException,
                ExecutionException, TimeoutException {
            return get(unit.toMillis(timeout));
        }

        private T get(long millis) throws InterruptedException,
                ExecutionException {
            synchronized (lock) {
                if (pending) {
                    lock.wait(millis);
                }
            }
            if (canceled) {
                throw new CancellationException();
            }
            if (error != null) {
                throw new ExecutionException(error);
            }
            return result;
        }

        @Override
        public boolean isCancelled() {
            return canceled;
        }

        @Override
        public boolean isDone() {
            return pending == false;
        }

    }
}
