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

package com.eje_c.meganekko.utility;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.HybridObject;
import com.eje_c.meganekko.Texture;
import com.eje_c.meganekko.AndroidResource.BitmapTextureCallback;
import com.eje_c.meganekko.AndroidResource.Callback;
import com.eje_c.meganekko.AndroidResource.CancelableCallback;
import com.eje_c.meganekko.AndroidResource.CompressedTextureCallback;

/**
 * Basic cache-by-resource-description.
 * 
 * Keeps system from reloading resources, so long as a previous copy is still in
 * memory. Generic, so there can be separate caches for meshes and textures: a
 * 'unified cache' (mapping resource to hybrid-object) with hooks in
 * {@link com.eje_c.meganekko.asynchronous.Throttler Throttler} would not be safe.
 * Passing the descriptor for a cached mesh to a get-texture call would return
 * the mesh ....
 */
public class ResourceCache<T extends HybridObject> {
    // private static final String TAG = Log.tag(ResourceCache.class);

    private final Map<AndroidResource, WeakReference<T>> cache //
    = new HashMap<AndroidResource, WeakReference<T>>();

    /** Save a weak reference to the resource */
    public void put(AndroidResource androidResource, T resource) {
        // Log.d(TAG, "put(%s) saving %s", androidResource, resource);

        cache.put(androidResource, new WeakReference<T>(resource));
    }

    /** Get the cached resource, or {@code null} */
    public T get(AndroidResource androidResource) {
        WeakReference<T> reference = cache.get(androidResource);
        if (reference == null) {
            // Not in map
            // Log.d(TAG, "get(%s) returning %s", androidResource, null);
            return null;
        }
        T cached = reference.get();
        if (cached == null) {
            // In map, but not in memory
            cache.remove(androidResource);
        } else {
            // No one will ever read this stream
            androidResource.closeStream();
        }
        // Log.d(TAG, "get(%s) returning %s", androidResource, cached);
        return cached;
    }

    /**
     * Wrap the callback, to cache the
     * {@link Callback#loaded(HybridObject, AndroidResource) loaded()}
     * resource
     */
    public Callback<T> wrapCallback(Callback<T> callback) {
        return new CallbackWrapper<T>(this, callback);
    }

    /**
     * Wrap the callback, to cache the
     * {@link CancelableCallback#loaded(HybridObject, AndroidResource)
     * loaded()} resource
     */
    public CancelableCallback<T> wrapCallback(CancelableCallback<T> callback) {
        return new CancelableCallbackWrapper<T>(this, callback);
    }

    /**
     * Wrap the callback, to cache the
     * {@link CompressedTextureCallback#loaded(HybridObject, AndroidResource)
     * loaded()} resource
     */
    public static CompressedTextureCallback wrapCallback(
            ResourceCache<Texture> cache, CompressedTextureCallback callback) {
        return new CompressedTextureCallbackWrapper(cache, callback);
    }

    /**
     * Wrap the callback, to cache the
     * {@link BitmapTextureCallback#loaded(HybridObject, AndroidResource)
     * loaded()} resource
     */
    public static BitmapTextureCallback wrapCallback(
            ResourceCache<Texture> cache, BitmapTextureCallback callback) {
        return new BitmapTextureCallbackWrapper(cache, callback);
    }

    private static class CallbackWrapper<T extends HybridObject> implements
            Callback<T> {

        protected final ResourceCache<T> cache;
        protected final Callback<T> callback;

        CallbackWrapper(ResourceCache<T> cache, Callback<T> callback) {
            Assert.checkNotNull("cache", cache);
            Assert.checkNotNull("callback", callback);

            this.cache = cache;
            this.callback = callback;
        }

        @Override
        public void loaded(T resource, AndroidResource androidResource) {
            cache.put(androidResource, resource);
            callback.loaded(resource, androidResource);
        }

        @Override
        public void failed(Throwable t, AndroidResource androidResource) {
            callback.failed(t, androidResource);
        }
    }

    private static class CancelableCallbackWrapper<T extends HybridObject>
            extends CallbackWrapper<T> implements CancelableCallback<T> {

        private CancelableCallbackWrapper(ResourceCache<T> cache,
                CancelableCallback<T> cancelableCallback) {
            super(cache, cancelableCallback);
        }

        @Override
        public boolean stillWanted(AndroidResource androidResource) {
            return ((CancelableCallback<T>) callback)
                    .stillWanted(androidResource);
        }
    }

    // Those 'convenience' interfaces are getting to be a real annoyance
    private static class CompressedTextureCallbackWrapper extends
            CallbackWrapper<Texture> implements CompressedTextureCallback {

        CompressedTextureCallbackWrapper(ResourceCache<Texture> cache,
                CompressedTextureCallback callback) {
            super(cache, callback);
        }
    }

    private static class BitmapTextureCallbackWrapper extends
            CancelableCallbackWrapper<Texture> implements
            BitmapTextureCallback {
        BitmapTextureCallbackWrapper(ResourceCache<Texture> cache,
                BitmapTextureCallback callback) {
            super(cache, callback);
        }
    }
}
