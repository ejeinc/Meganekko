package com.eje_c.meganekko.sample;

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.MeganekkoActivity;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;

public class MainActivity extends MeganekkoActivity {
    @Override
    protected void oneTimeInit(VrContext context) {
        Scene scene = new Scene(context);
        SceneObject obj = new SceneObject(context, 1, 1, context.loadTexture(new AndroidResource(context, R.mipmap.ic_launcher)));
        obj.getTransform().setPosition(0, 0, -3);
        scene.addChildObject(obj);
        setScene(scene);
    }
}
