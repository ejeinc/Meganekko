package com.eje_c.meganekko.sample;

import android.animation.ObjectAnimator;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrFrame;
import com.eje_c.meganekko.utility.Log;

import ovr.JoyButton;

public class MyApp implements MeganekkoApp {
    private static final String TAG = "MGN";
    private SceneObject obj;

    @Override
    public void init(Meganekko meganekko) {
        Log.d(TAG, "init");

        meganekko.setSceneFromXML(R.xml.scene);
        obj = meganekko.getScene().findObjectById(R.id.myObject);
    }

    @Override
    public void update(Meganekko meganekko, VrFrame vrFrame) {
//        Log.d(TAG, "update");

        final int buttonPressedBits = vrFrame.getButtonPressed();
        if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_TOUCH_SINGLE)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj, "opacity", 1, 0, 1);
            meganekko.animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_TOUCH_DOUBLE)) {
            meganekko.recenter();
        }

        if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_UP)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionY", obj.getTransform().getPositionY(), obj.getTransform().getPositionY() + 1.0f);
            meganekko.animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_DOWN)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionY", obj.getTransform().getPositionY(), obj.getTransform().getPositionY() - 1.0f);
            meganekko.animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_FORWARD)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionZ", obj.getTransform().getPositionZ(), obj.getTransform().getPositionZ() - 1.0f);
            meganekko.animate(anim, null);
        } else if (JoyButton.contains(buttonPressedBits, JoyButton.BUTTON_SWIPE_BACK)) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(obj.getTransform(), "positionZ", obj.getTransform().getPositionZ(), obj.getTransform().getPositionZ() + 1.0f);
            meganekko.animate(anim, null);
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
