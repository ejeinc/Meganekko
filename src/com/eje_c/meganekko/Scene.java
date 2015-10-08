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
import java.util.EventObject;
import java.util.List;

import com.eje_c.meganekko.event.EventEmitter;
import com.eje_c.meganekko.event.KeyEventListener;
import com.eje_c.meganekko.event.SwipeEventListener;
import com.eje_c.meganekko.event.TouchEventListener;
import com.eje_c.meganekko.utility.Log;

/** The scene graph */
public class Scene extends SceneObject {
    @SuppressWarnings("unused")
    private static final String TAG = Log.tag(Scene.class);

    private final List<SceneObject> mSceneObjects = new ArrayList<SceneObject>();
    private final List<OnFrameListener> mOnFrameListeners = new ArrayList<>();
    private Camera mMainCamera;
    private EventEmitter eventEmitter = new EventEmitter();

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
    @Deprecated
    public void addSceneObject(SceneObject sceneObject) {
        super.addChildObject(sceneObject);
    }

    /**
     * Remove a {@linkplain SceneObject scene object}
     * 
     * @param sceneObject
     *            The {@linkplain SceneObject scene object} to remove.
     */
    @Deprecated
    public void removeSceneObject(SceneObject sceneObject) {
        super.removeChildObject(sceneObject);
    }

    /**
     * The top-level scene objects.
     * 
     * @return A read-only list containing all the 'root' scene objects (those
     *         that were added directly to the scene).
     */
    @Deprecated
    public List<SceneObject> getSceneObjects() {
        return super.getChildren();
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
        for (SceneObject object : mSceneObjects) {
            SceneObject result = object.findObjectByName(name);
            if (result != null) {
                return result;
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

    /*
     * User input events.
     */

    /**
     * Called from VR thread when swipe up gesture is recognized.
     */
    void onSwipeUp() {
        eventEmitter.emit("swipeUp", new EventObject(this));
    }

    /**
     * Called from VR thread when swipe down gesture is recognized.
     */
    void onSwipeDown() {
        eventEmitter.emit("swipeDown", new EventObject(this));
    }

    /**
     * Called from VR thread when swipe forward gesture is recognized.
     */
    void onSwipeForward() {
        eventEmitter.emit("swipeForward", new EventObject(this));
    }

    /**
     * Called from VR thread when swipe back gesture is recognized.
     */
    void onSwipeBack() {
        eventEmitter.emit("swipeBack", new EventObject(this));
    }

    /**
     * Called from VR thread when touch pad single tap is recognized.
     */
    void onTouchSingle() {
        eventEmitter.emit("touchSingle", new EventObject(this));
    }

    /**
     * Called from VR thread when touch pad double tap is recognized.
     */
    void onTouchDouble() {
        eventEmitter.emit("touchDouble", new EventObject(this));
    }

    boolean onKeyShortPress(int keyCode, int repeatCount) {
        com.eje_c.meganekko.event.KeyEvent keyEvent = new com.eje_c.meganekko.event.KeyEvent(this, keyCode,
                repeatCount);
        eventEmitter.emit("keyShortPress", keyEvent);
        return keyEvent.isPreventDefaultCalled();
    }

    boolean onKeyDoubleTap(int keyCode, int repeatCount) {
        com.eje_c.meganekko.event.KeyEvent keyEvent = new com.eje_c.meganekko.event.KeyEvent(this, keyCode,
                repeatCount);
        eventEmitter.emit("keyDoubleTap", keyEvent);
        return keyEvent.isPreventDefaultCalled();
    }

    boolean onKeyLongPress(int keyCode, int repeatCount) {
        com.eje_c.meganekko.event.KeyEvent keyEvent = new com.eje_c.meganekko.event.KeyEvent(this, keyCode,
                repeatCount);
        eventEmitter.emit("keyLongPress", keyEvent);
        return keyEvent.isPreventDefaultCalled();
    }

    boolean onKeyDown(int keyCode, int repeatCount) {
        com.eje_c.meganekko.event.KeyEvent keyEvent = new com.eje_c.meganekko.event.KeyEvent(this, keyCode,
                repeatCount);
        eventEmitter.emit("keyDown", keyEvent);
        return keyEvent.isPreventDefaultCalled();
    }

    boolean onKeyUp(int keyCode, int repeatCount) {
        com.eje_c.meganekko.event.KeyEvent keyEvent = new com.eje_c.meganekko.event.KeyEvent(this, keyCode,
                repeatCount);
        eventEmitter.emit("keyUp", keyEvent);
        return keyEvent.isPreventDefaultCalled();
    }

    boolean onKeyMax(int keyCode, int repeatCount) {
        com.eje_c.meganekko.event.KeyEvent keyEvent = new com.eje_c.meganekko.event.KeyEvent(this, keyCode,
                repeatCount);
        eventEmitter.emit("keyMax", keyEvent);
        return keyEvent.isPreventDefaultCalled();
    }

    /*
     * Event register methods.
     */

    public void onSwipeUp(SwipeEventListener listener) {
        eventEmitter.on("swipeUp", listener);
    }

    public void onSwipeDown(SwipeEventListener listener) {
        eventEmitter.on("swipeDown", listener);
    }

    public void onSwipeForward(SwipeEventListener listener) {
        eventEmitter.on("swipeForward", listener);
    }

    public void onSwipeBack(SwipeEventListener listener) {
        eventEmitter.on("swipeBack", listener);
    }

    public void onTouchSingle(TouchEventListener listener) {
        eventEmitter.on("touchSingle", listener);
    }

    public void onTouchDouble(TouchEventListener listener) {
        eventEmitter.on("touchDouble", listener);
    }

    public void onKeyShortPress(KeyEventListener listener) {
        eventEmitter.on("keyShortPress", listener);
    }

    public void onKeyDoubleTap(KeyEventListener listener) {
        eventEmitter.on("keyDoubleTap", listener);
    }

    public void onKeyLongPress(KeyEventListener listener) {
        eventEmitter.on("keyLongPress", listener);
    }

    public void onKeyDown(KeyEventListener listener) {
        eventEmitter.on("keyDown", listener);
    }

    public void onKeyUp(KeyEventListener listener) {
        eventEmitter.on("keyUp", listener);
    }

    public void onKeyMax(KeyEventListener listener) {
        eventEmitter.on("keyMax", listener);
    }

    public void offSwipeUp(SwipeEventListener listener) {
        eventEmitter.off("swipeUp", listener);
    }

    public void offSwipeDown(SwipeEventListener listener) {
        eventEmitter.off("swipeDown", listener);
    }

    public void offSwipeForward(SwipeEventListener listener) {
        eventEmitter.off("swipeForward", listener);
    }

    public void offSwipeBack(SwipeEventListener listener) {
        eventEmitter.off("swipeBack", listener);
    }

    public void offTouchSingle(TouchEventListener listener) {
        eventEmitter.off("touchSingle", listener);
    }

    public void offTouchDouble(TouchEventListener listener) {
        eventEmitter.off("touchDouble", listener);
    }

    public void offKeyShortPress(KeyEventListener listener) {
        eventEmitter.off("keyShortPress", listener);
    }

    public void offKeyDoubleTap(KeyEventListener listener) {
        eventEmitter.off("keyDoubleTap", listener);
    }

    public void offKeyLongPress(KeyEventListener listener) {
        eventEmitter.off("keyLongPress", listener);
    }

    public void offKeyDown(KeyEventListener listener) {
        eventEmitter.off("keyDown", listener);
    }

    public void offKeyUp(KeyEventListener listener) {
        eventEmitter.off("keyUp", listener);
    }

    public void offKeyMax(KeyEventListener listener) {
        eventEmitter.off("keyMax", listener);
    }
}

class NativeScene {
    static native long ctor();

    public static native void setFrustumCulling(long scene, boolean flag);

    public static native void setOcclusionQuery(long scene, boolean flag);

    static native void setMainCamera(long scene, long camera);
}
