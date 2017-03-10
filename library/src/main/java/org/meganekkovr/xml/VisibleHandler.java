package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;

import org.meganekkovr.Entity;

/**
 * Define {@code visible} attribute.
 */
class VisibleHandler implements XmlAttributeParser.XmlAttributeHandler {

    @NonNull
    @Override
    public String attributeName() {
        return "visible";
    }

    @Override
    public void parse(@NonNull Entity entity, @NonNull String rawValue, @NonNull Context context) {

        boolean visible = Boolean.parseBoolean(rawValue);
        entity.setVisible(visible);
    }
}
