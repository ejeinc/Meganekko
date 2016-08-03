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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import ovr.JoyButton;

/**
 * The scene graph
 */
public class Scene extends SceneObject {

    private boolean mInitialized;
    private MeganekkoApp mApp;
    private float simulateTouchAdditionalY;
    private Bundle mArguments;

    private static native boolean isLookingAt(long scene, long sceneObject);

    private static native void getLookingPoint(long scene, long sceneObject, boolean axisInWorld, float[] val);

    private static native void setViewPosition(long scene, float x, float y, float z);

    private static native void getViewPosition(long scene, float[] val);

    private static native void getViewOrientation(long scene, float[] val);

    private static int getEventType(Frame frame) {
        if (JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH)) {
            return MotionEvent.ACTION_DOWN;
        } else if (JoyButton.contains(frame.getButtonState(), JoyButton.BUTTON_TOUCH)) {
            return MotionEvent.ACTION_MOVE;
        } else if (JoyButton.contains(frame.getButtonReleased(), JoyButton.BUTTON_TOUCH)) {
            return MotionEvent.ACTION_UP;
        } else {
            return -1;
        }
    }

    private static void dispatchTouchEvent(SceneObject target, View view, int eventType, Vector3f lookingPoint) {
        target.simulateTouchEvent(eventType, lookingPoint.x, lookingPoint.y);
        view.invalidate();
    }

    @Override
    protected native long initNativeInstance();

    /**
     * Supply the construction arguments for this scene.
     * This can only be called after {@link MeganekkoApp#setSceneFromXML(int, Bundle)} is called.
     *
     * @param args Bundle
     */
    public void setArguments(@Nullable Bundle args) {
        this.mArguments = args;
    }

    /**
     * Return the arguments supplied to setArguments(Bundle), if any.
     *
     * @return arguments supplied to {@link #setArguments(Bundle)}, or null.
     */
    @Nullable
    public final Bundle getArguments() {
        return mArguments;
    }

    /**
     * This is called just before this scene is first rendered.
     * Usually, you should keep reference to {@link SceneObject} by {@link #findObjectById(int)}
     * in this method.
     * You must call {@code super.initialize(app)} if you override this method.
     *
     * @param app
     */
    protected void initialize(MeganekkoApp app) {
        mApp = app;
    }

    void onResume(MeganekkoApp app) {

        // Call initialize first time only
        if (!mInitialized) {
            initialize(app);
            mInitialized = true;
        }

        onResume();
    }

    /**
     * Called when the scene becomes main scene with {@link MeganekkoApp#setScene(Scene)}.
     */
    public void onResume() {
    }

    /**
     * Called when the other scene becomes main scene with {@link MeganekkoApp#setScene(Scene)}.
     */
    public void onPause() {
    }

    public boolean isLookingAt(SceneObject target) {

        // cannot look at target has no mesh
        if (target.mesh() == null) return false;

        return isLookingAt(getNative(), target.getNative());
    }

    public Vector3f getLookingPoint(SceneObject target, boolean axisInWorld) {
        synchronized (sTempValuesForJni) {
            getLookingPoint(getNative(), target.getNative(), axisInWorld, sTempValuesForJni);
            return new Vector3f(sTempValuesForJni[0], sTempValuesForJni[1], sTempValuesForJni[2]);
        }
    }

    public void setViewPosition(float x, float y, float z) {
        setViewPosition(getNative(), x, y, z);
    }

    public Vector3f getViewPosition() {
        synchronized (sTempValuesForJni) {
            getViewPosition(getNative(), sTempValuesForJni);
            return new Vector3f(sTempValuesForJni[0], sTempValuesForJni[1], sTempValuesForJni[2]);
        }
    }

    public void setViewPosition(Vector3f pos) {
        setViewPosition(getNative(), pos.x, pos.y, pos.z);
    }

    public Quaternionf getViewOrientation() {
        synchronized (sTempValuesForJni) {
            getViewOrientation(getNative(), sTempValuesForJni);
            return new Quaternionf(sTempValuesForJni[0], sTempValuesForJni[1], sTempValuesForJni[2], sTempValuesForJni[3]);
        }
    }

    /**
     * @param frame    The {@link Frame}.
     * @param target   Target {@link SceneObject}. It have to render texture with {@code View}.
     * @param useSwipe If true swipe motion is enabled.
     */
    public void simulateTouch(Frame frame, final SceneObject target, boolean useSwipe) {
        simulateTouch(frame, target, useSwipe, false);
    }

    /**
     * @param frame              The {@link Frame}.
     * @param target             Target {@link SceneObject}. It have to render texture with {@code View}.
     * @param dispatchOnUiThread if true, {@code View.dispatchTouchEvent} will be called on UI thread.
     *                           Otherwise called in current thread.
     * @param useSwipe
     */
    public void simulateTouch(Frame frame, final SceneObject target, boolean useSwipe, boolean dispatchOnUiThread) {

        // Do nothing when target has no view.
        final View view = target.view();
        if (view == null) return;

        // MotionEvent.ACTION_DOWN, ACTION_MOVE, ACTION_UP or -1
        final int eventType = getEventType(frame);

        if (eventType != -1 && isLookingAt(target)) {
            final Vector3f lookingPoint = getLookingPoint(target, false);

            // Scroll with swipe gesture
            if (useSwipe) {
                final int buttonPressed = frame.getButtonPressed();
                if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_UP)) {
                    simulateTouchAdditionalY = 1;
                } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_DOWN)) {
                    simulateTouchAdditionalY = -1;
                }
            }

            lookingPoint.y += simulateTouchAdditionalY * (frame.getSwipeFraction() - 1.0f);

            if (dispatchOnUiThread) {
                getApp().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dispatchTouchEvent(target, view, eventType, lookingPoint);
                    }
                });
            } else {
                dispatchTouchEvent(target, view, eventType, lookingPoint);
            }

        } else {

            // Stop scrolling
            simulateTouchAdditionalY = 0;

            // Force redraw (required for RecyclerView or something)
            view.invalidate();
        }
    }

    public MeganekkoApp getApp() {
        return mApp;
    }
}
