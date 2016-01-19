package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class BasicParser implements XmlAttributeParser {

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        int id = attributeSet.getIdAttributeResourceValue(-1);
        if (id != -1) {
            object.setId(id);
        }

        String name = attributeSet.getAttributeValue(NAMESPACE, "name");
        if (name != null) {
            object.setName(name);
        }
    }
}
