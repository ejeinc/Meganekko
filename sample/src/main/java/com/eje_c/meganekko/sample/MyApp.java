package com.eje_c.meganekko.sample;

import android.animation.ObjectAnimator;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.Frame;
import com.eje_c.meganekko.utility.Log;

import ovr.JoyButton;

public class MyApp extends MeganekkoApp {
    private static final String TAG = "MGN";
    private SceneObject obj;

    public MyApp(Meganekko meganekko) {
        super(meganekko);

        setSceneFromXML(R.xml.scene);
        obj = getScene().findObjectById(R.id.myObject);
    }

    @Override
    public void update() {
        super.update();
//        Log.d(TAG, "update");

        Frame frame = getFrame();
        final int buttonPressedBits = frame.getButtonPressed();
        if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_TOUCH_SINGLE)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj, "opacity", 1, 0, 1);
            animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_TOUCH_DOUBLE)) {
            recenter();
        }

        if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_UP)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionY", obj.getTransform().getPositionY(), obj.getTransform().getPositionY() + 1.0f);
            animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_DOWN)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionY", obj.getTransform().getPositionY(), obj.getTransform().getPositionY() - 1.0f);
            animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_FORWARD)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionZ", obj.getTransform().getPositionZ(), obj.getTransform().getPositionZ() - 1.0f);
            animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_BACK)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionZ", obj.getTransform().getPositionZ(), obj.getTransform().getPositionZ() + 1.0f);
            animate(anim, null);
        }
    }

    @Override
    public void shutdown(Meganekko meganekko) {
        Log.d(TAG, "shutdown");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "resumed");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "paused");
    }
}
