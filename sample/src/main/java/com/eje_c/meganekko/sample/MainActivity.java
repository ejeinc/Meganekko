package com.eje_c.meganekko.sample;

import android.animation.ObjectAnimator;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoActivity;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrFrame;

import ovr.JoyButton;

public class MainActivity extends MeganekkoActivity {

    private SceneObject obj;

    @Override
    protected MeganekkoApp createMeganekkoApp() {
        return new MeganekkoApp() {
            @Override
            public void init(Meganekko meganekko) {
                setSceneFromXML(R.xml.scene);
                obj = findObjectById(R.id.myObject);
            }

            @Override
            public void update(Meganekko meganekko, VrFrame vrFrame) {

                if (JoyButton.contains(vrFrame.getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE)) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(obj, "opacity", 1, 0, 1);
                    meganekko.animate(anim, null);
                }
            }

            @Override
            public void shutdown(Meganekko meganekko) {
            }
        };
    }
}
