package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class MeshParser implements XmlAttributeParser {

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        String width = attributeSet.getAttributeValue(NAMESPACE, "width");
        String height = attributeSet.getAttributeValue(NAMESPACE, "height");
        if (width != null && height != null) {
            float w = Float.parseFloat(width);
            float h = Float.parseFloat(height);
            object.mesh(Mesh.createQuad(w, h));
        }
    }
}
