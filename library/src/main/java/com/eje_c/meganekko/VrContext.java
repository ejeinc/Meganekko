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

import android.app.Activity;
import android.content.Context;

import com.eje_c.meganekko.utility.ResourceCache;

import java.util.EnumSet;

/**
 * Like the Android {@link Context} class, {@code VRContext} provides core
 * services, and global information about an application environment.
 * <p/>
 * Use {@code VRContext} to {@linkplain #createQuad(float, float) create} and
 * {@linkplain #loadMesh(AndroidResource) load} GL meshes, Android
 * {@linkplain #loadBitmap(String) bitmaps}, and
 * {@linkplain #loadTexture(AndroidResource) GL textures.}
 */
public class VrContext {

    private static VrContext instance;

    public static VrContext get() {
        return instance;
    }

    private final static ResourceCache<Mesh> sMeshCache = new ResourceCache<Mesh>();
    private final MeganekkoActivity mContext;

    VrContext(MeganekkoActivity context) {
        mContext = context;
        instance = this;
    }

    /**
     * Get the Android {@link Context}, which provides access to system services
     * and to your application's resources. Since version 2.0.1, this is
     * actually your {@link MeganekkoActivity} implementation, but you should
     * probably use the new {@link #getActivity()} method, rather than casting
     * this method to an {@code (Activity)} or {@code (MeganekkoActivity)}.
     *
     * @return An Android {@code Context}
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Get the Android {@link Activity} which launched your Meganekko app.
     * <p/>
     * An {@code Activity} is-a {@link Context} and so provides access to system
     * services and to your application's resources; the {@code Activity} class
     * also provides additional services, including
     * {@link Activity#runOnUiThread(Runnable)}.
     *
     * @return The {@link MeganekkoActivity} which launched your Meganekko app.
     */
    public MeganekkoActivity getActivity() {
        return mContext;
    }

    /**
     * Loads a file as a {@link Mesh}.
     * <p/>
     * Note that this method can be quite slow; we recommend never calling it
     * from the GL thread. The asynchronous version
     * {@link #loadMesh(AndroidResource.MeshCallback, AndroidResource)} is
     * better because it moves most of the work to a background thread, doing as
     * little as possible on the GL thread.
     *
     * @param androidResource Basically, a stream containing a 3D model. The
     *                        {@link AndroidResource} class has six constructors to handle a
     *                        wide variety of Android resource types. Taking a
     *                        {@code AndroidResource} here eliminates six overloads.
     * @return The file as a GL mesh.
     */
    public Mesh loadMesh(AndroidResource androidResource) {
        return loadMesh(androidResource, ImportSettings.getRecommendedSettings());
    }

    /**
     * Loads a file as a {@link Mesh}.
     * <p/>
     * Note that this method can be quite slow; we recommend never calling it
     * from the GL thread. The asynchronous version
     * {@link #loadMesh(AndroidResource.MeshCallback, AndroidResource)} is
     * better because it moves most of the work to a background thread, doing as
     * little as possible on the GL thread.
     *
     * @param androidResource Basically, a stream containing a 3D model. The
     *                        {@link AndroidResource} class has six constructors to handle a
     *                        wide variety of Android resource types. Taking a
     *                        {@code AndroidResource} here eliminates six overloads.
     * @param settings        Additional import {@link ImpotSettings settings}.
     * @return The file as a GL mesh.
     */
    public Mesh loadMesh(AndroidResource androidResource, EnumSet<ImportSettings> settings) {
        Mesh mesh = sMeshCache.get(androidResource);
        if (mesh == null) {
            AssimpImporter assimpImporter = Importer.readFileFromResources(androidResource, settings);
            mesh = assimpImporter.getMesh(0);
            sMeshCache.put(androidResource, mesh);
        }
        return mesh;
    }

    /**
     * Creates a quad consisting of two triangles, with the specified width and
     * height.
     *
     * @param width  the quad's width
     * @param height the quad's height
     * @return A 2D, rectangular mesh with four vertices and two triangles
     */
    public Mesh createQuad(float width, float height) {
        Mesh mesh = new Mesh();

        float[] vertices = {width * -0.5f, height * 0.5f, 0.0f, width * -0.5f,
                height * -0.5f, 0.0f, width * 0.5f, height * 0.5f, 0.0f,
                width * 0.5f, height * -0.5f, 0.0f};
        mesh.setVertices(vertices);

        final float[] normals = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f};
        mesh.setNormals(normals);

        final float[] texCoords = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                1.0f};
        mesh.setTexCoords(texCoords);

        char[] triangles = {0, 1, 2, 1, 3, 2};
        mesh.setTriangles(triangles);

        return mesh;
    }

    /**
     * @deprecated Use {@link MeganekkoActivity#runOnGlThread(Runnable)}.
     */
    public void runOnGlThread(Runnable runnable) {
        getActivity().runOnGlThread(runnable);
    }
}
