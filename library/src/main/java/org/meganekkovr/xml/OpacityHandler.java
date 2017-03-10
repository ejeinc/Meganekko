package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;

import org.meganekkovr.Entity;

/**
 * Define {@code opacity} attribute.
 */
class OpacityHandler implements XmlAttributeParser.XmlAttributeHandler {

    @NonNull
    @Override
    public String attributeName() {
        return "opacity";
    }

    @Override
    public void parse(@NonNull Entity entity, @NonNull String rawValue, @NonNull Context context) {

        float opacity = Float.parseFloat(rawValue);
        entity.setOpacity(opacity);
    }
}
