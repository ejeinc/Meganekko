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

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.xmlpull.v1.XmlPullParserException;

import com.eje_c.meganekko.event.FrameListener;
import com.eje_c.meganekko.event.KeyDoubleTapEvent;
import com.eje_c.meganekko.event.KeyDownEvent;
import com.eje_c.meganekko.event.KeyLongPressEvent;
import com.eje_c.meganekko.event.KeyMaxEvent;
import com.eje_c.meganekko.event.KeyShortPressEvent;
import com.eje_c.meganekko.event.KeyUpEvent;
import com.eje_c.meganekko.event.SwipeBackEvent;
import com.eje_c.meganekko.event.SwipeDownEvent;
import com.eje_c.meganekko.event.SwipeForwardEvent;
import com.eje_c.meganekko.event.SwipeUpEvent;
import com.eje_c.meganekko.event.TouchDoubleEvent;
import com.eje_c.meganekko.event.TouchSingleEvent;
import com.eje_c.meganekko.utility.DockEventReceiver;
import com.eje_c.meganekko.utility.Log;
import com.eje_c.meganekko.xml.XmlSceneParser;
import com.eje_c.meganekko.xml.XmlSceneParserFactory;
import com.oculus.vrappframework.VrActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import de.greenrobot.event.EventBus;
import ovr.App;

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
    private InternalSensorManager mInternalSensorManager;
    private VrContext mVrContext = null;
    private boolean mDocked;
    private VrFrame vrFrame;
    private EventBus mEventBus = EventBus.builder().logNoSubscriberMessages(false).build();
    private Scene mScene;
    private MaterialShaderManager mMaterialShaderManager;
	private App mApp;

    static {
        System.loadLibrary("meganekko");
    }

    private static native long nativeSetAppInterface(VrActivity act,
            String fromPackageName, String commandString, String uriString);

    private static native void nativeHideGazeCursor(long appPtr);

    private static native void nativeShowGazeCursor(long appPtr);

    public static native void setDebugOptionEnable(boolean enable);

    private static native void recenterPose(long appPtr);

    static native void setScene(long appPtr, long nativeScene);

    static native void setShaderManager(long appPtr, long nativeShaderManager);

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

        mVrContext = new VrContext(this);
        mMaterialShaderManager = new MaterialShaderManager(mVrContext);
        mApp = new App(getAppPtr());
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

        setScene(new Scene(mVrContext));
        setShaderManager(getAppPtr(), getMaterialShaderManager().getNative());
        mVrContext.onSurfaceCreated();

        if (!mDocked) {
            mInternalSensorManager.start();
        }

        oneTimeInit(mVrContext);
    }

    /**
     * Called when native oneTimeInit is called.
     * 
     * @param context
     */
    protected void oneTimeInit(VrContext context) {
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

        // Notify frame event
        mEventBus.post(vrFrame);

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

        oneTimeShutDown(mVrContext);
    }

    /**
     * Called when native oneTimeShutDown is called.
     * 
     * @param context
     */
    protected void oneTimeShutDown(VrContext context) {
    }

    public void hideGazeCursor() {
        nativeHideGazeCursor(getAppPtr());
    }

    public void showGazeCursor() {
        nativeShowGazeCursor(getAppPtr());
    }

    public void setMinimumVsyncs(int vsyncs) {
        mApp.setMinimumVsyncs(vsyncs);
    }

    public int getMinimumVsyncs() {
        return mApp.getMinimumVsyncs();
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
        KeyShortPressEvent event = new KeyShortPressEvent(keyCode, repeatCount);
        mEventBus.post(event);
        return event.isPreventDefaultCalled();
    }

    public boolean onKeyDoubleTap(int keyCode, int repeatCount) {
        KeyDoubleTapEvent event = new KeyDoubleTapEvent(keyCode, repeatCount);
        mEventBus.post(event);
        return event.isPreventDefaultCalled();
    }

    public boolean onKeyLongPress(int keyCode, int repeatCount) {
        KeyLongPressEvent event = new KeyLongPressEvent(keyCode, repeatCount);
        mEventBus.post(event);
        return event.isPreventDefaultCalled() || onKeyLongPress(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    }

    public boolean onKeyDown(int keyCode, int repeatCount) {
        KeyDownEvent event = new KeyDownEvent(keyCode, repeatCount);
        mEventBus.post(event);
        return event.isPreventDefaultCalled() || onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    }

    public boolean onKeyUp(int keyCode, int repeatCount) {
        KeyUpEvent event = new KeyUpEvent(keyCode, repeatCount);
        mEventBus.post(event);
        return event.isPreventDefaultCalled() || onKeyUp(keyCode, new KeyEvent(KeyEvent.ACTION_UP, keyCode));
    }

    public boolean onKeyMax(int keyCode, int repeatCount) {
        KeyMaxEvent event = new KeyMaxEvent(keyCode, repeatCount);
        mEventBus.post(event);
        return event.isPreventDefaultCalled();
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
        mEventBus.post(new SwipeUpEvent());
    }

    /**
     * Called from VR thread when swipe down gesture is recognized.
     */
    public void onSwipeDown() {
        mEventBus.post(new SwipeDownEvent());
    }

    /**
     * Called from VR thread when swipe forward gesture is recognized.
     */
    public void onSwipeForward() {
        mEventBus.post(new SwipeForwardEvent());
    }

    /**
     * Called from VR thread when swipe back gesture is recognized.
     */
    public void onSwipeBack() {
        mEventBus.post(new SwipeBackEvent());
    }

    /**
     * Called from VR thread when touch pad single tap is recognized.
     */
    public void onTouchSingle() {
        mEventBus.post(new TouchSingleEvent());
    }

    /**
     * Called from VR thread when touch pad double tap is recognized.
     */
    public void onTouchDouble() {
        mEventBus.post(new TouchDoubleEvent());
    }

    private final Runnable mRunOnDock = new Runnable() {
        @Override
        public void run() {
            mDocked = true;
            mInternalSensorManager.stop();
        }
    };

    private final Runnable mRunOnUndock = new Runnable() {
        @Override
        public void run() {
            mDocked = false;
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
     * {@linkplain #onFrame(FrameListener)} frame listeners}.
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
     * Get {@code VrContext}.
     * 
     * @return {@code VrContext}
     */
    public VrContext getVrContext() {
        return mVrContext;
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

    /**
     * Short hand method for getScene().findObjectByName().
     * 
     * @param name
     * @return Found object or null.
     */
    public SceneObject findObjectByName(String name) {
        return mScene.findObjectByName(name);
    }

    /**
     * Short hand method for getScene().findObjectById().
     *
     * @param id
     * @return Found object or null.
     */
    public SceneObject findObjectById(int id) {
        return mScene.findObjectById(id);
    }

    /**
     * Short hand method for XML scene parsing.
     * 
     * @param xmlRes
     * @return New scene.
     * @deprecated Use {@code MeganekkoActivity#parseAndSetScene(int)}.
     */
    public Scene setScene(int xmlRes) {
        return parseAndSetScene(xmlRes);
    }

    /**
     * Short hand method for XML scene parsing.
     * 
     * @param xmlRes
     *            Scene XML resource.
     * @return New scene.
     */
    public Scene parseAndSetScene(int xmlRes) {

        XmlSceneParser parser = XmlSceneParserFactory.getInstance(mVrContext).getSceneParser();

        try {
            Scene scene = parser.parse(getResources().getXml(xmlRes), new Scene(mVrContext));
            setScene(scene);
            return scene;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Set current rendering scene.
     * 
     * @param scene
     */
    public synchronized void setScene(Scene scene) {

        if (scene == mScene)
            return;

        if (mScene != null) {
            mEventBus.unregister(mScene);
        }

        mEventBus.register(scene);

        mScene = scene;
        setScene(getAppPtr(), scene.getNative());
    }

    /**
     * Get current rendering scene.
     * 
     * @return Current rendering scene.
     */
    public Scene getScene() {
        return mScene;
    }

    /**
     * Add callback for every frame update.
     * 
     * @param listener
     */
    public void onFrame(com.eje_c.meganekko.event.FrameListener listener) {
        mEventBus.register(listener);
    }

    /**
     * Remove callback for every frame update.
     * 
     * @param listener
     */
    public void offFrame(com.eje_c.meganekko.event.FrameListener listener) {
        mEventBus.unregister(listener);
    }
    
    public MaterialShaderManager getMaterialShaderManager() {
        return mMaterialShaderManager;
    }
    
    public App getApp() {
    	return mApp;
    }

	/**
	 * Check if user is looking at target object.
	 * 
	 * @param object target object.
	 * @return true if user is looking at object.
	 */
	public boolean isLookingAt(SceneObject object) {
		if (object.getEyePointeeHolder() == null) {
			object.attachEyePointeeHolder();
		}
		return Picker.pickSceneObject(object, mScene.getMainCamera()) < Float.POSITIVE_INFINITY;
	}

    /**
     * Run {@link Animator} on UI thread and notify end callback on GL thread.
     *
     * @param anim        {@link Animator}.
     * @param endCallback Callback for animation end. This is <b>not</b> called when animation is canceled.
     *                    If you require more complicated callbacks, use {@code AnimatorListener} instead of this.
     */
    public void animate(@NonNull final Animator anim, @Nullable final Runnable endCallback) {

        if (anim.isRunning()) {
            cancel(anim, new Runnable() {
                @Override
                public void run() {
                    animate(anim, endCallback);
                }
            });
            return;
        }

        // Register one time animation end callback
        if (endCallback != null) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    anim.removeListener(this);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    anim.removeListener(this);
                    runOnGlThread(endCallback);
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                anim.start();
            }
        });
    }

    /**
     * Run {@link Animator} on UI thread.
     *
     * @param anim {@link Animator}.
     */
    public void animate(@NonNull Animator anim) {
        animate(anim, null);
    }

    /**
     * Cancel {@link Animator} running.
     *
     * @param anim     {@link Animator}.
     * @param callback Callback for canceling operation was called in UI thread.
     */
    public void cancel(@NonNull final Animator anim, @NonNull final Runnable callback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                anim.cancel();
                if (callback != null) runOnGlThread(callback);
            }
        });
    }

    /**
     * Cancel {@link Animator} running.
     *
     * @param anim
     */
    public void cancel(@NonNull final Animator anim) {
        cancel(anim, null);
    }
}