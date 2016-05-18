package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

import org.joml.Quaternionf;

@Deprecated
public class RotationParser implements XmlAttributeParser {
    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        String rotation = attributeSet.getAttributeValue(NAMESPACE, "rotation");
        if (rotation != null) {
            String[] arr = rotation.split("\\s+");

            if (arr.length == 4) {
                try {
                    float angle = Float.parseFloat(arr[0]);
                    float axisX = Float.parseFloat(arr[1]);
                    float axisY = Float.parseFloat(arr[2]);
                    float axisZ = Float.parseFloat(arr[3]);
                    object.rotation(new Quaternionf().rotateAxis((float) Math.toRadians(angle), axisX, axisY, axisZ));
                    return;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
