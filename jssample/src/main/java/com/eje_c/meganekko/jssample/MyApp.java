package com.eje_c.meganekko.jssample;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    public MyApp(Meganekko meganekko) {
        super(meganekko);
        resetScene();
    }

    public void resetScene() {
        setSceneFromAsset("scene.xml");
    }
}
