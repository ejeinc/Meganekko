package org.meganekkovr.sample;

import org.meganekkovr.FrameInput;
import org.meganekkovr.JoyButton;
import org.meganekkovr.MeganekkoApp;
import org.meganekkovr.ovrjni.OVRApp;

public class MyApp extends MeganekkoApp {
    private FirstScene firstScene; // cache first scene for returning from switched scene

    @Override
    public void init() {
        super.init();

        firstScene = (FirstScene) setSceneFromXmlAsset("scene.xml");
//        Scene scene = setSceneFromXml("https://dl.dropboxusercontent.com/u/11794879/scene.xml");
//        Scene scene = setSceneFromXml(new File(Environment.getExternalStorageDirectory(), "Download/scene.xml"));
    }

    @Override
    public void update(FrameInput frame) {

        // Handle long press touch gesture.
        if (JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH_LONGPRESS)) {

            // Reset head tracking orientation.
            OVRApp.getInstance().recenterYaw(true);
        }

        super.update(frame);
    }

    /**
     * Called from {@link FirstScene}.
     */
    public void onTapButton() {
        if (getScene() instanceof FirstScene) {
            setSceneFromXmlAsset("second_scene.xml");
        }
    }

    /**
     * Called from {@link FirstScene}.
     */
    public void onTapVideocam() {
        if (getScene() instanceof FirstScene) {
            setSceneFromXmlAsset("video_scene.xml");
        }
    }

    public void returnToHome() {
        setScene(firstScene);
    }
}
