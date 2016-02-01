package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MeshParser implements XmlAttributeParser {

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        String mesh = attributeSet.getAttributeValue(NAMESPACE, "mesh");

        if (mesh != null) {

            ensureHaveRenderData(object);

            AndroidResource resource = null;

            try {
                resource = new AndroidResource(context, mesh);
            } catch (IOException e) {
//                    e.printStackTrace();
            }

            if (resource == null) {
                try {
                    resource = new AndroidResource(mesh);
                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
                }
            }

            if (resource != null) {
                object.getRenderData().setMesh(Mesh.from(resource));
                return;
            }
        }

        String width = attributeSet.getAttributeValue(NAMESPACE, "width");
        String height = attributeSet.getAttributeValue(NAMESPACE, "height");
        if (width != null && height != null) {
            float w = Float.parseFloat(width);
            float h = Float.parseFloat(height);

            ensureHaveRenderData(object);

            object.getRenderData().setMesh(Mesh.createQuad(w, h));
        }
    }

    private static void ensureHaveRenderData(SceneObject object) {
        if (object.getRenderData() == null) {
            object.attachRenderData(new RenderData());
        }
    }
}
