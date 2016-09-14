package org.meganekkovr.sampleobj;

import org.meganekkovr.MeganekkoApp;
import org.meganekkovr.xml.XmlAttributeParser;

/**
 * This example shows how to define custom XML attribute and simple .obj parsing demo.
 */
public class App extends MeganekkoApp {

    @Override
    public void init() {
        super.init();

        // Add custom XML attribute
        XmlAttributeParser.getInstance().install(new ObjHandler());
        setSceneFromXml(R.xml.scene);
    }
}
