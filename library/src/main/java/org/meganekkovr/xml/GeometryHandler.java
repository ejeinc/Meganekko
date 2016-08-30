package org.meganekkovr.xml;

import android.content.Context;

import org.meganekkovr.Entity;
import org.meganekkovr.GeometryComponent;

import java.util.Map;

/**
 * Define {@code geometry} attribute.
 */
public class GeometryHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "geometry";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        GeometryComponent geometryComponent = null;

        Map<String, String> map = XmlAttributeParser.parseInlineValue(rawValue);
        String primitive = map.get("primitive");

        if (primitive != null) {
            geometryComponent = new GeometryComponent();
            switch (primitive) {
                case "plane": {
                    float width = map.containsKey("width") ? Float.parseFloat(map.get("width")) : 0;
                    float height = map.containsKey("height") ? Float.parseFloat(map.get("height")) : 0;
                    geometryComponent.buildQuad(width, height);
                }
                break;
                case "globe": {
                    geometryComponent.buildGlobe();
                }
                break;
            }
        }

        if (geometryComponent != null) {
            entity.add(geometryComponent);
        }
    }
}
