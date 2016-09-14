package org.meganekkovr.xml;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import org.meganekkovr.Entity;
import org.meganekkovr.SurfaceRendererComponent;

import java.util.Map;

/**
 * Define {@code surface} attribute.
 */
public class SurfaceHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "surface";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        SurfaceRendererComponent surfaceRendererComponent = null;

        Map<String, String> map = XmlAttributeParser.parseInlineValue(rawValue);
        String renderer = map.get("renderer");

        // Ignore invalid
        if (renderer == null) return;

        if (XmlAttributeParser.isDrawableResource(renderer)) {

            // renderer = @drawable/xxx
            int resId = XmlAttributeParser.toResourceId(renderer, context);
            surfaceRendererComponent = SurfaceRendererComponent.from(ContextCompat.getDrawable(context, resId));

        } else if (XmlAttributeParser.isLayoutResource(renderer)) {

            // renderer = @layout/xxx
            int resId = XmlAttributeParser.toResourceId(renderer, context);
            surfaceRendererComponent = SurfaceRendererComponent.from(LayoutInflater.from(context).inflate(resId, null));

        } else {

            // renderer = class name

            try {
                Class<?> clazz = Class.forName(renderer);

                // renderer is a class that extends CanvasRenderer
                if (SurfaceRendererComponent.CanvasRenderer.class.isAssignableFrom(clazz)) {
                    surfaceRendererComponent = new SurfaceRendererComponent();
                    surfaceRendererComponent.setCanvasRenderer((SurfaceRendererComponent.CanvasRenderer) clazz.newInstance());
                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Set SurfaceRendererComponent if successfully created
        if (surfaceRendererComponent != null) {
            entity.add(surfaceRendererComponent);
        }
    }
}
