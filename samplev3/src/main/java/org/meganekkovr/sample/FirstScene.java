package org.meganekkovr.sample;

import android.graphics.Color;
import android.util.Log;

import com.eje_c.meganekko.sample3.R;

import org.joml.Vector3f;
import org.meganekkovr.Entity;
import org.meganekkovr.FrameInput;
import org.meganekkovr.JoyButton;
import org.meganekkovr.LookDetectorComponent;
import org.meganekkovr.Scene;
import org.meganekkovr.SurfaceRendererComponent;

public class FirstScene extends Scene {
    private static final String TAG = "FirstScene";
    private Entity planeEntity;
    private Entity button;
    private Entity videocam;
    private PlaneRenderer planeRenderer;

    @Override
    public void init() {
        super.init();

        Log.d(TAG, "init");

        // Entity can be retrieve by R.id.xxx
        planeEntity = findById(R.id.plane);
        planeRenderer = (PlaneRenderer) planeEntity.getComponent(SurfaceRendererComponent.class).getCanvasRenderer();

        // Or, String ID
        button = findById("button");
        videocam = findById("videocam");

        /*
         * To control Entity by looking use LookDetectorComponent.
         */
        LookDetectorComponent lookDetectorComponent = new LookDetectorComponent(new LookDetectorComponent.LookListener() {

            @Override
            public void onLookStart(Entity entity, FrameInput frame) {
                button.animate().opacity(0.5f).start();
            }

            @Override
            public void onLookEnd(Entity entity, FrameInput frame) {
                button.animate().opacity(1.0f).start();
            }

            @Override
            public void onLooking(Entity entity, FrameInput frame) {
            }
        });
        button.add(lookDetectorComponent);

        LookDetectorComponent videoCamLookDetector = new LookDetectorComponent(new LookDetectorComponent.LookListener() {
            Vector3f focusSize = new Vector3f(1.5f, 1.5f, 1.5f);
            Vector3f originalSize = new Vector3f(1.0f, 1.0f, 1.0f);

            @Override
            public void onLookStart(Entity entity, FrameInput frame) {

                entity.animate()
                        .scaleTo(focusSize)
                        .start();
            }

            @Override
            public void onLookEnd(Entity entity, FrameInput frame) {

                entity.animate()
                        .scaleTo(originalSize)
                        .start();
            }

            @Override
            public void onLooking(Entity entity, FrameInput frame) {
            }
        });
        videocam.add(videoCamLookDetector);
    }

    @Override
    public void update(FrameInput frame) {

        if (getApp().isLookingAt(planeEntity)) {
            planeRenderer.color = Color.RED;
        } else {
            planeRenderer.color = Color.YELLOW;
        }

        /*
         * Single tap event can be detected with this method
         */
        final int buttonPressed = frame.getButtonPressed();
        final boolean singleTouchDetected = JoyButton.contains(buttonPressed, JoyButton.BUTTON_TOUCH_SINGLE);

        if (singleTouchDetected) {
            onSingleTouchDetected();
        }

        super.update(frame);
    }

    private void onSingleTouchDetected() {
        if (getApp().isLookingAt(button)) {
            MyApp app = (MyApp) getApp();
            app.onTapButton();
        } else if (getApp().isLookingAt(videocam)) {
            MyApp app = (MyApp) getApp();
            app.onTapVideocam();
        }
    }
}
