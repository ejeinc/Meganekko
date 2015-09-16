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

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

import com.eje_c.meganekko.utility.DockEventReceiver;
import com.eje_c.meganekko.utility.Log;
import com.oculus.vrappframework.VrActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * The typical Meganekko application will have a single Android {@link Activity}
 * , which <em>must</em> descend from {@link MeganekkoActivity}, not directly
 * from {@code Activity}.
 *
 * {@code MeganekkoActivity} creates and manages the internal classes which use
 * sensor data to manage a viewpoint, and thus present an appropriate
 * stereoscopic view of your scene graph.
 */
public class MeganekkoActivity extends VrActivity {

    private static final String TAG = Log.tag(MeganekkoActivity.class);

    private final Queue<Runnable> mRunnables = new LinkedBlockingQueue<Runnable>();
    private final Set<FrameListener> mFrameListeners = new CopyOnWriteArraySet<>();
    private InternalSensorManager mInternalSensorManager;
    private GLContext mGVRContext = null;
    private boolean mDocked;
    private VrFrame vrFrame;

    static {
        System.loadLibrary("meganekko");
    }

    private static native void nativeSetContext(long appPtr, long contextPtr);

    private static native long nativeSetAppInterface(VrActivity act,
            String fromPackageName, String commandString, String uriString);

    private static native void nativeHideGazeCursor(long appPtr);

    private static native void nativeShowGazeCursor(long appPtr);

    private static native void nativeSetMinimumVsyncs(long appPtr, int vsyncs);

    private static native int nativeGetMinimumVsyncs(long appPtr);

    private static native void nativeOnDock(long appPtr);

    private static native void nativeOnUndock(long appPtr);

    private static native void nativeSetIntervalSensorValues(long appPtr, float x, float y, float z, float w);

    public static native void setDebugOptionEnable(boolean enable);

    private static native void recenterPose(long appPtr);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         * Removes the title bar and the status bar.
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String commandString = VrActivity.getCommandStringFromIntent(intent);
        String fromPackageNameString = VrActivity
                .getPackageStringFromIntent(intent);
        String uriString = VrActivity.getUriStringFromIntent(intent);

        setAppPtr(nativeSetAppInterface(this, fromPackageNameString,
                commandString, uriString));

        mDockEventReceiver = new DockEventReceiver(this, mRunOnDock, mRunOnUndock);
        mInternalSensorManager = new InternalSensorManager(this, getAppPtr());

        mGVRContext = new GLContext(this);
        nativeSetContext(getAppPtr(), mGVRContext.getNativePtr());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDockEventReceiver.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDockEventReceiver.start();
    }

    /**
     * Called from native AppInterface::oneTimeInit().
     */
    private void oneTimeInit() {
        mGVRContext.onSurfaceCreated();

        if (!mDocked) {
            mInternalSensorManager.start();
        }

        oneTimeInit(mGVRContext);
    }

    /**
     * Called when native oneTimeInit is called.
     * 
     * @param context
     */
    protected void oneTimeInit(GLContext context) {
    }

    /**
     * Called from native AppInterface::frame().
     */
    private void frame(long vrFramePtr) {

        // Setup VrFrame
        if (vrFrame == null) {
            vrFrame = new VrFrame(vrFramePtr);
        } else {
            vrFrame.setNativePtr(vrFramePtr);
        }

        // runOnGlThread handling (1 event per frame)
        if (!mRunnables.isEmpty()) {
            Runnable event = mRunnables.poll();
            event.run();
        }

        // DrawFrameListener handling
        for (FrameListener frameListener : mFrameListeners) {
            frameListener.frame();
        }

        mGVRContext.getMainScene().onFrame(vrFrame);

        frame();
    }

    /**
     * Called from GL thread at every frame.
     */
    protected void frame() {
    }

    /**
     * Called from native AppInterface::oneTimeShutDown().
     */
    private void oneTimeShutDown() {

        if (!mDocked) {
            mInternalSensorManager.stop();
        }

        oneTimeShutDown(mGVRContext);
    }

    /**
     * Called when native oneTimeShutDown is called.
     * 
     * @param context
     */
    protected void oneTimeShutDown(GLContext context) {
    }

    public void hideGazeCursor() {
        nativeHideGazeCursor(getAppPtr());
    }

    public void showGazeCursor() {
        nativeShowGazeCursor(getAppPtr());
    }

    public void setMinimumVsyncs(int vsyncs) {
        nativeSetMinimumVsyncs(getAppPtr(), vsyncs);
    }

    public int getMinimumVsyncs() {
        return nativeGetMinimumVsyncs(getAppPtr());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handled = super.dispatchTouchEvent(event);// VrActivity's

        /*
         * Situation: while the super class VrActivity is implementing
         * dispatchTouchEvent() without calling its own super
         * dispatchTouchEvent(), we still need to call the
         * VRTouchPadGestureDetector onTouchEvent. Call it here, similar way
         * like in place of viewGroup.onInterceptTouchEvent()
         */
        onTouchEvent(event);

        return handled;
    }

    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        //        Log.d(TAG, "onKeyShortPress key:%d repeat:%d", keyCode, repeatCount);
        return false;
    }

    public boolean onKeyDoubleTap(int keyCode, int repeatCount) {
        //        Log.d(TAG, "onKeyDoubleTap %d repeat:%d", keyCode, repeatCount);
        return false;
    }

    public boolean onKeyLongPress(int keyCode, int repeatCount) {
        //        Log.d(TAG, "onKeyLongPress %d repeat:%d", keyCode, repeatCount);
        return onKeyLongPress(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    }

    public boolean onKeyDown(int keyCode, int repeatCount) {
        //        Log.d(TAG, "onKeyDown %d repeat:%d", keyCode, repeatCount);
        return onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    }

    public boolean onKeyUp(int keyCode, int repeatCount) {
        //        Log.d(TAG, "onKeyUp %d repeat:%d", keyCode, repeatCount);
        return onKeyUp(keyCode, new KeyEvent(KeyEvent.ACTION_UP, keyCode));
    }

    public boolean onKeyMax(int keyCode, int repeatCount) {
        //        Log.d(TAG, "onKeyMax %d repeat:%d", keyCode, repeatCount);
        return false;
    }

    @Deprecated
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Deprecated
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Deprecated
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called from VR thread when swipe up gesture is recognized.
     */
    public void onSwipeUp() {
        //        Log.d(TAG, "onSwipeUp called");
    }

    /**
     * Called from VR thread when swipe down gesture is recognized.
     */
    public void onSwipeDown() {
        //        Log.d(TAG, "onSwipeDown called");
    }

    /**
     * Called from VR thread when swipe forward gesture is recognized.
     */
    public void onSwipeForward() {
        //        Log.d(TAG, "onSwipeForward called");
    }

    /**
     * Called from VR thread when swipe back gesture is recognized.
     */
    public void onSwipeBack() {
        //        Log.d(TAG, "onSwipeBack called");
    }

    /**
     * Called from VR thread when touch pad single tap is recognized.
     */
    public void onTouchSingle() {
        //        Log.d(TAG, "onTouchSingle called");
    }

    /**
     * Called from VR thread when touch pad double tap is recognized.
     */
    public void onTouchDouble() {
        //        Log.d(TAG, "onTouchDouble called");
    }

    private final Runnable mRunOnDock = new Runnable() {
        @Override
        public void run() {
            mDocked = true;
            nativeOnDock(getAppPtr());
            mInternalSensorManager.stop();
        }
    };

    private final Runnable mRunOnUndock = new Runnable() {
        @Override
        public void run() {
            mDocked = false;
            nativeOnUndock(getAppPtr());
        }
    };

    private DockEventReceiver mDockEventReceiver;

    /**
     * Enqueues a callback to be run in the GL thread.
     * 
     * This is how you take data generated on a background thread (or the main
     * (GUI) thread) and pass it to the coprocessor, using calls that must be
     * made from the GL thread (aka the "GL context"). The callback queue is
     * processed before any registered
     * {@linkplain #registerFrameListener(FrameListener) frame listeners}.
     * 
     * @param runnable
     *            A bit of code that must run on the GL thread
     */
    public void runOnGlThread(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable must not be null");
        }
        mRunnables.add(runnable);
    }

    /**
     * Subscribes a {@link FrameListener}.
     * 
     * Each frame listener is called, once per frame, after any pending
     * {@linkplain #runOnGlThread(Runnable) GL callbacks} and before
     * {@link MeganekkoActivity#frame()}.
     * 
     * @param frameListener
     *            A callback that will fire once per frame, until it is
     *            {@linkplain #unregisterFrameListener(FrameListener)
     *            unregistered}
     * 
     * @deprecated Use {@code OnFrameListener} and
     *             {@code Scene#addOnFrameListener(OnFrameListener)}
     */
    public void registerFrameListener(FrameListener frameListener) {
        if (frameListener == null) {
            throw new IllegalArgumentException("frameListener must not be null");
        }
        mFrameListeners.add(frameListener);
    }

    /**
     * Remove a previously-subscribed {@link FrameListener}.
     * 
     * @param frameListener
     *            An instance of a {@link FrameListener} implementation.
     *            Unsubscribing a listener which is not actually subscribed will
     *            not throw an exception.
     * 
     * @deprecated Use {@code OnFrameListener} and
     *             {@code Scene#removeOnFrameListener(OnFrameListener)}
     */
    public void unregisterFrameListener(FrameListener frameListener) {
        if (frameListener == null) {
            throw new IllegalArgumentException("frameListener must not be null");
        }
        mFrameListeners.remove(frameListener);
    }

    /**
     * Get {@code GVRContext}.
     * 
     * @return {@code GVRContext}
     */
    public GLContext getGVRContext() {
        return mGVRContext;
    }

    /**
     * Get {@code VrFrame}.
     * 
     * @return {@code VrFrame}
     */
    public VrFrame getVrFrame() {
        return vrFrame;
    }

    public void recenterPose() {
        recenterPose(getAppPtr());
    }
}
