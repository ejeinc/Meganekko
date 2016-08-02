package com.eje_c.meganekko.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.eje_c.meganekko.Frame;
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.ObjectLookingStateDetector;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.utility.Threads;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ovr.JoyButton;

public class FirstScene extends Scene {
    private SceneObject cursor, customViewSceneObject, bitmapObj, button, videocam;
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
        cursor = findObjectById(R.id.cursor);
        customViewSceneObject = findObjectById(R.id.custom_view_scene_object);
        bitmapObj = findObjectById(R.id.bitmap_obj);
        button = findObjectById(R.id.button);
        videocam = findObjectById(R.id.ic_videocam);

        // load image from URL
        loadImageFromURL(app);

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

    private void loadImageFromURL(MeganekkoApp app) {
        Threads.spawn(() -> {
            // this block don't block rendering because it is executed in background thread.
            // You can use try-with because Gear VR works only devices greater than Android 4.4
            try (InputStream is = new URL("http://www.eje-c.com/common/img/logo.jpg").openStream()) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                // updating SceneObject has to be done in GL thread
                app.runOnGlThread(() -> {
                    bitmapObj.mesh(Mesh.from(bitmap));
                    bitmapObj.material(Material.from(bitmap));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private final Vector3f forward = new Vector3f(0, 0, -5);
    private final Vector3f tmp = new Vector3f();

    @Override
    public void update(Frame frame) {

        // update cursor position
        updateCursorPosition();

        // To simulate touch control, call this on every frame update
        simulateTouch(frame, customViewSceneObject, false);

        // These update() dispatches onLookStart and onLookEnd
        buttonLookingStateDetector.update(frame);
        videocamLookingStateDetector.update(frame);

        /*
         * Single tap event can be detected with this method
         */
        final int buttonPressed = frame.getButtonPressed();
        final boolean singleTouchDetected = JoyButton.contains(buttonPressed, JoyButton.BUTTON_TOUCH_SINGLE);

        if (singleTouchDetected) {
            onSingleTouchDetected();
        }

        /*
         * Swipe event handling and SceneObject animation
         */
        if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_UP)) {
            bitmapObj.animate()
                    .moveBy(new Vector3f(0, 1, 0))
                    .start(getApp());
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_DOWN)) {
            bitmapObj.animate()
                    .moveBy(new Vector3f(0, -1, 0))
                    .start(getApp());
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_FORWARD)) {
            bitmapObj.animate()
                    .rotateBy(0, (float) (Math.PI / 4), 0)
                    .start(getApp());
        } else if (JoyButton.contains(buttonPressed, JoyButton.BUTTON_SWIPE_BACK)) {
            bitmapObj.animate()
                    .rotateBy(0, (float) (-Math.PI / 4), 0)
                    .start(getApp());
        }

        super.update(frame);
    }

    private void onSingleTouchDetected() {
        if (isLookingAt(button)) {
            MyApp app = (MyApp) getApp();
            app.onTapButton();
        } else if (isLookingAt(videocam)) {
            MyApp app = (MyApp) getApp();
            app.onTapVideocam();
        }
    }

    private void updateCursorPosition() {
        Quaternionf headRotation = getViewOrientation();
        headRotation.transform(forward, tmp);
        cursor.position(tmp);
        cursor.rotation(headRotation);
    }
}
