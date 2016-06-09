package com.eje_c.meganekko.sample2;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {
    public MyApp(Meganekko meganekko) {
        super(meganekko);
        setSceneFromXML(R.xml.scene);
    }
}
