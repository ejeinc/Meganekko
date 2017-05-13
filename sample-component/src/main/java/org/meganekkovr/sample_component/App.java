package org.meganekkovr.sample_component;

import org.meganekkovr.MeganekkoApp;

public class App extends MeganekkoApp {
    @Override
    public void init() {
        super.init();
        setSceneFromXml(R.xml.scene);
    }
}
