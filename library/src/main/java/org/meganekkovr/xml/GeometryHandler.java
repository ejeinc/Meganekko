package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;

import org.meganekkovr.Entity;
import org.meganekkovr.GeometryComponent;

import java.util.Map;

/**
 * Define {@code geometry} attribute.
 */
class GeometryHandler implements XmlAttributeParser.XmlAttributeHandler {

    @NonNull
    @Override
    public String attributeName() {
        return "geometry";
    }

    @Override
    public void parse(@NonNull Entity entity, @NonNull String rawValue, @NonNull Context context) {

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
                case "dome": {
                    float lat = map.containsKey("lat") ? Float.parseFloat(map.get("lat")) : 0;
                    geometryComponent.buildDome((float) Math.toRadians(lat));
                }
                break;
                case "spherePatch": {
                    float fov = map.containsKey("fov") ? Float.parseFloat(map.get("fov")) : 0;
                    geometryComponent.buildSpherePatch(fov);
                }
                break;
            }
        }

        if (geometryComponent != null) {
            entity.add(geometryComponent);
        }
    }
}
