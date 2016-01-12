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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.eje_c.meganekko.AndroidResource.BitmapTextureCallback;
import com.eje_c.meganekko.AndroidResource.CompressedTextureCallback;
import com.eje_c.meganekko.AndroidResource.MeshCallback;
import com.eje_c.meganekko.AndroidResource.TextureCallback;
import com.eje_c.meganekko.Material.ShaderType;
import com.eje_c.meganekko.animation.Animation;
import com.eje_c.meganekko.animation.AnimationEngine;
import com.eje_c.meganekko.asynchronous.AsynchronousResourceLoader;
import com.eje_c.meganekko.asynchronous.CompressedTextureLoader;
import com.eje_c.meganekko.asynchronous.GVRCompressedTexture;
import com.eje_c.meganekko.jassimp.AiColor;
import com.eje_c.meganekko.jassimp.AiMaterial;
import com.eje_c.meganekko.jassimp.AiMatrix4f;
import com.eje_c.meganekko.jassimp.AiNode;
import com.eje_c.meganekko.jassimp.AiScene;
import com.eje_c.meganekko.jassimp.AiTextureType;
import com.eje_c.meganekko.jassimp.AiWrapperProvider;
import com.eje_c.meganekko.periodic.PeriodicEngine;
import com.eje_c.meganekko.utility.Log;
import com.eje_c.meganekko.utility.ResourceCache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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
    private final static ResourceCache<Texture> sTextureCache = new ResourceCache<Texture>();
    private static final List<Runnable> sHandlers = new ArrayList<Runnable>();
    /**
     * The default texture parameter instance for overloading texture methods
     */
    public final TextureParameters DEFAULT_TEXTURE_PARAMETERS = new TextureParameters(
            this);
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

        // Clear singletons and per-run data structures
        resetOnRestart();

        AsynchronousResourceLoader.setup(this);
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
            AssimpImporter assimpImporter = Importer
                    .readFileFromResources(this, androidResource, settings);
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
     * Simple, high-level method to load a scene as {@link SceneObject} from 3D
     * model.
     *
     * @param assetRelativeFilename A filename, relative to the {@code assets} directory. The file
     *                              can be in a sub-directory of the {@code assets} directory:
     *                              {@code "foo/bar.png"} will open the file
     *                              {@code assets/foo/bar.png}
     * @return A {@link SceneObject} that contains the meshes with textures
     * @throws IOException File does not exist or cannot be read
     */
    public SceneObject getAssimpModel(String assetRelativeFilename)
            throws IOException {
        return getAssimpModel(assetRelativeFilename,
                ImportSettings.getRecommendedSettings());
    }

    /**
     * Simple, high-level method to load a scene as {@link SceneObject} from 3D
     * model.
     *
     * @param assetRelativeFilename A filename, relative to the {@code assets} directory. The file
     *                              can be in a sub-directory of the {@code assets} directory:
     *                              {@code "foo/bar.png"} will open the file
     *                              {@code assets/foo/bar.png}
     * @param settings              Additional import {@link ImportSettings settings}
     * @return A {@link SceneObject} that contains the meshes with textures
     * @throws IOException File does not exist or cannot be read
     */
    public SceneObject getAssimpModel(String assetRelativeFilename,
                                      EnumSet<ImportSettings> settings) throws IOException {
        AssimpImporter assimpImporter = Importer.readFileFromResources(
                this, new AndroidResource(this, assetRelativeFilename),
                settings);

        SceneObject wholeSceneObject = new SceneObject(this);

        AiScene assimpScene = assimpImporter.getAssimpScene();

        AiWrapperProvider<byte[], AiMatrix4f, AiColor, AiNode, byte[]> wrapperProvider = new AiWrapperProvider<byte[], AiMatrix4f, AiColor, AiNode, byte[]>() {

            /**
             * Wraps a RGBA color.
             * <p>
             *
             * A color consists of 4 float values (r,g,b,a) starting from offset
             *
             * @param buffer
             *            the buffer to wrap
             * @param offset
             *            the offset into buffer
             * @return the wrapped color
             */
            @Override
            public AiColor wrapColor(ByteBuffer buffer, int offset) {
                AiColor color = new AiColor(buffer, offset);
                return color;
            }

            /**
             * Wraps a 4x4 matrix of floats.
             * <p>
             *
             * The calling code will allocate a new array for each invocation of
             * this method. It is safe to store a reference to the passed in
             * array and use the array to store the matrix data.
             *
             * @param data
             *            the matrix data in row-major order
             * @return the wrapped matrix
             */
            @Override
            public AiMatrix4f wrapMatrix4f(float[] data) {

                AiMatrix4f transformMatrix = new AiMatrix4f(data);
                return transformMatrix;
            }

            /**
             * Wraps a quaternion.
             * <p>
             *
             * A quaternion consists of 4 float values (w,x,y,z) starting from
             * offset
             *
             * @param buffer
             *            the buffer to wrap
             * @param offset
             *            the offset into buffer
             * @return the wrapped quaternion
             */
            @Override
            public byte[] wrapQuaternion(ByteBuffer buffer, int offset) {
                byte[] quaternion = new byte[4];
                buffer.get(quaternion, offset, 4);
                return quaternion;
            }

            /**
             * Wraps a scene graph node.
             * <p>
             *
             * See {@link AiNode} for a description of the scene graph structure
             * used by assimp.
             * <p>
             *
             * The parent node is either null or an instance returned by this
             * method. It is therefore safe to cast the passed in parent object
             * to the implementation specific type
             *
             * @param parent
             *            the parent node
             * @param matrix
             *            the transformation matrix
             * @param meshReferences
             *            array of mesh references (indexes)
             * @param name
             *            the name of the node
             * @return the wrapped scene graph node
             */
            @Override
            public AiNode wrapSceneNode(Object parent, Object matrix,
                                        int[] meshReferences, String name) {

                AiNode node = new AiNode(null, matrix, meshReferences, name);

                return node;
            }

            /**
             * Wraps a vector.
             * <p>
             *
             * Most vectors are 3-dimensional, i.e., with 3 components. The
             * exception are texture coordinates, which may be 1- or
             * 2-dimensional. A vector consists of numComponents floats (x,y,z)
             * starting from offset
             *
             * @param buffer
             *            the buffer to wrap
             * @param offset
             *            the offset into buffer
             * @param numComponents
             *            the number of components
             * @return the wrapped vector
             */
            @Override
            public byte[] wrapVector3f(ByteBuffer buffer, int offset,
                                       int numComponents) {
                byte[] warpedVector = new byte[numComponents];
                buffer.get(warpedVector, offset, numComponents);
                return warpedVector;
            }
        };

        AiNode rootNode = assimpScene.getSceneRoot(wrapperProvider);

        // Recurse through the entire hierarchy to attache all the meshes as
        // Scene Object
        this.recurseAssimpNodes(assetRelativeFilename, wholeSceneObject,
                rootNode, wrapperProvider);

        return wholeSceneObject;
    }

    /**
     * Helper method to recurse through all the assimp nodes and get all their
     * meshes that can be used to create {@link SceneObject} to be attached to
     * the set of complete scene objects for the assimp model.
     *
     * @param assetRelativeFilename A filename, relative to the {@code assets} directory. The file
     *                              can be in a sub-directory of the {@code assets} directory:
     *                              {@code "foo/bar.png"} will open the file
     *                              {@code assets/foo/bar.png}
     * @param parentSceneObject     A reference of the {@link SceneObject}, to which all other
     *                              scene objects are attached.
     * @param node                  A reference to the AiNode for which we want to recurse all its
     *                              children and meshes.
     * @param wrapperProvider       AiWrapperProvider for unwrapping Jassimp properties.
     */
    @SuppressWarnings("resource")
    private void recurseAssimpNodes(
            String assetRelativeFilename,
            SceneObject parentSceneObject,
            AiNode node,
            AiWrapperProvider<byte[], AiMatrix4f, AiColor, AiNode, byte[]> wrapperProvider) {
        try {
            SceneObject newParentSceneObject = new SceneObject(this);
            if (node.getNumMeshes() == 0) {
                parentSceneObject.addChildObject(newParentSceneObject);
                parentSceneObject = newParentSceneObject;
            } else if (node.getNumMeshes() == 1) {
                // add the scene object to the scene graph
                SceneObject sceneObject = createSceneObject(
                        assetRelativeFilename, node, 0, wrapperProvider);
                parentSceneObject.addChildObject(sceneObject);
                parentSceneObject = sceneObject;
            } else {
                for (int i = 0; i < node.getNumMeshes(); i++) {
                    SceneObject sceneObject = createSceneObject(
                            assetRelativeFilename, node, i, wrapperProvider);
                    newParentSceneObject.addChildObject(sceneObject);
                }
                parentSceneObject.addChildObject(newParentSceneObject);
                parentSceneObject = newParentSceneObject;
            }
            for (int i = 0; i < node.getNumChildren(); i++) {
                this.recurseAssimpNodes(assetRelativeFilename,
                        parentSceneObject, node.getChildren().get(i),
                        wrapperProvider);
            }
        } catch (Exception e) {
            // Error while recursing the Scene Graph
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create a new {@link SceneObject} with the mesh at the
     * index {@link index} of the node mesh array with a color or texture
     * material.
     *
     * @param assetRelativeFilename A filename, relative to the {@code assets} directory. The file
     *                              can be in a sub-directory of the {@code assets} directory:
     *                              {@code "foo/bar.png"} will open the file
     *                              {@code assets/foo/bar.png}
     * @param node                  A reference to the AiNode for which we want to recurse all its
     *                              children and meshes.
     * @param index                 The index of the mesh in the array of meshes for that node.
     * @param wrapperProvider       AiWrapperProvider for unwrapping Jassimp properties.
     * @return The new {@link SceneObject} with the mesh at the index
     * {@link index} for the node {@link node}
     * @throws IOException File does not exist or cannot be read
     */
    private SceneObject createSceneObject(
            String assetRelativeFilename,
            AiNode node,
            int index,
            AiWrapperProvider<byte[], AiMatrix4f, AiColor, AiNode, byte[]> wrapperProvider)
            throws IOException {

        FutureWrapper<Mesh> futureMesh = new FutureWrapper<Mesh>(
                this.getNodeMesh(new AndroidResource(this,
                        assetRelativeFilename), node.getName(), index));

        AiMaterial material = this.getMeshMaterial(new AndroidResource(this,
                assetRelativeFilename), node.getName(), index);

        Material meshMaterial = new Material(this,
                ShaderType.Assimp.ID);

        /* Feature set */
        int assimpFeatureSet = 0x00000000;

        /* Diffuse color */
        AiColor diffuseColor = material.getDiffuseColor(wrapperProvider);
        meshMaterial.setDiffuseColor(diffuseColor.getRed(),
                diffuseColor.getGreen(), diffuseColor.getBlue(),
                diffuseColor.getAlpha());

        /* Specular color */
        AiColor specularColor = material.getSpecularColor(wrapperProvider);
        meshMaterial.setSpecularColor(specularColor.getRed(),
                specularColor.getGreen(), specularColor.getBlue(),
                specularColor.getAlpha());

        /* Ambient color */
        AiColor ambientColor = material.getAmbientColor(wrapperProvider);
        meshMaterial.setAmbientColor(ambientColor.getRed(),
                ambientColor.getGreen(), ambientColor.getBlue(),
                ambientColor.getAlpha());

        /* Emissive color */
        AiColor emissiveColor = material.getEmissiveColor(wrapperProvider);
        meshMaterial.setVec4("emissive_color", emissiveColor.getRed(),
                emissiveColor.getGreen(), emissiveColor.getBlue(),
                emissiveColor.getAlpha());

        /* Opacity */
        float opacity = material.getOpacity();
        meshMaterial.setOpacity(opacity);

        /* Diffuse Texture */
        String texDiffuseFileName = material.getTextureFile(
                AiTextureType.DIFFUSE, 0);
        if (texDiffuseFileName != null && !texDiffuseFileName.isEmpty()) {
            assimpFeatureSet = ShaderType.Assimp.setBit(assimpFeatureSet,
                    ShaderType.Assimp.AS_DIFFUSE_TEXTURE);
            Future<Texture> futureDiffuseTexture = this
                    .loadFutureTexture(new AndroidResource(this,
                            texDiffuseFileName));
            meshMaterial.setMainTexture(futureDiffuseTexture);
        }

        /* Apply feature set to the material */
        meshMaterial.setShaderFeatureSet(assimpFeatureSet);

        SceneObject sceneObject = new SceneObject(this);
        RenderData sceneObjectRenderData = new RenderData(this);
        sceneObjectRenderData.setMesh(futureMesh);
        sceneObjectRenderData.setMaterial(meshMaterial);
        sceneObject.attachRenderData(sceneObjectRenderData);
        return sceneObject;
    }

    /**
     * Retrieves the particular index mesh for the given node.
     *
     * @return The mesh, encapsulated as a {@link Mesh}.
     */
    public Mesh getNodeMesh(AndroidResource androidResource,
                            String nodeName, int meshIndex) {
        AssimpImporter assimpImporter = Importer.readFileFromResources(
                this, androidResource,
                ImportSettings.getRecommendedSettings());
        return assimpImporter.getNodeMesh(nodeName, meshIndex);
    }

    /**
     * Retrieves the material for the mesh of the given node..
     *
     * @return The material, encapsulated as a {@link AiMaterial}.
     */
    public AiMaterial getMeshMaterial(AndroidResource androidResource,
                                      String nodeName, int meshIndex) {
        AssimpImporter assimpImporter = Importer.readFileFromResources(
                this, androidResource,
                ImportSettings.getRecommendedSettings());
        return assimpImporter.getMeshMaterial(nodeName, meshIndex);
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
        Mesh mesh = new Mesh(this);

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
     * Loads file placed in the assets folder, as a {@link Bitmap}.
     * <p/>
     * <p/>
     * Note that this method may take hundreds of milliseconds to return: unless
     * the bitmap is quite tiny, you probably don't want to call this directly
     * from your callback as that is called
     * once per frame, and a long call will cause you to miss frames.
     * <p/>
     * <p/>
     * Note also that this method does no scaling, and will return a full-size
     * {@link Bitmap}. Loading (say) an unscaled photograph may abort your app:
     * Use pre-scaled images, or {@link BitmapFactory} methods which give you
     * more control over the image size.
     *
     * @param fileName The name of a file, relative to the assets directory. The
     *                 assets directory may contain an arbitrarily complex tree of
     *                 subdirectories; the file name can specify any location in or
     *                 under the assets directory.
     * @return The file as a bitmap, or {@code null} if file path does not exist
     * or the file can not be decoded into a Bitmap.
     */
    public Bitmap loadBitmap(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("File name should not be null.");
        }
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            try {
                stream = mContext.getAssets().open(fileName);
                return bitmap = BitmapFactory.decodeStream(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Don't discard a valid Bitmap because of an IO error closing the
            // file!
            return bitmap;
        }
    }

    /**
     * Loads file placed in the assets folder, as a {@link BitmapTexture}.
     * <p/>
     * <p/>
     * Note that this method may take hundreds of milliseconds to return: unless
     * the texture is quite tiny, you probably don't want to call this directly
     * from your callback as that is called
     * once per frame, and a long call will cause you to miss frames. For large
     * images, you should use either
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)
     * loadBitmapTexture()} (faster) or
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * (fastest <em>and</em> least memory pressure).
     * <p/>
     * <p/>
     * Note also that this method does no scaling, and will return a full-size
     * {@link Bitmap}. Loading (say) an unscaled photograph may abort your app:
     * Use
     * <ul>
     * <li>Pre-scaled images
     * <li>{@link BitmapFactory} methods which give you more control over the
     * image size, or
     * <li>{@link #loadTexture(AndroidResource)} or
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)}
     * which automatically scale large images to fit the GPU's restrictions and
     * to avoid {@linkplain OutOfMemoryError out of memory errors.}
     * </ul>
     *
     * @param fileName The name of a file, relative to the assets directory. The
     *                 assets directory may contain an arbitrarily complex tree of
     *                 sub-directories; the file name can specify any location in or
     *                 under the assets directory.
     * @return The file as a texture, or {@code null} if file path does not
     * exist or the file can not be decoded into a Bitmap.
     * @deprecated We will remove this uncached, blocking function during Q3 of
     * 2015. We suggest that you switch to
     * {@link #loadTexture(AndroidResource)}
     */
    public BitmapTexture loadTexture(String fileName) {
        return loadTexture(fileName, DEFAULT_TEXTURE_PARAMETERS);
    }

    /**
     * Loads file placed in the assets folder, as a {@link BitmapTexture} with
     * the user provided texture parameters.
     * <p/>
     * <p/>
     * Note that this method may take hundreds of milliseconds to return: unless
     * the texture is quite tiny, you probably don't want to call this directly
     * from your callback as that is called
     * once per frame, and a long call will cause you to miss frames. For large
     * images, you should use either
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)
     * loadBitmapTexture()} (faster) or
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * (fastest <em>and</em> least memory pressure).
     * <p/>
     * <p/>
     * Note also that this method does no scaling, and will return a full-size
     * {@link Bitmap}. Loading (say) an unscaled photograph may abort your app:
     * Use
     * <ul>
     * <li>Pre-scaled images
     * <li>{@link BitmapFactory} methods which give you more control over the
     * image size, or
     * <li>{@link #loadTexture(AndroidResource)} or
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)}
     * which automatically scale large images to fit the GPU's restrictions and
     * to avoid {@linkplain OutOfMemoryError out of memory errors.}
     * </ul>
     *
     * @param fileName          The name of a file, relative to the assets directory. The
     *                          assets directory may contain an arbitrarily complex tree of
     *                          sub-directories; the file name can specify any location in or
     *                          under the assets directory.
     * @param textureParameters The texture parameter object which has all the values that
     *                          were provided by the user for texture enhancement. The
     *                          {@link TextureParameters} class has methods to set all the
     *                          texture filters and wrap states.
     * @return The file as a texture, or {@code null} if file path does not
     * exist or the file can not be decoded into a Bitmap.
     * @deprecated We will remove this uncached, blocking function during Q3 of
     * 2015. We suggest that you switch to
     * {@link #loadTexture(AndroidResource)}
     */
    public BitmapTexture loadTexture(String fileName,
                                     TextureParameters textureParameters) {

        assertGLThread();

        if (fileName.endsWith(".png")) {// load png directly to texture
            return new BitmapTexture(this, fileName);
        }

        Bitmap bitmap = loadBitmap(fileName);
        return bitmap == null ? null : new BitmapTexture(this, bitmap,
                textureParameters);
    }

    /**
     * Loads file placed in the assets folder, as a {@link BitmapTexture}.
     * <p/>
     * <p/>
     * Note that this method may take hundreds of milliseconds to return: unless
     * the texture is quite tiny, you probably don't want to call this directly
     * from your callback as that is called
     * once per frame, and a long call will cause you to miss frames. For large
     * images, you should use either
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)
     * loadBitmapTexture()} (faster) or
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * (fastest <em>and</em> least memory pressure).
     * <p/>
     * <p/>
     * This method automatically scales large images to fit the GPU's
     * restrictions and to avoid {@linkplain OutOfMemoryError out of memory
     * errors.}
     * </ul>
     *
     * @param resource Basically, a stream containing a bitmap texture. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @return The file as a texture, or {@code null} if the file can not be
     * decoded into a Bitmap.
     */
    public Texture loadTexture(AndroidResource resource) {
        return loadTexture(resource, DEFAULT_TEXTURE_PARAMETERS);
    }

    /**
     * Loads file placed in the assets folder, as a {@link BitmapTexture} with
     * the user provided texture parameters.
     * <p/>
     * <p/>
     * Note that this method may take hundreds of milliseconds to return: unless
     * the texture is quite tiny, you probably don't want to call this directly
     * from your callback as that is called
     * once per frame, and a long call will cause you to miss frames. For large
     * images, you should use either
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)
     * loadBitmapTexture()} (faster) or
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * (fastest <em>and</em> least memory pressure).
     * <p/>
     * <p/>
     * This method automatically scales large images to fit the GPU's
     * restrictions and to avoid {@linkplain OutOfMemoryError out of memory
     * errors.}
     * </ul>
     *
     * @param resource          Basically, a stream containing a bitmap texture. The
     *                          {@link AndroidResource} class has six constructors to handle a
     *                          wide variety of Android resource types. Taking a
     *                          {@code AndroidResource} here eliminates six overloads.
     * @param textureParameters The texture parameter object which has all the values that
     *                          were provided by the user for texture enhancement. The
     *                          {@link TextureParameters} class has methods to set all the
     *                          texture filters and wrap states.
     * @return The file as a texture, or {@code null} if the file can not be
     * decoded into a Bitmap.
     */
    public Texture loadTexture(AndroidResource resource,
                               TextureParameters textureParameters) {

        Texture texture = sTextureCache.get(resource);
        if (texture == null) {
            assertGLThread();

            Bitmap bitmap = AsynchronousResourceLoader.decodeStream(
                    resource.getStream(), false);
            resource.closeStream();
            texture = bitmap == null ? null : new BitmapTexture(this,
                    bitmap, textureParameters);
            if (texture != null) {
                sTextureCache.put(resource, texture);
            }
        }
        return texture;
    }

    /**
     * Loads a cube map texture synchronously.
     * <p/>
     * <p/>
     * Note that this method may take hundreds of milliseconds to return: unless
     * the cube map is quite tiny, you probably don't want to call this directly
     * from your callback as that is called
     * once per frame, and a long call will cause you to miss frames.
     *
     * @param resourceArray An array containing six resources for six bitmaps. The order
     *                      of the bitmaps is important to the correctness of the cube map
     *                      texture. The six bitmaps should correspond to +x, -x, +y, -y,
     *                      +z, and -z faces of the cube map texture respectively.
     * @return The cube map texture, or {@code null} if the length of
     * rsourceArray is not 6.
     */
    /*
     * TODO Deprecate, and replace with an overload that takes a single
     * AndroidResource which specifies a zip file ... and caches result
     */
    public CubemapTexture loadCubemapTexture(
            AndroidResource[] resourceArray) {
        return loadCubemapTexture(resourceArray, DEFAULT_TEXTURE_PARAMETERS);
    }

    // Texture parameters
    public CubemapTexture loadCubemapTexture(
            AndroidResource[] resourceArray,
            TextureParameters textureParameters) {

        assertGLThread();

        if (resourceArray.length != 6) {
            return null;
        }

        Bitmap[] bitmapArray = new Bitmap[6];
        for (int i = 0; i < 6; i++) {
            bitmapArray[i] = AsynchronousResourceLoader.decodeStream(
                    resourceArray[i].getStream(), false);
            resourceArray[i].closeStream();
        }

        return new CubemapTexture(this, bitmapArray, textureParameters);
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
     * Load a bitmap, asynchronously, with a default priority.
     * <p/>
     * Because it is asynchronous, this method <em>is</em> a bit harder to use
     * than {@link #loadTexture(String)}, but it moves a large amount of work
     * (in {@link BitmapFactory#decodeStream(InputStream)} from the GL thread to
     * a background thread. Since you <em>can</em> create a {@link SceneObject}
     * without a mesh and texture - and set them later - using the asynchronous
     * API can improve startup speed and/or reduce frame misses . This API may also
     * use less RAM than {@link #loadTexture(String)}.
     * <p/>
     * <p/>
     * This API will 'consolidate' requests: If you request a texture like
     * {@code R.raw.wood_grain} and then - before it has loaded - issue another
     * request for {@code R.raw.wood_grain}, Meganekko will only read the bitmap
     * file once; only create a single {@link Texture}; and then call both
     * callbacks, passing each the same texture.
     * <p/>
     * <p/>
     * Please be aware that {@link BitmapFactory#decodeStream(InputStream)} is a
     * comparatively expensive operation: it can take hundreds of milliseconds
     * and use several megabytes of temporary RAM. Meganekko includes a
     * throttler to keep the total load manageable - but
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * is <em>much</em> faster and lighter-weight: that API simply loads the
     * compressed texture into a small amount RAM (which doesn't take very long)
     * and does some simple parsing to figure out the parameters to pass
     * {@code glCompressedTexImage2D()}. The GL hardware does the decoding much
     * faster than Android's {@link BitmapFactory}!
     * <p/>
     * <p/>
     * TODO Take a boolean parameter that controls mipmap generation?
     *
     * @param callback Before loading, Meganekko may call
     *                 {@link AndroidResource.BitmapTextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} several times (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                 loaded()} on the GL thread;
     *                 <p/>
     *                 any errors will call
     *                 {@link AndroidResource.BitmapTextureCallback#failed(Throwable, AndroidResource)
     *                 failed()}, with no promises about threading.
     *                 <p/>
     *                 <p/>
     *                 This method uses a throttler to avoid overloading the system.
     *                 If the throttler has threads available, it will run this
     *                 request immediately. Otherwise, it will enqueue the request,
     *                 and call
     *                 {@link AndroidResource.BitmapTextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} at least once (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     * @param resource Basically, a stream containing a bitmapped image. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
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
    public void loadBitmapTexture(BitmapTextureCallback callback,
                                  AndroidResource resource) {
        loadBitmapTexture(callback, resource, DEFAULT_PRIORITY);
    }

    /**
     * Load a bitmap, asynchronously, with an explicit priority.
     * <p/>
     * Because it is asynchronous, this method <em>is</em> a bit harder to use
     * than {@link #loadTexture(String)}, but it moves a large amount of work
     * (in {@link BitmapFactory#decodeStream(InputStream)} from the GL thread to
     * a background thread. Since you <em>can</em> create a {@link SceneObject}
     * without a mesh and texture - and set them later - using the asynchronous
     * API can improve startup speed and/or reduce frame misses.
     * <p/>
     * <p/>
     * This API will 'consolidate' requests: If you request a texture like
     * {@code R.raw.wood_grain} and then - before it has loaded - issue another
     * request for {@code R.raw.wood_grain}, Meganekko will only read the bitmap
     * file once; only create a single {@link Texture}; and then call both
     * callbacks, passing each the same texture.
     * <p/>
     * <p/>
     * Please be aware that {@link BitmapFactory#decodeStream(InputStream)} is a
     * comparatively expensive operation: it can take hundreds of milliseconds
     * and use several megabytes of temporary RAM. Meganekko includes a
     * throttler to keep the total load manageable - but
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)}
     * is <em>much</em> faster and lighter-weight: that API simply loads the
     * compressed texture into a small amount RAM (which doesn't take very long)
     * and does some simple parsing to figure out the parameters to pass
     * {@code glCompressedTexImage2D()}. The GL hardware does the decoding much
     * faster than Android's {@link BitmapFactory}!
     *
     * @param callback Before loading, Meganekko may call
     *                 {@link AndroidResource.BitmapTextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} several times (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                 loaded()} on the GL thread;
     *                 <p/>
     *                 any errors will call
     *                 {@link AndroidResource.BitmapTextureCallback#failed(Throwable, AndroidResource)
     *                 failed()}, with no promises about threading.
     *                 <p/>
     *                 <p/>
     *                 This method uses a throttler to avoid overloading the system.
     *                 If the throttler has threads available, it will run this
     *                 request immediately. Otherwise, it will enqueue the request,
     *                 and call
     *                 {@link AndroidResource.BitmapTextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} at least once (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     * @param resource Basically, a stream containing a bitmapped image. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param priority This request's priority. Please see the notes on asynchronous
     *                 priorities in the <a href="package-summary.html#async">package
     *                 description</a>.
     * @throws IllegalArgumentException If {@code priority} {@literal <} {@link #LOWEST_PRIORITY} or
     *                                  {@literal >} {@link #HIGHEST_PRIORITY}, or either of the
     *                                  other parameters is {@code null} - or if you 'abuse' request
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
    public void loadBitmapTexture(BitmapTextureCallback callback,
                                  AndroidResource resource, int priority)
            throws IllegalArgumentException {
        AsynchronousResourceLoader.loadBitmapTexture(this, sTextureCache,
                callback, resource, priority);
    }

    /**
     * Load a compressed texture, asynchronously.
     * <p/>
     * Meganekko currently supports ASTC, ETC2, and KTX formats: applications
     * can add new formats by implementing {@link CompressedTextureLoader}.
     * <p/>
     * <p/>
     * This method uses the fastest possible rendering. To specify higher
     * quality (but slower) rendering, you can use the
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource, int)}
     * overload.
     *
     * @param callback Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                 loaded()} on the GL thread; any errors will call
     *                 {@link AndroidResource.CompressedTextureCallback#failed(Throwable, AndroidResource)
     *                 failed()}, with no promises about threading.
     * @param resource Basically, a stream containing a compressed texture. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
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
    public void loadCompressedTexture(CompressedTextureCallback callback,
                                      AndroidResource resource) {
        AsynchronousResourceLoader.loadCompressedTexture(this,
                sTextureCache, callback, resource);
    }

    /**
     * Load a compressed texture, asynchronously.
     * <p/>
     * Meganekko currently supports ASTC, ETC2, and KTX formats: applications
     * can add new formats by implementing {@link CompressedTextureLoader}.
     *
     * @param callback Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)}
     *                 on the GL thread; any errors will call
     *                 {@link AndroidResource.CompressedTextureCallback#failed(Throwable, AndroidResource)}
     *                 , with no promises about threading.
     * @param resource Basically, a stream containing a compressed texture. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param quality  Speed/quality tradeoff: should be one of
     *                 {@link GVRCompressedTexture#SPEED},
     *                 {@link GVRCompressedTexture#BALANCED}, or
     *                 {@link GVRCompressedTexture#QUALITY}, but other values are
     *                 'clamped' to one of the recognized values.
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
    public void loadCompressedTexture(CompressedTextureCallback callback,
                                      AndroidResource resource, int quality) {
        AsynchronousResourceLoader.loadCompressedTexture(this,
                sTextureCache, callback, resource, quality);
    }

    /**
     * A simplified, low-level method that loads a texture asynchronously,
     * without making you specify
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)
     * loadBitmapTexture()} or
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)
     * loadCompressedTexture()}.
     * <p/>
     * This method can detect whether the resource file holds a compressed
     * texture (Meganekko currently supports ASTC, ETC2, and KTX formats:
     * applications can add new formats by implementing
     * {@link CompressedTextureLoader}): if the file is not a compressed
     * texture, it is loaded as a normal, bitmapped texture. This format
     * detection adds very little to the cost of loading even a compressed
     * texture, and it makes your life a lot easier: you can replace, say,
     * {@code res/raw/resource.png} with {@code res/raw/resource.etc2} without
     * having to change any code.
     * <p/>
     * <p/>
     * This method uses a default priority and a default render quality: Use
     * {@link #loadTexture(AndroidResource.TextureCallback, AndroidResource, int)}
     * to specify an explicit priority, and
     * {@link #loadTexture(AndroidResource.TextureCallback, AndroidResource, int, int)}
     * to specify an explicit quality.
     * <p/>
     * <p/>
     * We will continue to support the {@code loadBitmapTexture()} and
     * {@code loadCompressedTexture()} APIs for at least a little while: We
     * haven't yet decided whether to deprecate them or not.
     *
     * @param callback Before loading, Meganekko may call
     *                 {@link AndroidResource.TextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} several times (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                 loaded()} on the GL thread;
     *                 <p/>
     *                 any errors will call
     *                 {@link AndroidResource.TextureCallback#failed(Throwable, AndroidResource)
     *                 failed()}, with no promises about threading.
     *                 <p/>
     *                 <p/>
     *                 This method uses a throttler to avoid overloading the system.
     *                 If the throttler has threads available, it will run this
     *                 request immediately. Otherwise, it will enqueue the request,
     *                 and call
     *                 {@link AndroidResource.TextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} at least once (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 <p/>
     *                 Use {@link #loadFutureTexture(AndroidResource)} to avoid
     *                 having to implement a callback.
     * @param resource Basically, a stream containing a texture file. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
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
    public void loadTexture(TextureCallback callback,
                            AndroidResource resource) {
        loadTexture(callback, resource, DEFAULT_PRIORITY);
    }

    /**
     * A simplified, low-level method that loads a texture asynchronously,
     * without making you specify
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)
     * loadBitmapTexture()} or
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)
     * loadCompressedTexture()}.
     * <p/>
     * This method can detect whether the resource file holds a compressed
     * texture (Meganekko currently supports ASTC, ETC2, and KTX formats:
     * applications can add new formats by implementing
     * {@link CompressedTextureLoader}): if the file is not a compressed
     * texture, it is loaded as a normal, bitmapped texture. This format
     * detection adds very little to the cost of loading even a compressed
     * texture, and it makes your life a lot easier: you can replace, say,
     * {@code res/raw/resource.png} with {@code res/raw/resource.etc2} without
     * having to change any code.
     * <p/>
     * <p/>
     * This method uses a default render quality: Use
     * {@link #loadTexture(AndroidResource.TextureCallback, AndroidResource, int, int)}
     * to specify an explicit quality.
     * <p/>
     * <p/>
     * We will continue to support the {@code loadBitmapTexture()} and
     * {@code loadCompressedTexture()} APIs for at least a little while: We
     * haven't yet decided whether to deprecate them or not.
     *
     * @param callback Before loading, Meganekko may call
     *                 {@link AndroidResource.TextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} several times (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                 loaded()} on the GL thread;
     *                 <p/>
     *                 any errors will call
     *                 {@link AndroidResource.TextureCallback#failed(Throwable, AndroidResource)
     *                 failed()}, with no promises about threading.
     *                 <p/>
     *                 <p/>
     *                 This method uses a throttler to avoid overloading the system.
     *                 If the throttler has threads available, it will run this
     *                 request immediately. Otherwise, it will enqueue the request,
     *                 and call
     *                 {@link AndroidResource.TextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} at least once (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 <p/>
     *                 Use {@link #loadFutureTexture(AndroidResource)} to avoid
     *                 having to implement a callback.
     * @param resource Basically, a stream containing a texture file. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param priority This request's priority. Please see the notes on asynchronous
     *                 priorities in the <a href="package-summary.html#async">package
     *                 description</a>. Also, please note priorities only apply to
     *                 uncompressed textures (standard Android bitmap files, which
     *                 can take hundreds of milliseconds to load): compressed
     *                 textures load so quickly that they are not run through the
     *                 request scheduler.
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
    public void loadTexture(TextureCallback callback,
                            AndroidResource resource, int priority) {
        loadTexture(callback, resource, priority, GVRCompressedTexture.BALANCED);
    }

    /**
     * A simplified, low-level method that loads a texture asynchronously,
     * without making you specify
     * {@link #loadBitmapTexture(AndroidResource.BitmapTextureCallback, AndroidResource)
     * loadBitmapTexture()} or
     * {@link #loadCompressedTexture(AndroidResource.CompressedTextureCallback, AndroidResource)
     * loadCompressedTexture()}.
     * <p/>
     * This method can detect whether the resource file holds a compressed
     * texture (Meganekko currently supports ASTC, ETC2, and KTX formats:
     * applications can add new formats by implementing
     * {@link CompressedTextureLoader}): if the file is not a compressed
     * texture, it is loaded as a normal, bitmapped texture. This format
     * detection adds very little to the cost of loading even a compressed
     * texture, and it makes your life a lot easier: you can replace, say,
     * {@code res/raw/resource.png} with {@code res/raw/resource.etc2} without
     * having to change any code.
     * <p/>
     * <p/>
     * We will continue to support the {@code loadBitmapTexture()} and
     * {@code loadCompressedTexture()} APIs for at least a little while: We
     * haven't yet decided whether to deprecate them or not.
     *
     * @param callback Before loading, Meganekko may call
     *                 {@link AndroidResource.TextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} several times (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 Successful loads will call
     *                 {@link AndroidResource.Callback#loaded(HybridObject, AndroidResource)
     *                 loaded()} on the GL thread;
     *                 <p/>
     *                 any errors will call
     *                 {@link AndroidResource.TextureCallback#failed(Throwable, AndroidResource)
     *                 failed()}, with no promises about threading.
     *                 <p/>
     *                 <p/>
     *                 This method uses a throttler to avoid overloading the system.
     *                 If the throttler has threads available, it will run this
     *                 request immediately. Otherwise, it will enqueue the request,
     *                 and call
     *                 {@link AndroidResource.TextureCallback#stillWanted(AndroidResource)
     *                 stillWanted()} at least once (on a background thread) to give
     *                 you a chance to abort a 'stale' load.
     *                 <p/>
     *                 <p/>
     *                 Use {@link #loadFutureTexture(AndroidResource)} to avoid
     *                 having to implement a callback.
     * @param resource Basically, a stream containing a texture file. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param priority This request's priority. Please see the notes on asynchronous
     *                 priorities in the <a href="package-summary.html#async">package
     *                 description</a>. Also, please note priorities only apply to
     *                 uncompressed textures (standard Android bitmap files, which
     *                 can take hundreds of milliseconds to load): compressed
     *                 textures load so quickly that they are not run through the
     *                 request scheduler.
     * @param quality  The compressed texture {@link GVRCompressedTexture#mQuality
     *                 quality} parameter: should be one of
     *                 {@link GVRCompressedTexture#SPEED},
     *                 {@link GVRCompressedTexture#BALANCED}, or
     *                 {@link GVRCompressedTexture#QUALITY}, but other values are
     *                 'clamped' to one of the recognized values. Please note that
     *                 this (currently) only applies to compressed textures; normal
     *                 {@linkplain BitmapTexture bitmapped textures} don't take a
     *                 quality parameter.
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
    public void loadTexture(TextureCallback callback,
                            AndroidResource resource, int priority, int quality) {
        AsynchronousResourceLoader.loadTexture(this, sTextureCache,
                callback, resource, priority, quality);
    }

    /**
     * Simple, high-level method to load a texture asynchronously, for use with
     * {@link Shaders#setMainTexture(Future)} and
     * {@link Shaders#setTexture(String, Future)}.
     * <p/>
     * This method uses a default priority and a default render quality: use
     * {@link #loadFutureTexture(AndroidResource, int)} to specify a priority or
     * {@link #loadFutureTexture(AndroidResource, int, int)} to specify a
     * priority and render quality.
     * <p/>
     * <p/>
     * This method is significantly easier to use than
     * {@link #loadTexture(AndroidResource.TextureCallback, AndroidResource)} :
     * you don't have to implement a callback; you don't have to pay attention
     * to the low-level details of
     * {@linkplain SceneObject#attachRenderData(RenderData) attaching} a
     * {@link RenderData} to your scene object. What's more, you don't even lose
     * any functionality: {@link Future#cancel(boolean)} lets you cancel a
     * 'stale' request, just like
     * {@link AndroidResource.CancelableCallback#stillWanted(AndroidResource)
     * stillWanted()} does. The flip side, of course, is that it <em>is</em> a
     * bit more expensive: methods like {@link Material#setMainTexture(Future)}
     * use an extra thread from the thread pool to wait for the blocking
     * {@link Future#get()} call. For modest numbers of loads, this overhead is
     * acceptable - but thread creation is not free, and if your
     * {@link MeganekkoActivity#oneTimeInit()} method fires of dozens of
     * future loads, you may well see an impact.
     *
     * @param resource Basically, a stream containing a texture file. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @return A {@link Future} that you can pass to methods like
     * {@link Shaders#setMainTexture(Future)}
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
    public Future<Texture> loadFutureTexture(AndroidResource resource) {
        return loadFutureTexture(resource, DEFAULT_PRIORITY);
    }

    /**
     * Simple, high-level method to load a texture asynchronously, for use with
     * {@link Shaders#setMainTexture(Future)} and
     * {@link Shaders#setTexture(String, Future)}.
     * <p/>
     * This method uses a default render quality:
     * {@link #loadFutureTexture(AndroidResource, int, int)} to specify render
     * quality.
     * <p/>
     * <p/>
     * This method is significantly easier to use than
     * {@link #loadTexture(AndroidResource.TextureCallback, AndroidResource, int)
     * : you don't have to implement a callback; you don't have to pay attention
     * to the low-level details of
     * {@linkplain SceneObject#attachRenderData(RenderData) attaching} a
     * {@link RenderData} to your scene object. What's more, you don't even lose
     * any functionality: {@link Future#cancel(boolean)} lets you cancel a
     * 'stale' request, just like
     * {@link AndroidResource.CancelableCallback#stillWanted(AndroidResource)
     * stillWanted()} does. The flip side, of course, is that it <em>is</em> a
     * bit more expensive: methods like {@link Material#setMainTexture(Future)}
     * use an extra thread from the thread pool to wait for the blocking
     * {@link Future#get()} call. For modest numbers of loads, this overhead is
     * acceptable - but thread creation is not free, and if your
     * {@link MeganekkoActivity#oneTimeInit()} method fires of dozens of
     * future loads, you may well see an impact.
     *
     * @param resource Basically, a stream containing a texture file. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param priority This request's priority. Please see the notes on asynchronous
     *                 priorities in the <a href="package-summary.html#async">package
     *                 description</a>. Also, please note priorities only apply to
     *                 uncompressed textures (standard Android bitmap files, which
     *                 can take hundreds of milliseconds to load): compressed
     *                 textures load so quickly that they are not run through the
     *                 request scheduler.
     * @return A {@link Future} that you can pass to methods like
     * {@link Shaders#setMainTexture(Future)}
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
    public Future<Texture> loadFutureTexture(AndroidResource resource,
                                             int priority) {
        return loadFutureTexture(resource, priority,
                GVRCompressedTexture.BALANCED);
    }

    /**
     * Simple, high-level method to load a texture asynchronously, for use with
     * {@link Shaders#setMainTexture(Future)} and
     * {@link Shaders#setTexture(String, Future)}.
     * <p/>
     * <p/>
     * <p/>
     * This method is significantly easier to use than
     * {@link #loadTexture(AndroidResource.TextureCallback, AndroidResource, int, int)
     * : you don't have to implement a callback; you don't have to pay attention
     * to the low-level details of
     * {@linkplain SceneObject#attachRenderData(RenderData) attaching} a
     * {@link RenderData} to your scene object. What's more, you don't even lose
     * any functionality: {@link Future#cancel(boolean)} lets you cancel a
     * 'stale' request, just like
     * {@link AndroidResource.CancelableCallback#stillWanted(AndroidResource)
     * stillWanted()} does. The flip side, of course, is that it <em>is</em> a
     * bit more expensive: methods like {@link Material#setMainTexture(Future)}
     * use an extra thread from the thread pool to wait for the blocking
     * {@link Future#get()} call. For modest numbers of loads, this overhead is
     * acceptable - but thread creation is not free, and if your
     * {@link MeganekkoActivity#oneTimeInit()} method fires of dozens of
     * future loads, you may well see an impact.
     *
     * @param resource Basically, a stream containing a texture file. The
     *                 {@link AndroidResource} class has six constructors to handle a
     *                 wide variety of Android resource types. Taking a
     *                 {@code AndroidResource} here eliminates six overloads.
     * @param priority This request's priority. Please see the notes on asynchronous
     *                 priorities in the <a href="package-summary.html#async">package
     *                 description</a>. Also, please note priorities only apply to
     *                 uncompressed textures (standard Android bitmap files, which
     *                 can take hundreds of milliseconds to load): compressed
     *                 textures load so quickly that they are not run through the
     *                 request scheduler.
     * @param quality  The compressed texture {@link GVRCompressedTexture#mQuality
     *                 quality} parameter: should be one of
     *                 {@link GVRCompressedTexture#SPEED},
     *                 {@link GVRCompressedTexture#BALANCED}, or
     *                 {@link GVRCompressedTexture#QUALITY}, but other values are
     *                 'clamped' to one of the recognized values. Please note that
     *                 this (currently) only applies to compressed textures; normal
     *                 {@linkplain BitmapTexture bitmapped textures} don't take a
     *                 quality parameter.
     * @return A {@link Future} that you can pass to methods like
     * {@link Shaders#setMainTexture(Future)}
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
    public Future<Texture> loadFutureTexture(AndroidResource resource,
                                             int priority, int quality) {
        return AsynchronousResourceLoader.loadFutureTexture(this,
                sTextureCache, resource, priority, quality);
    }

    /**
     * Simple, high-level method to load a cube map texture asynchronously, for
     * use with {@link Shaders#setMainTexture(Future)} and
     * {@link Shaders#setTexture(String, Future)}.
     *
     * @param resource A steam containing a zip file which contains six bitmaps. The
     *                 six bitmaps correspond to +x, -x, +y, -y, +z, and -z faces of
     *                 the cube map texture respectively. The default names of the
     *                 six images are "posx.png", "negx.png", "posy.png", "negx.png",
     *                 "posz.png", and "negz.png", which can be changed by calling
     *                 {@link CubemapTexture#setFaceNames(String[])}.
     * @return A {@link Future} that you can pass to methods like
     * {@link Shaders#setMainTexture(Future)}
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
    public Future<Texture> loadFutureCubemapTexture(
            AndroidResource resource) {
        return AsynchronousResourceLoader.loadFutureCubemapTexture(this,
                sTextureCache, resource, DEFAULT_PRIORITY,
                CubemapTexture.faceIndexMap);
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
     * The {@linkplain MaterialShaderManager object shader manager} singleton.
     * <p/>
     * Use the shader manager to define custom GL object shaders, which are used
     * to render a scene object's surface.
     *
     * @return The {@linkplain MaterialShaderManager shader manager} singleton.
     */
    @Deprecated
    public MaterialShaderManager getMaterialShaderManager() {
        return getActivity().getMaterialShaderManager();
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
