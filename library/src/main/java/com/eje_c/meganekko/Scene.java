/* 
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
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

import de.greenrobot.event.EventBus;

/** The scene graph */
public class Scene extends SceneObject implements FrameListener,
        KeyDoubleTapEventListener, KeyDownEventListener, KeyLongPressEventListener, KeyMaxEventListener,
        KeyShortPressEventListener, KeyUpEventListener,
        SwipeBackEventListener, SwipeDownEventListener, SwipeForwardEventListener, SwipeUpEventListener,
        TouchDoubleEventListener, TouchSingleEventListener {

    private Camera mMainCamera;
    private EventBus mEventBus = EventBus.builder().logNoSubscriberMessages(false).build();

    /**
     * Constructs a scene with a camera rig holding left & right cameras in it.
     *
     * @param vrContext
     *            {@link VrContext} the app is using.
     */
    public Scene(VrContext vrContext) {
        super(vrContext);
        Camera camera = new Camera(vrContext);
        addChildObject(camera);
        setMainCamera(camera);
    }

    @Override
    protected native long initNativeInstance();

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

    @Override
    public void addChildObject(SceneObject child) {
        super.addChildObject(child);
        mEventBus.register(child);
    }

    @Override
    public void removeChildObject(SceneObject child) {
        super.removeChildObject(child);
        mEventBus.unregister(child);
    }

    /*
     * Event register methods.
     */

    /**
     * Register a {@link FrameListener}.
     *
     * @param listener
     */
    public final void onFrame(FrameListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link SwipeUpEventListener}.
     *
     * @param listener
     */
    public final void onSwipeUp(SwipeUpEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link SwipeDownEventListener}.
     *
     * @param listener
     */
    public final void onSwipeDown(SwipeDownEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link SwipeForwardEventListener}.
     *
     * @param listener
     */
    public final void onSwipeForward(SwipeForwardEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link SwipeBackEventListener}.
     *
     * @param listener
     */
    public final void onSwipeBack(SwipeBackEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link TouchSingleEventListener}.
     *
     * @param listener
     */
    public final void onTouchSingle(TouchSingleEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link TouchDoubleEventListener}.
     *
     * @param listener
     */
    public final void onTouchDouble(TouchDoubleEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link KeyShortPressEventListener}.
     *
     * @param listener
     */
    public final void onKeyShortPress(KeyShortPressEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link KeyDoubleTapEventListener}.
     *
     * @param listener
     */
    public final void onKeyDoubleTap(KeyDoubleTapEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link KeyLongPressEventListener}.
     *
     * @param listener
     */
    public final void onKeyLongPress(KeyLongPressEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link KeyDownEventListener}.
     *
     * @param listener
     */
    public final void onKeyDown(KeyDownEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link KeyUpEventListener}.
     *
     * @param listener
     */
    public final void onKeyUp(KeyUpEventListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Register a {@link KeyMaxEventListener}.
     *
     * @param listener
     */
    public final void onKeyMax(KeyMaxEventListener listener) {
        mEventBus.register(listener);
    }

    /*
     * Event unregister methods.
     */

    /**
     * Unregister a {@link FrameListener}.
     *
     * @param listener
     */
    public final void offFrame(FrameListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link SwipeUpEventListener}.
     *
     * @param listener
     */
    public final void offSwipeUp(SwipeUpEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link SwipeDownEventListener}.
     *
     * @param listener
     */
    public final void offSwipeDown(SwipeDownEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link SwipeForwardEventListener}.
     *
     * @param listener
     */
    public final void offSwipeForward(SwipeForwardEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link SwipeBackEventListener}.
     *
     * @param listener
     */
    public final void offSwipeBack(SwipeBackEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link TouchSingleEventListener}.
     *
     * @param listener
     */
    public final void offTouchSingle(TouchSingleEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link TouchDoubleEventListener}.
     *
     * @param listener
     */
    public final void offTouchDouble(TouchDoubleEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link KeyShortPressEventListener}.
     *
     * @param listener
     */
    public final void offKeyShortPress(KeyShortPressEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link KeyDoubleTapEventListener}.
     *
     * @param listener
     */
    public final void offKeyDoubleTap(KeyDoubleTapEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link KeyLongPressEventListener}.
     *
     * @param listener
     */
    public final void offKeyLongPress(KeyLongPressEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link KeyDownEventListener}.
     *
     * @param listener
     */
    public final void offKeyDown(KeyDownEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link KeyUpEventListener}.
     *
     * @param listener
     */
    public final void offKeyUp(KeyUpEventListener listener) {
        mEventBus.unregister(listener);
    }

    /**
     * Unregister a {@link KeyMaxEventListener}.
     *
     * @param listener
     */
    public final void offKeyMax(KeyMaxEventListener listener) {
        mEventBus.unregister(listener);
    }

    /*
     * These onEvents are notified from MeganekkoActivity. These redirect passed
     * event to scene local event bus.
     */

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
