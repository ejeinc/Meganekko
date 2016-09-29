package org.meganekkovr.xml;

import android.content.Context;

import org.meganekkovr.Entity;

/**
 * Define {@code scale} attribute.
 */
class ScaleHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "scale";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        String[] strs = rawValue.split("\\s+", 3);
        if (strs.length == 3) {
            float x = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            float z = Float.parseFloat(strs[2]);
            entity.setScale(x, y, z);
        }
    }
}
