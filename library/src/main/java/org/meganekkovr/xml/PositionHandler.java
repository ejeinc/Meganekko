package org.meganekkovr.xml;

import android.content.Context;

import org.meganekkovr.Entity;

/**
 * Define {@code position} attribute.
 */
class PositionHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "position";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        String[] strs = rawValue.split("\\s+", 3);
        if (strs.length == 3) {
            float x = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            float z = Float.parseFloat(strs[2]);
            entity.setPosition(x, y, z);
        }
    }
}
