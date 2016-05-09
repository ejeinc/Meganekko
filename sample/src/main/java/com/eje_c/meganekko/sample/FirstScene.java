package com.eje_c.meganekko.sample;

import android.view.View;

import com.eje_c.meganekko.Frame;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.ObjectLookingStateDetector;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;

import org.joml.Vector3f;

import ovr.JoyButton;

public class FirstScene extends Scene {
    private SceneObject button, videocam;
    private ObjectLookingStateDetector buttonLookingStateDetector, videocamLookingStateDetector;

    /**
     * Initialize task.
     *
     * @param app
     */
    @Override
    protected void initialize(MeganekkoApp app) {
        super.initialize(app);

        // Cache SceneObjects
        button = findObjectById(R.id.button);
        videocam = findObjectById(R.id.ic_videocam);

        buttonLookingStateDetector = new ObjectLookingStateDetector(app, button, new ObjectLookingStateDetector.ObjectLookingStateListener() {
            View buttonView = button.view();

            @Override
            public void onLookStart(SceneObject targetObject, Frame vrFrame) {
                getApp().runOnUiThread(() -> buttonView.setPressed(true));
            }

            @Override
            public void onLooking(SceneObject targetObject, Frame vrFrame) {
            }

            @Override
            public void onLookEnd(SceneObject targetObject, Frame vrFrame) {
                getApp().runOnUiThread(() -> buttonView.setPressed(false));
            }
        });

        videocamLookingStateDetector = new ObjectLookingStateDetector(app, videocam, new ObjectLookingStateDetector.ObjectLookingStateListener() {
            Vector3f focusSize = new Vector3f(1.5f, 1.5f, 1.5f);
            Vector3f originalSize = videocam.scale();

            @Override
            public void onLookStart(SceneObject targetObject, Frame vrFrame) {
                videocam.animate()
                        .scaleTo(focusSize)
                        .start(app);
            }

            @Override
            public void onLooking(SceneObject targetObject, Frame vrFrame) {
            }

            @Override
            public void onLookEnd(SceneObject targetObject, Frame vrFrame) {
                videocam.animate()
                        .scaleTo(originalSize)
                        .start(app);
            }
        });
    }

    @Override
    public void update(Frame frame) {

        // These update() dispatches onLookStart and onLookEnd
        buttonLookingStateDetector.update(frame);
        videocamLookingStateDetector.update(frame);

        // Single tap event can be detected with this method
        final boolean singleTouchDetected = JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE);

        if (singleTouchDetected) {
            if (isLookingAt(button)) {
                MyApp app = (MyApp) getApp();
                app.onTapButton();
            } else if (isLookingAt(videocam)) {
                MyApp app = (MyApp) getApp();
                app.onTapVideocam();
            }
        }

        super.update(frame);
    }
}
