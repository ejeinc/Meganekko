package org.meganekkovr.sample;

import android.graphics.Color;
import android.util.Log;

import com.eje_c.meganekko.sample3.R;

import org.joml.Vector3f;
import org.meganekkovr.Entity;
import org.meganekkovr.LookDetectorComponent;
import org.meganekkovr.Scene;
import org.meganekkovr.SurfaceRendererComponent;

import org.meganekkovr.FrameInput;
import org.meganekkovr.JoyButton;

public class FirstScene extends Scene {
    private static final String TAG = "FirstScene";
    private Entity planeEntity;
    private Entity button;
    private Entity videocam;
    private PlaneRenderer planeRenderer;
//    private SceneObject cursor, customViewSceneObject, bitmapObj, button, videocam;
//    private ObjectLookingStateDetector buttonLookingStateDetector, videocamLookingStateDetector;

    @Override
    public void init() {
        super.init();

        Log.d(TAG, "init");

        planeEntity = findById(R.id.plane);
        planeRenderer = (PlaneRenderer) planeEntity.getComponent(SurfaceRendererComponent.class).getCanvasRenderer();

        // Cache SceneObjects
        button = findById("button");
        videocam = findById("videocam");

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

//        // To simulate touch control, call this on every frame update
//        simulateTouch(frame, customViewSceneObject, false);
//
//        /*
//         * Swipe event handling and SceneObject animation
//         */
//        if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_UP)) {
//            bitmapObj.animate()
//                    .moveBy(new Vector3f(0, 1, 0))
//                    .start(getApp());
//        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_DOWN)) {
//            bitmapObj.animate()
//                    .moveBy(new Vector3f(0, -1, 0))
//                    .start(getApp());
//        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_FORWARD)) {
//            bitmapObj.animate()
//                    .rotateBy(0, (float) (Math.PI / 4), 0)
//                    .start(getApp());
//        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_BACK)) {
//            bitmapObj.animate()
//                    .rotateBy(0, (float) (-Math.PI / 4), 0)
//                    .start(getApp());
//        }
//
//        super.update(frame);
//    }

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
