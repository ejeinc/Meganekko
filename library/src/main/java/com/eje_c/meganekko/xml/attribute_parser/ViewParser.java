package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.scene_objects.ViewSceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class ViewParser implements XmlAttributeParser {

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {
        if (!(object instanceof ViewSceneObject)) return;

        String layout = attributeSet.getAttributeValue(NAMESPACE, "layout");
        if (layout != null) {
            if (layout.startsWith("@layout/")) {
                ((ViewSceneObject) object).setView(attributeSet.getAttributeResourceValue(null, "layout", 0));
            }
        }
    }
}
