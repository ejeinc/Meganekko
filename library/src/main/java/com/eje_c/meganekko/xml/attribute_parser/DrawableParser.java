package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class DrawableParser implements XmlAttributeParser {

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        String drawable = attributeSet.getAttributeValue(NAMESPACE, "drawable");

        if (drawable != null) {
            if (drawable.startsWith("@drawable/") || drawable.startsWith("@mipmap/")) {
                int res = attributeSet.getAttributeResourceValue(NAMESPACE, "drawable", 0);
                setDrawable(object, ContextCompat.getDrawable(context, res));
                return;
            }
        }

        // alternative attribute
        drawable = attributeSet.getAttributeValue(NAMESPACE, "texture");

        if (drawable != null) {
            if (drawable.startsWith("@drawable/") || drawable.startsWith("@mipmap/")) {
                int res = attributeSet.getAttributeResourceValue(NAMESPACE, "texture", 0);
                setDrawable(object, ContextCompat.getDrawable(context, res));
                return;
            }
        }
    }

    private static void setDrawable(SceneObject object, Drawable d) {
        RenderData renderData = object.getRenderData();

        if (renderData == null) {
            renderData = new RenderData();
            object.attachRenderData(renderData);
        }

        renderData.getMaterial().getTexture().set(d);
    }
}
