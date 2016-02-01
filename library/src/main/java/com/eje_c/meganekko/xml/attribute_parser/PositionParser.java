package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.Transform;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class PositionParser implements XmlAttributeParser {
    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        final Transform transform = object.getTransform();

        String position = attributeSet.getAttributeValue(NAMESPACE, "position");
        if (position != null) {
            String[] arr = position.split("\\s+");

            if (arr.length == 3) {
                try {
                    float x = Float.parseFloat(arr[0]);
                    float y = Float.parseFloat(arr[1]);
                    float z = Float.parseFloat(arr[2]);
                    transform.setPosition(x, y, z);
                    return;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        } else {

            // Simple position
            String x = attributeSet.getAttributeValue(NAMESPACE, "x");
            if (x != null) {
                try {
                    transform.setPositionX(Float.parseFloat(x));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            String y = attributeSet.getAttributeValue(NAMESPACE, "y");
            if (y != null) {
                try {
                    transform.setPositionY(Float.parseFloat(y));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            String z = attributeSet.getAttributeValue(NAMESPACE, "z");
            if (z != null) {
                try {
                    transform.setPositionZ(Float.parseFloat(z));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
