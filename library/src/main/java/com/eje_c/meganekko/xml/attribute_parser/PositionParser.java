package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

import org.joml.Vector3f;

@Deprecated
public class PositionParser implements XmlAttributeParser {
    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        String position = attributeSet.getAttributeValue(NAMESPACE, "position");
        if (position != null) {
            String[] arr = position.split("\\s+");

            if (arr.length == 3) {
                try {
                    float x = Float.parseFloat(arr[0]);
                    float y = Float.parseFloat(arr[1]);
                    float z = Float.parseFloat(arr[2]);
                    object.position(new Vector3f(x, y, z));
                    return;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        } else {

            // Simple position
            String x = attributeSet.getAttributeValue(NAMESPACE, "x");
            Vector3f vec = new Vector3f();
            if (x != null) {
                try {
                    vec.x = Float.parseFloat(x);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            String y = attributeSet.getAttributeValue(NAMESPACE, "y");
            if (y != null) {
                try {
                    vec.y = Float.parseFloat(y);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            String z = attributeSet.getAttributeValue(NAMESPACE, "z");
            if (z != null) {
                try {
                    vec.z = Float.parseFloat(z);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            object.position(vec);
        }
    }
}
