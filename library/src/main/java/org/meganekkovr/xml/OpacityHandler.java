package org.meganekkovr.xml;

import android.content.Context;

import org.meganekkovr.Entity;

/**
 * Define {@code opacity} attribute.
 */
class OpacityHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "opacity";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        float opacity = Float.parseFloat(rawValue);
        entity.setOpacity(opacity);
    }
}
