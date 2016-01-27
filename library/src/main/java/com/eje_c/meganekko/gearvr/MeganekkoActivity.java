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

package com.eje_c.meganekko.gearvr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrFrame;
import com.eje_c.meganekko.utility.DockEventReceiver;
import com.eje_c.meganekko.xml.XmlSceneParser;
import com.eje_c.meganekko.xml.XmlSceneParserFactory;
import com.oculus.vrappframework.VrActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ovr.App;

/**
 * The Meganekko application will have a single Android {@link Activity} which extends {@link MeganekkoActivity},
 * not directly from {@code Activity}. {@code MeganekkoActivity} creates and manages the internal classes which use
 * sensor data to manage a viewpoint, and thus present an appropriate stereoscopic view of your scene graph.
 */
public abstract class MeganekkoActivity extends VrActivity implements Meganekko {

    private static final int MAX_EVENTS_PER_FRAME = 16;

    static {
        System.loadLibrary("meganekko");
    }

    private final Queue<Runnable> mRunnables = new LinkedBlockingQueue<Runnable>();
    private InternalSensorManager mInternalSensorManager;
    private boolean mDocked;
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
    private VrFrame vrFrame;
    private Scene mScene;
    private App mApp;
    private DockEventReceiver mDockEventReceiver;
    private MeganekkoApp meganekkoApp;

    private static native long nativeSetAppInterface(VrActivity act, String fromPackageName, String commandString, String uriString);

    private static native void nativeHideGazeCursor(long appPtr);

    private static native void nativeShowGazeCursor(long appPtr);

    public static native void setDebugOptionEnable(boolean enable);

    private static native void recenterPose(long appPtr);

    private static native void setScene(long appPtr, long nativeScene);

    private static native boolean isLookingAt(long appPtr, long nativeSceneObject);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String commandString = getCommandStringFromIntent(intent);
        String fromPackageNameString = getPackageStringFromIntent(intent);
        String uriString = getUriStringFromIntent(intent);

        setAppPtr(nativeSetAppInterface(this, fromPackageNameString, commandString, uriString));

        mDockEventReceiver = new DockEventReceiver(this, mRunOnDock, mRunOnUndock);
        mInternalSensorManager = new InternalSensorManager(this, getAppPtr());

        mApp = new App(getAppPtr());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDockEventReceiver.stop();

        if (meganekkoApp != null) {
            meganekkoApp.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDockEventReceiver.start();

        if (meganekkoApp != null) {
            meganekkoApp.onResume();
        }
    }

    /**
     * Called from native AppInterface::oneTimeInit().
     */
    private void oneTimeInit() {

        meganekkoApp = createMeganekkoApp();
        meganekkoApp.init(this);

        if (!mDocked) {
            mInternalSensorManager.start();
        }
    }

    protected abstract MeganekkoApp createMeganekkoApp();

    /**
     * Called from native AppInterface::frame().
     */
    private void frame(long vrFramePtr) {

        // Setup VrFrame
        if (vrFrame == null) {
            vrFrame = new ovr.VrFrame(vrFramePtr);
        }

        // runOnGlThread handling
        for (int i = 0; !mRunnables.isEmpty() && i < MAX_EVENTS_PER_FRAME; ++i) {
            Runnable event = mRunnables.poll();
            event.run();
        }

        mScene.update(vrFrame);
        meganekkoApp.update(this, vrFrame);
    }

    /**
     * Called from native AppInterface::oneTimeShutDown().
     */
    private void oneTimeShutDown() {

        meganekkoApp.shutdown(this);

        if (!mDocked) {
            mInternalSensorManager.stop();
        }
    }

    public void hideGazeCursor() {
        nativeHideGazeCursor(getAppPtr());
    }

    public void showGazeCursor() {
        nativeShowGazeCursor(getAppPtr());
    }

    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        return mScene.onKeyShortPress(keyCode, repeatCount);
    }

    public boolean onKeyDoubleTap(int keyCode, int repeatCount) {
        return mScene.onKeyDoubleTap(keyCode, repeatCount);
    }

    public boolean onKeyLongPress(int keyCode, int repeatCount) {
        return mScene.onKeyLongPress(keyCode, repeatCount);
    }

    public boolean onKeyDown(int keyCode, int repeatCount) {
        return mScene.onKeyLongPress(keyCode, repeatCount);
    }

    public boolean onKeyUp(int keyCode, int repeatCount) {
        return mScene.onKeyUp(keyCode, repeatCount);
    }

    public boolean onKeyMax(int keyCode, int repeatCount) {
        return mScene.onKeyMax(keyCode, repeatCount);
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
     * Enqueues a callback to be run in the GL thread.
     * This is how you take data generated on a background thread (or the main
     * (GUI) thread) and pass it to the coprocessor, using calls that must be
     * made from the GL thread (aka the "GL context").
     *
     * @param runnable A bit of code that must run on the GL thread
     */
    @Override
    public void runOnGlThread(@NonNull Runnable runnable) {
        mRunnables.add(runnable);
    }

    /**
     * Get {@link VrFrame}.
     *
     * @return {@code VrFrame}
     */
    @Override
    public VrFrame getVrFrame() {
        return vrFrame;
    }

    @Override
    public void recenter() {
        recenterPose(getAppPtr());
    }

    @Override
    public void setSceneFromXML(int xmlRes) {

        XmlSceneParser parser = XmlSceneParserFactory.getInstance(this).getSceneParser();

        try {
            Scene scene = parser.parse(getResources().getXml(xmlRes), null);
            setScene(scene);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get current rendering scene.
     *
     * @return Current rendering scene.
     */
    @Override
    public Scene getScene() {
        return mScene;
    }

    /**
     * Set current rendering scene.
     *
     * @param scene
     */
    @Override
    public synchronized void setScene(@NonNull Scene scene) {

        if (scene == mScene)
            return;

        if (mScene != null) {
            mScene.onPause();
        }

        scene.onResume();

        mScene = scene;
        setScene(getAppPtr(), scene.getNative());
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
    @Override
    public boolean isLookingAt(@NonNull SceneObject object) {
        return isLookingAt(getAppPtr(), object.getNative());
    }

    /**
     * Run {@link Animator} on UI thread and notify end callback on GL thread.
     *
     * @param anim        {@link Animator}.
     * @param endCallback Callback for animation end. This is <b>not</b> called when animation is canceled.
     *                    If you require more complicated callbacks, use {@code AnimatorListener} instead of this.
     */
    @Override
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
     * Cancel {@link Animator} running.
     *
     * @param anim     {@link Animator}.
     * @param callback Callback for canceling operation was called in UI thread.
     */
    @Override
    public void cancel(@NonNull final Animator anim, @Nullable final Runnable callback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                anim.cancel();
                if (callback != null) runOnGlThread(callback);
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }
}