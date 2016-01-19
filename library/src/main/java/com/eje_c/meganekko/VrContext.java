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
import android.opengl.GLES20;

import com.eje_c.meganekko.AndroidResource.MeshCallback;
import com.eje_c.meganekko.animation.Animation;
import com.eje_c.meganekko.animation.AnimationEngine;
import com.eje_c.meganekko.asynchronous.AsynchronousResourceLoader;
import com.eje_c.meganekko.periodic.PeriodicEngine;
import com.eje_c.meganekko.utility.Log;
import com.eje_c.meganekko.utility.ResourceCache;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Future;

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

    /**
     * Meganekko can't use every {@code int} as a priority - it needs some
     * sentinel values. It will probably never need anywhere near this many, but
     * raising the number of reserved values narrows the 'dynamic range'
     * available to apps mapping some internal score to the
     * {@link #LOWEST_PRIORITY} to {@link #HIGHEST_PRIORITY} range, and might
     * change app behavior in subtle ways that seem best avoided.
     */
    public static final int RESERVED_PRIORITIES = 1024;
    /**
     * Meganekko can't use every {@code int} as a priority - it needs some
     * sentinel values. A simple approach to generating priorities is to score
     * resources from 0 to 1, and then map that to the range
     * {@link #LOWEST_PRIORITY} to {@link #HIGHEST_PRIORITY}.
     */
    public static final int LOWEST_PRIORITY = Integer.MIN_VALUE
            + RESERVED_PRIORITIES;

    /*
     * Fields and constants
     */

    // Priorities constants, for asynchronous loading
    /**
     * Meganekko can't use every {@code int} as a priority - it needs some
     * sentinel values. A simple approach to generating priorities is to score
     * resources from 0 to 1, and then map that to the range
     * {@link #LOWEST_PRIORITY} to {@link #HIGHEST_PRIORITY}.
     */
    public static final int HIGHEST_PRIORITY = Integer.MAX_VALUE;
    /**
     * The priority used by
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)}
     * and {@link #loadMesh(AndroidResource.MeshCallback, AndroidResource)}
     */
    public static final int DEFAULT_PRIORITY = 0;
    private static final String TAG = Log.tag(VrContext.class);
    private final static ResourceCache<Mesh> sMeshCache = new ResourceCache<Mesh>();
    private static final List<Runnable> sHandlers = new ArrayList<Runnable>();
    private final MeganekkoActivity mContext;

    /*
     * Methods
     */
    // true or false based on the support for anisotropy
    public boolean isAnisotropicSupported;
    /**
     * The ID of the GLthread. We use this ID to prevent non-GL thread from
     * calling GL functions.
     */
    protected long mGLThreadID;

    VrContext(MeganekkoActivity context) {
        mContext = context;

        instance = this;
        // Clear singletons and per-run data structures
        resetOnRestart();
    }

    /**
     * Register a method that is called every time Meganekko creates a new
     * {@link VrContext}.
     * <p/>
     * Android apps aren't mapped 1:1 to Linux processes; the system may keep a
     * process loaded even after normal complete shutdown, and call Android
     * lifecycle methods to reinitialize it. This causes problems for (in
     * particular) lazy-created singletons that are tied to a particular
     * {@code VRContext}. This method lets you register a handler that will be
     * called on restart, which can reset your {@code static} variables to the
     * compiled-in start state.
     * <p/>
     * <p/>
     * For example,
     * <p/>
     * <pre>
     *
     * static YourSingletonClass sInstance;
     *
     * static {
     *     VRContext.addResetOnRestartHandler(new Runnable() {
     *
     *         &#064;Override
     *         public void run() {
     *             sInstance = null;
     *         }
     *     });
     * }
     *
     * </pre>
     * <p/>
     * <p/>
     * Meganekko will force an Android garbage collection after running any
     * handlers, which will free any remaining native objects from the previous
     * run.
     *
     * @param handler Callback to run on restart.
     */
    public synchronized static void addResetOnRestartHandler(Runnable handler) {
        sHandlers.add(handler);
    }

    protected synchronized static void resetOnRestart() {
        for (Runnable handler : sHandlers) {
            Log.d(TAG, "Running on-restart handler %s", handler);
            handler.run();
        }

        // We've probably just nulled-out a bunch of references, but many Meganekko
        // apps do relatively little Java memory allocation, so it may actually
        // be a longish while before the recyclable references go stale.
        System.gc();

        // We do NOT want to clear sHandlers - the static initializers won't be
        // run again, even if the new run does recreate singletons.
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
        return loadMesh(androidResource,
                ImportSettings.getRecommendedSettings());
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
    public Mesh loadMesh(AndroidResource androidResource,
                         EnumSet<ImportSettings> settings) {
        Mesh mesh = sMeshCache.get(androidResource);
        if (mesh == null) {
            AssimpImporter assimpImporter = Importer.readFileFromResources(androidResource, settings);
            mesh = assimpImporter.getMesh(0);
            sMeshCache.put(androidResource, mesh);
        }
        return mesh;
    }

    /**
     * Loads a mesh file, asynchronously, at a default priority.
     * <p/>
     * This method and the
     * {@linkplain #loadMesh(AndroidResource.MeshCallback, AndroidResource, int)
     * overload that takes a priority} are generally going to be your best
     * choices for loading {@link Mesh} resources: mesh loading can take
     * hundreds - and even thousands - of milliseconds, and so should not be
     * done on the GL thread in either {@link MeganekkoActivity#oneTimeInit()}
     * onInit()} or {@link MeganekkoActivity#frame()}.
     * <p/>
     * <p/>
     * The asynchronous methods improve throughput in three ways. First, by
     * doing all the work on a background thread, then delivering the loaded
     * mesh to the GL thread on a {@link #runOnGlThread(Runnable)
     * runOnGlThread()} callback. Second, they use a throttler to avoid
     * overloading the system and/or running out of memory. Third, they do
     * 'request consolidation' - if you issue any requests for a particular file
     * while there is still a pending request, the file will only be read once,
     * and each callback will get the same {@link Mesh}.
     *
     * @param callback        App supplied callback, with three different methods.
     *                        <ul>
     *                        <li>Before loading, Meganekko may call
     *                        {@link AndroidResource.MeshCallback#stillWanted(AndroidResource)
     *                        stillWanted()} (on a background thread) to give you a chance
     *                        to abort a 'stale' load.
     *                        <p/>
     *                        <li>Successful loads will call
     *                        {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                        loaded()} on the GL thread.
     *                        <p/>
     *                        <li>Any errors will call
     *                        {@link AndroidResource.MeshCallback#failed(Throwable, AndroidResource)
     *                        failed(),} with no promises about threading.
     *                        </ul>
     * @param androidResource Basically, a stream containing a 3D model. The
     *                        {@link AndroidResource} class has six constructors to handle a
     *                        wide variety of Android resource types. Taking a
     *                        {@code AndroidResource} here eliminates six overloads.
     * @throws IllegalArgumentException If either parameter is {@code null} or if you 'abuse' request
     *                                  consolidation by passing the same {@link AndroidResource}
     *                                  descriptor to multiple load calls.
     *                                  <p/>
     *                                  It's fairly common for multiple scene objects to use the same
     *                                  texture or the same mesh. Thus, if you try to load, say,
     *                                  {@code R.raw.whatever} while you already have a pending
     *                                  request for {@code R.raw.whatever}, it will only be loaded
     *                                  once; the same resource will be used to satisfy both (all)
     *                                  requests. This "consolidation" uses
     *                                  {@link AndroidResource#equals(Object)}, <em>not</em>
     *                                  {@code ==} (aka "reference equality"): The problem with using
     *                                  the same resource descriptor is that if requests can't be
     *                                  consolidated (because the later one(s) came in after the
     *                                  earlier one(s) had already completed) the resource will be
     *                                  reloaded ... but the original descriptor will have been
     *                                  closed.
     */
    public void loadMesh(MeshCallback callback,
                         AndroidResource androidResource) throws IllegalArgumentException {
        loadMesh(callback, androidResource, DEFAULT_PRIORITY);
    }

    /**
     * Loads a mesh file, asynchronously, at an explicit priority.
     * <p/>
     * This method and the
     * {@linkplain #loadMesh(AndroidResource.MeshCallback, AndroidResource)
     * overload that supplies a default priority} are generally going to be your
     * best choices for loading {@link Mesh} resources: mesh loading can take
     * hundreds - and even thousands - of milliseconds, and so should not be
     * done on the GL thread in either {@link MeganekkoActivity#oneTimeInit()}
     * or {@link MeganekkoActivity#frame()}.
     * <p/>
     * <p/>
     * The asynchronous methods improve throughput in three ways. First, by
     * doing all the work on a background thread, then delivering the loaded
     * mesh to the GL thread on a {@link #runOnGlThread(Runnable)
     * runOnGlThread()} callback. Second, they use a throttler to avoid
     * overloading the system and/or running out of memory. Third, they do
     * 'request consolidation' - if you issue any requests for a particular file
     * while there is still a pending request, the file will only be read once,
     * and each callback will get the same {@link Mesh}.
     *
     * @param callback App supplied callback, with three different methods.
     *                 <ul>
     *                 <li>Before loading, Meganekko may call
     *                 {@link AndroidResource.MeshCallback#stillWanted(AndroidResource)
     *                 stillWanted()} (on a background thread) to give you a chance
     *                 to abort a 'stale' load.
     *                 <p/>
     *                 <li>Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                 loaded()} on the GL thread.
     *                 <p/>
     *                 <li>Any errors will call
     *                 {@link AndroidResource.MeshCallback#failed(Throwable, AndroidResource)
     *                 failed(),} with no promises about threading.
     *                 </ul>
     * @param resource Basically, a stream containing a 3D model. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param priority This request's priority. Please see the notes on asynchronous
     *                 priorities in the <a href="package-summary.html#async">package
     *                 description</a>.
     * @throws IllegalArgumentException If either {@code callback} or {@code resource} is
     *                                  {@code null}, or if {@code priority} is out of range - or if
     *                                  you 'abuse' request consolidation by passing the same
     *                                  {@link AndroidResource} descriptor to multiple load calls.
     *                                  <p/>
     *                                  It's fairly common for multiple scene objects to use the same
     *                                  texture or the same mesh. Thus, if you try to load, say,
     *                                  {@code R.raw.whatever} while you already have a pending
     *                                  request for {@code R.raw.whatever}, it will only be loaded
     *                                  once; the same resource will be used to satisfy both (all)
     *                                  requests. This "consolidation" uses
     *                                  {@link AndroidResource#equals(Object)}, <em>not</em>
     *                                  {@code ==} (aka "reference equality"): The problem with using
     *                                  the same resource descriptor is that if requests can't be
     *                                  consolidated (because the later one(s) came in after the
     *                                  earlier one(s) had already completed) the resource will be
     *                                  reloaded ... but the original descriptor will have been
     *                                  closed.
     */
    public void loadMesh(MeshCallback callback, AndroidResource resource,
                         int priority) throws IllegalArgumentException {
        AsynchronousResourceLoader.loadMesh(this, callback, resource,
                priority);
    }

    /**
     * Simple, high-level method to load a mesh asynchronously, for use with
     * {@link RenderData#setMesh(Future)}.
     * <p/>
     * This method uses a default priority; use
     * {@link #loadFutureMesh(AndroidResource, int)} to specify a priority; use
     * one of the lower-level
     * {@link #loadMesh(AndroidResource.MeshCallback, AndroidResource)} methods
     * to get more control over loading.
     *
     * @param resource Basically, a stream containing a 3D model. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @return A {@link Future} that you can pass to
     * {@link RenderData#setMesh(Future)}
     * @throws IllegalArgumentException If you 'abuse' request consolidation by passing the same
     *                                  {@link AndroidResource} descriptor to multiple load calls.
     *                                  <p/>
     *                                  It's fairly common for multiple scene objects to use the same
     *                                  texture or the same mesh. Thus, if you try to load, say,
     *                                  {@code R.raw.whatever} while you already have a pending
     *                                  request for {@code R.raw.whatever}, it will only be loaded
     *                                  once; the same resource will be used to satisfy both (all)
     *                                  requests. This "consolidation" uses
     *                                  {@link AndroidResource#equals(Object)}, <em>not</em>
     *                                  {@code ==} (aka "reference equality"): The problem with using
     *                                  the same resource descriptor is that if requests can't be
     *                                  consolidated (because the later one(s) came in after the
     *                                  earlier one(s) had already completed) the resource will be
     *                                  reloaded ... but the original descriptor will have been
     *                                  closed.
     */
    public Future<Mesh> loadFutureMesh(AndroidResource resource) {
        return loadFutureMesh(resource, DEFAULT_PRIORITY);
    }

    /**
     * Simple, high-level method to load a mesh asynchronously, for use with
     * {@link RenderData#setMesh(Future)}.
     * <p/>
     * This method trades control for convenience; use one of the lower-level
     * {@link #loadMesh(AndroidResource.MeshCallback, AndroidResource)} methods
     * if, say, you want to do something more than just
     * {@link RenderData#setMesh(Mesh)} when the mesh loads.
     *
     * @param resource Basically, a stream containing a 3D model. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param priority This request's priority. Please see the notes on asynchronous
     *                 priorities in the <a href="package-summary.html#async">package
     *                 description</a>.
     * @return A {@link Future} that you can pass to
     * {@link RenderData#setMesh(Future)}
     * @throws IllegalArgumentException If you 'abuse' request consolidation by passing the same
     *                                  {@link AndroidResource} descriptor to multiple load calls.
     *                                  <p/>
     *                                  It's fairly common for multiple scene objects to use the same
     *                                  texture or the same mesh. Thus, if you try to load, say,
     *                                  {@code R.raw.whatever} while you already have a pending
     *                                  request for {@code R.raw.whatever}, it will only be loaded
     *                                  once; the same resource will be used to satisfy both (all)
     *                                  requests. This "consolidation" uses
     *                                  {@link AndroidResource#equals(Object)}, <em>not</em>
     *                                  {@code ==} (aka "reference equality"): The problem with using
     *                                  the same resource descriptor is that if requests can't be
     *                                  consolidated (because the later one(s) came in after the
     *                                  earlier one(s) had already completed) the resource will be
     *                                  reloaded ... but the original descriptor will have been
     *                                  closed.
     */
    public Future<Mesh> loadFutureMesh(AndroidResource resource,
                                       int priority) {
        return AsynchronousResourceLoader.loadFutureMesh(this, resource,
                priority);
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
     * Throws an exception if the current thread is not a GL thread.
     */
    private void assertGLThread() {

        if (Thread.currentThread().getId() != mGLThreadID) {
            throw new RuntimeException(
                    "Should not run GL functions from a non-GL thread!");
        }

    }

    /**
     * @deprecated Use {@link MeganekkoActivity#getScene()}.
     */
    @Deprecated
    public Scene getMainScene() {
        return getActivity().getScene();
    }

    /**
     * @deprecated Use {@link MeganekkoActivity#setScene(Scene)}
     */
    @Deprecated
    synchronized void setMainScene(Scene scene) {
        getActivity().setScene(scene);
    }

    /**
     * @deprecated Use {@link MeganekkoActivity#runOnGlThread(Runnable)}.
     */
    public void runOnGlThread(Runnable runnable) {
        getActivity().runOnGlThread(runnable);
    }

    /**
     * The {@linkplain AnimationEngine animation engine} singleton.
     * <p/>
     * Use the animation engine to start and stop {@linkplain Animation
     * animations}.
     *
     * @return The {@linkplain AnimationEngine animation engine} singleton.
     */
    @Deprecated
    public AnimationEngine getAnimationEngine() {
        return AnimationEngine.getInstance(this);
    }

    /**
     * The {@linkplain PeriodicEngine periodic engine} singleton.
     * <p/>
     * Use the periodic engine to schedule {@linkplain Runnable runnables} to
     * run on the GL thread at a future time.
     *
     * @return The {@linkplain PeriodicEngine periodic engine} singleton.
     */
    public PeriodicEngine getPeriodicEngine() {
        return PeriodicEngine.getInstance(this);
    }

    /**
     * Called when the surface changed size. When
     * setPreserveEGLContextOnPause(true) is called in the surface, this is
     * called only once.
     */
    void onSurfaceCreated() {
        Log.v(TAG, "onSurfaceCreated");

        Thread currentThread = Thread.currentThread();

        // Reduce contention with other Android processes
        currentThread.setPriority(Thread.MAX_PRIORITY);

        // we know that the current thread is a GL one, so we store it to
        // prevent non-GL thread from calling GL functions
        mGLThreadID = currentThread.getId();

        // Evaluating anisotropic support on GL Thread
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        isAnisotropicSupported = extensions
                .contains("GL_EXT_texture_filter_anisotropic");

        /*
         * GL Initializations.
         */
//        nativeSetShaderManager(nativePtr, getMaterialShaderManager().getNative());
    }
}
