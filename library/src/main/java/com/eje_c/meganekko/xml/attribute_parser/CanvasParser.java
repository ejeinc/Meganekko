package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.scene_objects.CanvasSceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class CanvasParser implements XmlAttributeParser {

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {
        if (!(object instanceof CanvasSceneObject)) return;

        String canvasSize = attributeSet.getAttributeValue(NAMESPACE, "canvasSize");
        if (canvasSize != null) {
            String[] arr = canvasSize.split("\\s+");

            if (arr.length == 2) {
                try {
                    int width = Integer.parseInt(arr[0]);
                    int height = Integer.parseInt(arr[1]);
                    ((CanvasSceneObject) object).setCanvasSize(width, height);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
