package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;

import org.meganekkovr.Entity;

/**
 * Define {@code position} attribute.
 */
class PositionHandler implements XmlAttributeParser.XmlAttributeHandler {

    @NonNull
    @Override
    public String attributeName() {
        return "position";
    }

    @Override
    public void parse(@NonNull Entity entity, @NonNull String rawValue, @NonNull Context context) {

        String[] strs = rawValue.split("\\s+", 3);
        if (strs.length == 3) {
            float x = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            float z = Float.parseFloat(strs[2]);
            entity.setPosition(x, y, z);
        }
    }
}
