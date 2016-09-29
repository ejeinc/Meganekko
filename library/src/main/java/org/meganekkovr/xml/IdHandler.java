package org.meganekkovr.xml;

import android.content.Context;

import org.meganekkovr.Entity;

/**
 * Define {@code id} attribute.
 */
class IdHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "id";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

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
