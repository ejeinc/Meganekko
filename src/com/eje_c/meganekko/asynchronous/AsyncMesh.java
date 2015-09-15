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

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.GLContext;
import com.eje_c.meganekko.HybridObject;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.AndroidResource.CancelableCallback;
import com.eje_c.meganekko.asynchronous.Throttler.AsyncLoader;
import com.eje_c.meganekko.asynchronous.Throttler.AsyncLoaderFactory;
import com.eje_c.meganekko.asynchronous.Throttler.GlConverter;
import com.eje_c.meganekko.utility.Log;

/**
 * Async resource loading: meshes.
 * 
 * @since 1.6.2
 */
abstract class AsyncMesh {

    @SuppressWarnings("unused")
    private static final String TAG = Log.tag(AsyncMesh.class);

    /*
     * The API
     */

    static void loadMesh(GLContext gvrContext,
            CancelableCallback<Mesh> callback, AndroidResource resource,
            int priority) {
        Throttler.registerCallback(gvrContext, MESH_CLASS, callback, resource,
                priority);
    }

    /*
     * The implementation
     */

    private static class AsyncLoadMesh extends AsyncLoader<Mesh, Mesh> {
        static final GlConverter<Mesh, Mesh> sConverter = new GlConverter<Mesh, Mesh>() {

            @Override
            public Mesh convert(GLContext gvrContext, Mesh mesh) {
                return mesh;
            }
        };

        AsyncLoadMesh(GLContext gvrContext, AndroidResource request,
                CancelableCallback<HybridObject> callback, int priority) {
            super(gvrContext, sConverter, request, callback);
        }

        @Override
        protected Mesh loadResource() throws InterruptedException {
            return gvrContext.loadMesh(resource);
        }
    }

    private static final Class<? extends HybridObject> MESH_CLASS = Mesh.class;

    static {
        Throttler.registerDatatype(MESH_CLASS,
                new AsyncLoaderFactory<Mesh, Mesh>() {

                    @Override
                    AsyncLoader<Mesh, Mesh> threadProc(
                            GLContext gvrContext, AndroidResource request,
                            CancelableCallback<HybridObject> callback,
                            int priority) {
                        return new AsyncLoadMesh(gvrContext, request, callback,
                                priority);
                    }
                });
    }

}
