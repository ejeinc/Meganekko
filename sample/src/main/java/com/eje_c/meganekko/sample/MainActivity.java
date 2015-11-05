package com.eje_c.meganekko.sample;

import android.animation.ObjectAnimator;

import com.eje_c.meganekko.MeganekkoActivity;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;

public class MainActivity extends MeganekkoActivity {

    private SceneObject obj;

    @Override
    protected void oneTimeInit(VrContext context) {
        parseAndSetScene(R.xml.scene);
        obj = findObjectById(R.id.myObject);
    }

    @Override
    public void onTouchSingle() {

        // Perform animation
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anim = ObjectAnimator.ofFloat(obj, "opacity", 1, 0, 1);
                anim.start();
            }
        });
    }
}
