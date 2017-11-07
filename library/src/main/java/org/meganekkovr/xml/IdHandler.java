package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;

import org.meganekkovr.Entity;

/**
 * Define {@code id} attribute.
 */
class IdHandler implements XmlAttributeParser.XmlAttributeHandler {

    @NonNull
    @Override
    public String attributeName() {
        return "id";
    }

    @Override
    public void parse(@NonNull Entity entity, @NonNull String rawValue, @NonNull Context context) {

        // Android Gradle 3.0's xml resource
        if (rawValue.matches("@\\d+")) {
            int id = Integer.parseInt(rawValue.substring(1));
            entity.setId(id);
            return;
        }

        if (XmlAttributeParser.isIdResource(rawValue)) {

            // rawValue = @id/xxx or @+id/xxx
            int id = XmlAttributeParser.toResourceId(rawValue, context);
            if (id != 0) {
                entity.setId(id);
            }

        } else {
            entity.setId(rawValue);
        }
    }
}
