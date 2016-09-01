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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.eje_c.meganekko.Frame;
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.oculus.vrappframework.VrActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ovr.App;
import ovr.VrFrame;

/**
 * The Meganekko application will have a single Android {@link Activity} which extends {@link MeganekkoActivity},
 * not directly from {@code Activity}. {@code MeganekkoActivity} creates and manages the internal classes which use
 * sensor data to manage a viewpoint, and thus present an appropriate stereoscopic view of your scene graph.
 */
public class MeganekkoActivity extends VrActivity implements Meganekko {

    static {
        System.loadLibrary("meganekko");
    }

    private App mApp;
    private MeganekkoApp meganekkoApp;

    private static native long nativeSetAppInterface(VrActivity act, String fromPackageName, String commandString, String uriString);

    private static native void nativeHideGazeCursor(long appPtr);

    private static native void nativeShowGazeCursor(long appPtr);

    public static native void setDebugOptionEnable(boolean enable);

    private static native void recenterPose(long appPtr);

    @Override
    public MeganekkoApp createMeganekkoApp(Meganekko meganekko) {

        final Bundle metaData = getApplicationInfo().metaData;
        if (metaData == null) return null;

        String appClass = metaData.getString("com.eje_c.meganekko.App");
        if (appClass == null) return null;

        try {
            Class<? extends MeganekkoApp> clazz = (Class<? extends MeganekkoApp>) Class.forName(appClass);
            for (Constructor<?> constructor : clazz.getConstructors()) {
                Class<?>[] params = constructor.getParameterTypes();

                if (params.length == 1 && params[0].equals(Meganekko.class)) {

                    // Old constructor MeganekkoApp(Meganekko)
                    return (MeganekkoApp) constructor.newInstance(this);

                } else if (params.length == 0) {

                    // Call default constructor
                    MeganekkoApp app = (MeganekkoApp) constructor.newInstance();
                    app.setMeganekko(this);

                    return app;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String commandString = getCommandStringFromIntent(intent);
        String fromPackageNameString = getPackageStringFromIntent(intent);
        String uriString = getUriStringFromIntent(intent);

        setAppPtr(nativeSetAppInterface(this, fromPackageNameString, commandString, uriString));

        mApp = new App(getAppPtr());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (meganekkoApp != null) {
            meganekkoApp.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (meganekkoApp != null) {
            meganekkoApp.onResume();
        }
    }

    /**
     * Called from native AppInterface::EnteredVrMode() at first time.
     */
    protected void oneTimeInit() {

        meganekkoApp = createMeganekkoApp(this);
        meganekkoApp.init();
    }

    /**
     * Called from native AppInterface::frame().
     */
    private void frame(long vrFramePtr) {

        // Setup VrFrame
        Frame vrFrame = meganekkoApp.getFrame();
        if (vrFrame == null) {
            vrFrame = new VrFrame(vrFramePtr);
            meganekkoApp.setFrame(vrFrame);
        }

        meganekkoApp.update();
    }

    /**
     * Called from native AppInterface::EnteredVrMode().
     */
    protected void enteredVrMode() {
        meganekkoApp.enteredVrMode();
    }

    /**
     * Called from native AppInterface::LeavingVrMode().
     */
    protected void leavingVrMode() {
        meganekkoApp.leavingVrMode();
    }

    /**
     * Called from native AppInterface::LeavingVrMode().
     */
    protected void oneTimeShutDown() {
        meganekkoApp.shutdown();
    }

    protected void onHmdMounted() {
        meganekkoApp.onHmdMounted();
    }

    protected void onHmdUnmounted() {
        meganekkoApp.onHmdUnmounted();
    }

    public void hideGazeCursor() {
        nativeHideGazeCursor(getAppPtr());
    }

    public void showGazeCursor() {
        nativeShowGazeCursor(getAppPtr());
    }

    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        return meganekkoApp.onKeyShortPress(keyCode, repeatCount);
    }

    public boolean onKeyDoubleTap(int keyCode, int repeatCount) {
        return meganekkoApp.onKeyDoubleTap(keyCode, repeatCount);
    }

    public boolean onKeyLongPress(int keyCode, int repeatCount) {
        return meganekkoApp.onKeyLongPress(keyCode, repeatCount);
    }

    public boolean onKeyDown(int keyCode, int repeatCount) {
        return meganekkoApp.onKeyDown(keyCode, repeatCount);
    }

    public boolean onKeyUp(int keyCode, int repeatCount) {
        return meganekkoApp.onKeyUp(keyCode, repeatCount);
    }

    public boolean onKeyMax(int keyCode, int repeatCount) {
        return meganekkoApp.onKeyMax(keyCode, repeatCount);
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

    @Override
    public void recenter() {
        recenterPose(getAppPtr());
    }

    public App getApp() {
        return mApp;
    }

    @Override
    public Context getContext() {
        return this;
    }

    private long getNativeScene() {
        return meganekkoApp.getScene().getNative();
    }
}