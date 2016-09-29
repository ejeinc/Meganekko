package org.meganekkovr.xml;

import android.content.Context;

import org.meganekkovr.Entity;

/**
 * Define {@code visible} attribute.
 */
class VisibleHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "visible";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        boolean visible = Boolean.parseBoolean(rawValue);
        entity.setVisible(visible);
    }
}
