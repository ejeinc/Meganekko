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
import java.util.List;

import com.eje_c.meganekko.event.FrameListener;
import com.eje_c.meganekko.event.KeyDoubleTapEvent;
import com.eje_c.meganekko.event.KeyDoubleTapEventListener;
import com.eje_c.meganekko.event.KeyDownEvent;
import com.eje_c.meganekko.event.KeyDownEventListener;
import com.eje_c.meganekko.event.KeyLongPressEvent;
import com.eje_c.meganekko.event.KeyLongPressEventListener;
import com.eje_c.meganekko.event.KeyMaxEvent;
import com.eje_c.meganekko.event.KeyMaxEventListener;
import com.eje_c.meganekko.event.KeyShortPressEvent;
import com.eje_c.meganekko.event.KeyShortPressEventListener;
import com.eje_c.meganekko.event.KeyUpEvent;
import com.eje_c.meganekko.event.KeyUpEventListener;
import com.eje_c.meganekko.event.SwipeBackEvent;
import com.eje_c.meganekko.event.SwipeBackEventListener;
import com.eje_c.meganekko.event.SwipeDownEvent;
import com.eje_c.meganekko.event.SwipeDownEventListener;
import com.eje_c.meganekko.event.SwipeForwardEvent;
import com.eje_c.meganekko.event.SwipeForwardEventListener;
import com.eje_c.meganekko.event.SwipeUpEvent;
import com.eje_c.meganekko.event.SwipeUpEventListener;
import com.eje_c.meganekko.event.TouchDoubleEvent;
import com.eje_c.meganekko.event.TouchDoubleEventListener;
import com.eje_c.meganekko.event.TouchSingleEvent;
import com.eje_c.meganekko.event.TouchSingleEventListener;
import com.eje_c.meganekko.utility.Log;

import de.greenrobot.event.EventBus;

/** The scene graph */
public class Scene extends SceneObject implements FrameListener,
        KeyDoubleTapEventListener, KeyDownEventListener, KeyLongPressEventListener, KeyMaxEventListener,
        KeyShortPressEventListener, KeyUpEventListener,
        SwipeBackEventListener, SwipeDownEventListener, SwipeForwardEventListener, SwipeUpEventListener,
        TouchDoubleEventListener, TouchSingleEventListener {
    @SuppressWarnings("unused")
    private static final String TAG = Log.tag(Scene.class);

    private final List<OnFrameListener> mOnFrameListeners = new ArrayList<>();
    private Camera mMainCamera;
    private EventBus mEventBus = new EventBus();

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

    /*
     * Event register methods.
     */

    public void onSwipeUp(SwipeUpEventListener listener) {
        mEventBus.register(listener);
    }

    public void onSwipeDown(SwipeDownEventListener listener) {
        mEventBus.register(listener);
    }

    public void onSwipeForward(SwipeForwardEventListener listener) {
        mEventBus.register(listener);
    }

    public void onSwipeBack(SwipeBackEventListener listener) {
        mEventBus.register(listener);
    }

    public void onTouchSingle(TouchSingleEventListener listener) {
        mEventBus.register(listener);
    }

    public void onTouchDouble(TouchDoubleEventListener listener) {
        mEventBus.register(listener);
    }

    public void onKeyShortPress(KeyShortPressEventListener listener) {
        mEventBus.register(listener);
    }

    public void onKeyDoubleTap(KeyDoubleTapEventListener listener) {
        mEventBus.register(listener);
    }

    public void onKeyLongPress(KeyLongPressEventListener listener) {
        mEventBus.register(listener);
    }

    public void onKeyDown(KeyDownEventListener listener) {
        mEventBus.register(listener);
    }

    public void onKeyUp(KeyUpEventListener listener) {
        mEventBus.register(listener);
    }

    public void onKeyMax(KeyMaxEventListener listener) {
        mEventBus.register(listener);
    }

    public void offSwipeUp(SwipeUpEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offSwipeDown(SwipeDownEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offSwipeForward(SwipeForwardEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offSwipeBack(SwipeBackEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offTouchSingle(TouchSingleEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offTouchDouble(TouchDoubleEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offKeyShortPress(KeyShortPressEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offKeyDoubleTap(KeyDoubleTapEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offKeyLongPress(KeyLongPressEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offKeyDown(KeyDownEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offKeyUp(KeyUpEventListener listener) {
        mEventBus.unregister(listener);
    }

    public void offKeyMax(KeyMaxEventListener listener) {
        mEventBus.unregister(listener);
    }

    @Override
    public void onEvent(TouchSingleEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(TouchDoubleEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(SwipeUpEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(SwipeForwardEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(SwipeDownEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(SwipeBackEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(KeyUpEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(KeyShortPressEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(KeyMaxEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(KeyLongPressEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(KeyDownEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(KeyDoubleTapEvent event) {
        mEventBus.post(event);
    }

    @Override
    public void onEvent(VrFrame vrFrame) {
        mEventBus.post(vrFrame);
    }
}

class NativeScene {
    static native long ctor();

    public static native void setFrustumCulling(long scene, boolean flag);

    public static native void setOcclusionQuery(long scene, boolean flag);

    static native void setMainCamera(long scene, long camera);
}
