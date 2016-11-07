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
        if (strs.length == 1) {

            // ex. scale="1.2"
            // Apply scaling to all axises.

            float scale = Float.parseFloat(strs[0]);
            entity.setScale(scale, scale, scale);

        } else if (strs.length == 2) {

            // ex. scale="0.5 2.0"
            // Apply scaling to X and Y axises.

            float x = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            entity.setScale(x, y, 1.0f);

        } else if (strs.length == 3) {

            // ex. scale="1.1 1.25 2.0"
            // Apply scaling to each axises.

            float x = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            float z = Float.parseFloat(strs[2]);
            entity.setScale(x, y, z);

        }
    }
}
