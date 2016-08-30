package org.meganekkovr.xml;

import android.content.Context;

import org.meganekkovr.Entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Define {@code id} attribute.
 */
public class IdHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "id";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        Pattern pattern = Pattern.compile("@\\+?id/(.+)");
        Matcher matcher = pattern.matcher(rawValue);
        if (matcher.find()) {
            String idName = matcher.group(1);
            String packageName = context.getPackageName();
            int id = context.getResources().getIdentifier(idName, "id", packageName);
            if (id != 0) {
                entity.setId(id);
            }
        } else {
            entity.setId(rawValue);
        }
    }
}
