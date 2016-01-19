package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.Transform;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class ScaleParser implements XmlAttributeParser {
    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        final Transform transform = object.getTransform();

        String scale = attributeSet.getAttributeValue(NAMESPACE, "scale");
        if (scale != null) {
            String[] arr = scale.split("\\s+");

            if (arr.length == 3) {
                try {
                    float x = Float.parseFloat(arr[0]);
                    float y = Float.parseFloat(arr[1]);
                    float z = Float.parseFloat(arr[2]);
                    transform.setScale(x, y, z);
                    return;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        // Simple scale
        String x = attributeSet.getAttributeValue(NAMESPACE, "scaleX");
        if (x != null) {
            try {
                transform.setScaleX(Float.parseFloat(x));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String y = attributeSet.getAttributeValue(NAMESPACE, "scaleY");
        if (y != null) {
            try {
                transform.setScaleY(Float.parseFloat(y));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String z = attributeSet.getAttributeValue(NAMESPACE, "scaleZ");
        if (z != null) {
            try {
                transform.setScaleZ(Float.parseFloat(z));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
