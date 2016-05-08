package com.eje_c.meganekko.sample;

import android.view.View;

import com.eje_c.meganekko.Frame;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;

import ovr.JoyButton;

public class FirstScene extends Scene {
    private SceneObject button;
    private View buttonView;
    private boolean wasLookingAtButton;

    @Override
    protected void initialize(MeganekkoApp app) {
        super.initialize(app);
        button = findObjectById(R.id.button);
        buttonView = button.view();
    }

    @Override
    public void update(Frame frame) {
        final boolean isLookingAtButton = isLookingAt(button);

        if (isLookingAtButton != wasLookingAtButton) {
            getApp().runOnUiThread(() -> buttonView.setPressed(wasLookingAtButton));
        }

        if (isLookingAtButton && JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE)) {
            MyApp app = (MyApp) getApp();
            app.onTapButton();
        }

        wasLookingAtButton = isLookingAtButton;

        super.update(frame);
    }
}
