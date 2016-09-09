package org.meganekkovr.sample;

import org.meganekkovr.Scene;

import org.meganekkovr.KeyCode;

/**
 * This scene demonstrates key event overriding.
 */
public class SecondScene extends Scene {

    @Override
    public boolean onKeyDown(int keyCode, int repeatCount) {

        // Override Back key default behavior.
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, repeatCount);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int repeatCount) {

        // Override Back key default behavior.
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            MyApp app = (MyApp) getApp();
            app.returnToHome();
            return true;
        }

        return super.onKeyPressed(keyCode, repeatCount);
    }
}
