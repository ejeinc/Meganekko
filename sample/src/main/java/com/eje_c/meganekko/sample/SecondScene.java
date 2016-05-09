package com.eje_c.meganekko.sample;

import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.utility.Log;

import ovr.KeyCode;

/**
 * This scene demonstrates key event overriding.
 */
public class SecondScene extends Scene {
    private static final String TAG = SecondScene.class.getSimpleName();

    @Override
    public boolean onKeyDown(int keyCode, int repeatCount) {
        Log.d(TAG, "onKeyDown %d %d", keyCode, repeatCount);

        // Override Back key default behavior.
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, repeatCount);
    }

    @Override
    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        Log.d(TAG, "onKeyPress %d %d", keyCode, repeatCount);

        // Override Back key default behavior.
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            MyApp app = (MyApp) getApp();
            app.returnToHome();
            return true;
        }

        return super.onKeyShortPress(keyCode, repeatCount);
    }

    @Override
    public boolean onKeyUp(int keyCode, int repeatCount) {
        Log.d(TAG, "onKeyUp %d %d", keyCode, repeatCount);
        return super.onKeyUp(keyCode, repeatCount);
    }

    @Override
    public boolean onKeyDoubleTap(int keyCode, int repeatCount) {
        Log.d(TAG, "onKeyDoubleTap %d %d", keyCode, repeatCount);
        return super.onKeyDoubleTap(keyCode, repeatCount);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, int repeatCount) {
        Log.d(TAG, "onKeyPress %d %d", keyCode, repeatCount);
        return super.onKeyLongPress(keyCode, repeatCount);
    }
}
