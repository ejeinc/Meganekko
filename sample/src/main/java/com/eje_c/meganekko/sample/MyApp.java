package com.eje_c.meganekko.sample;

import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.utility.Log;

import ovr.JoyButton;

/**
 * Meganekko's main app logics.
 */
public class MyApp extends MeganekkoApp {
    private static final String TAG = "MGN";
    private FirstScene firstScene; // cache first scene for returning from switched scene

    @Override
    public void init() {
        super.init();

        // Set first scene
        setSceneFromXML(R.xml.first_scene);
    }

    @Override
    public void onHmdMounted() {
        super.onHmdMounted();
        Log.d(TAG, "onHmdMounted");
    }

    @Override
    public void onHmdUnmounted() {
        super.onHmdUnmounted();
        Log.d(TAG, "onHmdUnmounted");
    }

    /**
     * Called on every frame update.
     */
    @Override
    public void update() {
        super.update(); // This is important! Don't forget to call super.update();

        final int buttonPressed = getFrame().getButtonPressed();
        if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_TOUCH_SINGLE)) {
            Log.d(TAG, "on single tap");
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_TOUCH_DOUBLE)) {
            Log.d(TAG, "on double tap");
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_BACK)) {
            Log.d(TAG, "on swipe back");
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_FORWARD)) {
            Log.d(TAG, "on swipe forward");
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_UP)) {
            Log.d(TAG, "on swipe up");
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_DOWN)) {
            Log.d(TAG, "on swipe down");
        }
//        Log.d(TAG, "update");
    }

    @Override
    public void shutdown() {
        Log.d(TAG, "shutdown");
    }

    /**
     * Called when {@code Activity.onResume} was called.
     * Note: This method will not be called on first launch.
     */
    @Override
    public void onResume() {
        Log.d(TAG, "resumed");
    }

    /**
     * Called when {@code Activity.onPause} was called.
     * Note: This method will be called about after 10 seconds from device is detached from Gear VR.
     */
    @Override
    public void onPause() {
        Log.d(TAG, "paused");
    }

    /**
     * Called from {@link FirstScene}.
     */
    public void onTapButton() {
        if (getScene() instanceof FirstScene) {
            firstScene = (FirstScene) getScene();
            setSceneFromXML(R.xml.second_scene);
        }
    }

    /**
     * Called from {@link FirstScene}.
     */
    public void onTapVideocam() {
        if (getScene() instanceof FirstScene) {
            firstScene = (FirstScene) getScene();
            setSceneFromXML(R.xml.video_scene);
        }
    }

    /**
     * Called from {@link SecondScene} and {@link VideoScene}.
     */
    public void returnToHome() {
        setScene(firstScene);
    }
}
