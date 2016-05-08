package com.eje_c.meganekko.sample;

import com.eje_c.meganekko.Scene;

import ovr.KeyCode;

public class SecondScene extends Scene {
    @Override
    public boolean onKeyDown(int keyCode, int repeatCount) {
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, repeatCount);
    }

    @Override
    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            MyApp app = (MyApp) getApp();
            app.onBackFromSecondScene();
            return true;
        }
        return super.onKeyShortPress(keyCode, repeatCount);
    }
}
