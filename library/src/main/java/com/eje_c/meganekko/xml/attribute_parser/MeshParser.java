package com.eje_c.meganekko.xml.attribute_parser;

import android.util.AttributeSet;

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.xml.XmlAttributeParser;

import java.io.IOException;

public class MeshParser implements XmlAttributeParser {

    @Override
    public void parse(SceneObject object, AttributeSet attributeSet) {

        // Already have mesh
        if (object.getRenderData() != null && object.getRenderData().getMesh() != null) return;

        String mesh = attributeSet.getAttributeValue(NAMESPACE, "mesh");

        if (mesh != null) {
            try {
                object.getRenderData().setMesh(VrContext.get().loadMesh(new AndroidResource(VrContext.get(), mesh)));
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String width = attributeSet.getAttributeValue(NAMESPACE, "width");
        String height = attributeSet.getAttributeValue(NAMESPACE, "height");
        if (width != null && height != null) {
            float w = Float.parseFloat(width);
            float h = Float.parseFloat(height);
            object.getRenderData().setMesh(VrContext.get().createQuad(w, h));
        }
    }
}
