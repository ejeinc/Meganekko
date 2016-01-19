package com.eje_c.meganekko.xml.attribute_parser;

import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.Transform;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class RotationParser implements XmlAttributeParser {
    @Override
    public void parse(SceneObject object, AttributeSet attributeSet) {

        final Transform transform = object.getTransform();

        String rotation = attributeSet.getAttributeValue(NAMESPACE, "rotation");
        if (rotation != null) {
            String[] arr = rotation.split("\\s+");

            if (arr.length == 4) {
                try {
                    float angle = Float.parseFloat(arr[0]);
                    float axisX = Float.parseFloat(arr[1]);
                    float axisY = Float.parseFloat(arr[2]);
                    float axisZ = Float.parseFloat(arr[3]);
                    transform.setRotationByAxis(angle, axisX, axisY, axisZ);
                    return;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
