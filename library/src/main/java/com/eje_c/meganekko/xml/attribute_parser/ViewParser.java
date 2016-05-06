package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.scene_objects.ViewSceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class ViewParser implements XmlAttributeParser {

    private static final float AUTO_SIZE_SCALE = 0.006f;

    // layout & texture are alias
    private static final String[] ATTRS = {
            "layout", "texture"
    };

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        for (String attr : ATTRS) {
            String layout = attributeSet.getAttributeValue(NAMESPACE, attr);
            if (layout == null) continue;

            // Inflate from resource
            if (layout.startsWith("@layout/")) {

                if (object instanceof ViewSceneObject) {
                    // For ViewSceneObject (deprecated)
                    ((ViewSceneObject) object).setView(context, attributeSet.getAttributeResourceValue(NAMESPACE, attr, 0));
                } else {

                    // For normal object
                    int res = attributeSet.getAttributeResourceValue(NAMESPACE, attr, 0);
                    View view = LayoutInflater.from(context).inflate(res, null);
                    ensureHaveRenderData(object);
                    final RenderData renderData = object.getRenderData();
                    Material material = new Material();
                    material.texture().set(view);
                    renderData.setMaterial(material);

                    // Set auto sized view
                    if (attributeSet.getAttributeValue(NAMESPACE, "width") == null
                            && attributeSet.getAttributeValue(NAMESPACE, "height") == null
                            && attributeSet.getAttributeValue(NAMESPACE, "mesh") == null) {

                        Mesh mesh = Mesh.from(view);
                        renderData.setMesh(mesh);
                    }
                }

                return;
            }
        }

    }

    private static void ensureHaveRenderData(SceneObject object) {
        if (object.getRenderData() == null) {
            object.attachRenderData(new RenderData());
        }
    }
}
