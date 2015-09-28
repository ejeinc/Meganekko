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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eje_c.meganekko.utility.Log;

/** The scene graph */
public class Scene extends HybridObject {
    @SuppressWarnings("unused")
    private static final String TAG = Log.tag(Scene.class);

    private final List<SceneObject> mSceneObjects = new ArrayList<SceneObject>();
    private final List<OnFrameListener> mOnFrameListeners = new ArrayList<>();
    private Camera mMainCamera;
    
    public interface OnFrameListener {
        void onFrame(VrFrame vrFrame);
    }

    /**
     * Constructs a scene with a camera rig holding left & right cameras in it.
     * 
     * @param vrContext
     *            {@link VrContext} the app is using.
     */
    public Scene(VrContext vrContext) {
        super(vrContext, NativeScene.ctor());

        Camera camera = new Camera(vrContext);
        addSceneObject(camera);
        setMainCamera(camera);
    }

    private Scene(VrContext vrContext, long ptr) {
        super(vrContext, ptr);
    }

    /**
     * Called from GL thread in every frame.
     * 
     * @param vrFrame
     */
    protected void onFrame(VrFrame vrFrame) {
        synchronized (this) {
            final List<OnFrameListener> list = mOnFrameListeners;
            final int size = mOnFrameListeners.size();
            for (int i = 0; i < size; ++i) {
                list.get(i).onFrame(vrFrame);
            }
        }
    }

    /**
     * Register a callback to be invoked when frame update.
     * 
     * @param onFrameListener
     */
    public void addOnFrameListener(OnFrameListener onFrameListener) {
        synchronized (this) {
            if (onFrameListener == null) {
                throw new IllegalArgumentException("onFrameListener must not be null");
            }
            mOnFrameListeners.add(onFrameListener);
        }
    }

    /**
     * Remove a callback for frame update listener.
     * 
     * @param onFrameListener
     */
    public void removeOnFrameListener(OnFrameListener onFrameListener) {
        synchronized (this) {
            if (onFrameListener == null) {
                throw new IllegalArgumentException("onFrameListener must not be null");
            }
            mOnFrameListeners.remove(onFrameListener);
        }
    }

    /**
     * Add an {@linkplain SceneObject scene object}
     * 
     * @param sceneObject
     *            The {@linkplain SceneObject scene object} to add.
     */
    public void addSceneObject(SceneObject sceneObject) {
        mSceneObjects.add(sceneObject);
        NativeScene.addSceneObject(getNative(), sceneObject.getNative());
    }

    /**
     * Remove a {@linkplain SceneObject scene object}
     * 
     * @param sceneObject
     *            The {@linkplain SceneObject scene object} to remove.
     */
    public void removeSceneObject(SceneObject sceneObject) {
        mSceneObjects.remove(sceneObject);
        NativeScene.removeSceneObject(getNative(), sceneObject.getNative());
    }

    /**
     * The top-level scene objects.
     * 
     * @return A read-only list containing all the 'root' scene objects (those
     *         that were added directly to the scene).
     */
    public List<SceneObject> getSceneObjects() {
        return Collections.unmodifiableList(mSceneObjects);
    }

    /**
     * @return The {@link Camera camera rig} used for rendering the scene on the
     *         screen.
     */
    public Camera getMainCamera() {
        return mMainCamera;
    }

    /**
     * Set the {@link Camera camera} used for rendering the scene on the screen.
     * 
     * @param camera
     *            The {@link Camera camera} to render with.
     */
    public void setMainCamera(Camera camera) {
        mMainCamera = camera;
        NativeScene.setMainCamera(getNative(), camera.getNative());
    }

    /**
     * @return The flattened hierarchy of {@link SceneObject objects} as an
     *         array.
     */
    public SceneObject[] getWholeSceneObjects() {
        List<SceneObject> list = new ArrayList<SceneObject>(mSceneObjects);
        for (SceneObject child : mSceneObjects) {
            addChildren(list, child);
        }
        return list.toArray(new SceneObject[list.size()]);
    }

    private void addChildren(List<SceneObject> list,
            SceneObject sceneObject) {
        for (SceneObject child : sceneObject.rawGetChildren()) {
            list.add(child);
            addChildren(list, child);
        }
    }

    /**
     * Sets the frustum culling for the {@link Scene}.
     */
    public void setFrustumCulling(boolean flag) {
        NativeScene.setFrustumCulling(getNative(), flag);
    }

    /**
     * Sets the occlusion query for the {@link Scene}.
     */
    public void setOcclusionQuery(boolean flag) {
        NativeScene.setOcclusionQuery(getNative(), flag);
    }

    public SceneObject findObjectByName(String name) {
        for (SceneObject object : getWholeSceneObjects()) {
            if (name.equals(object.getName())) {
                return object;
            }
        }

        return null;
    }

    public List<SceneObject> findObjectsByName(String name) {

        List<SceneObject> objects = new ArrayList<>();

        for (SceneObject object : getWholeSceneObjects()) {
            if (name.equals(object.getName())) {
                objects.add(object);
            }
        }

        return objects;
    }
}

class NativeScene {
    static native long ctor();

    static native void addSceneObject(long scene, long sceneObject);

    static native void removeSceneObject(long scene, long sceneObject);

    public static native void setFrustumCulling(long scene, boolean flag);

    public static native void setOcclusionQuery(long scene, boolean flag);

    static native void setMainCamera(long scene, long camera);
}
