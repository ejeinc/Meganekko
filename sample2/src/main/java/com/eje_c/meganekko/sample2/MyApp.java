package com.eje_c.meganekko.sample2;

import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {
    @Override
    public void init() {
        super.init();
        setSceneFromXML(R.xml.scene);
    }
}
