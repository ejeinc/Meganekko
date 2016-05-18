package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

import org.joml.Vector3f;

@Deprecated
public class ScaleParser implements XmlAttributeParser {
    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        String scale = attributeSet.getAttributeValue(NAMESPACE, "scale");
        if (scale != null) {
            String[] arr = scale.split("\\s+");

            if (arr.length == 3) {
                try {
                    float x = Float.parseFloat(arr[0]);
                    float y = Float.parseFloat(arr[1]);
                    float z = Float.parseFloat(arr[2]);
                    object.scale(new Vector3f(x, y, z));
                    return;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        Vector3f vec = object.scale();

        // Simple scale
        String x = attributeSet.getAttributeValue(NAMESPACE, "scaleX");
        if (x != null) {
            try {
                vec.x = Float.parseFloat(x);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String y = attributeSet.getAttributeValue(NAMESPACE, "scaleY");
        if (y != null) {
            try {
                vec.y = Float.parseFloat(y);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String z = attributeSet.getAttributeValue(NAMESPACE, "scaleZ");
        if (z != null) {
            try {
                vec.z = Float.parseFloat(z);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        object.scale(vec);
    }
}
