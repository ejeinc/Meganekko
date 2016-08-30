package org.meganekkovr.xml;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import org.meganekkovr.Entity;
import org.meganekkovr.SurfaceRendererComponent;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Pattern pattern = Pattern.compile("@(.+)/(.+)");
        Matcher matcher = pattern.matcher(renderer);
        if (matcher.find()) {
            String resType = matcher.group(1);
            String resName = matcher.group(2);
            int id = context.getResources().getIdentifier(resName, resType, context.getPackageName());

            switch (resType) {
                case "drawable":
                case "mipmap":
                    surfaceRendererComponent = SurfaceRendererComponent.from(ContextCompat.getDrawable(context, id));
                    break;
                case "layout":
                    surfaceRendererComponent = SurfaceRendererComponent.from(LayoutInflater.from(context).inflate(id, null));
                    break;
            }
        } else {
            try {
                Class<?> clazz = Class.forName(renderer);
                if (SurfaceRendererComponent.CanvasRenderer.class.isAssignableFrom(clazz)) {
                    surfaceRendererComponent = new SurfaceRendererComponent();
                    surfaceRendererComponent.setCanvasRenderer((SurfaceRendererComponent.CanvasRenderer) clazz.newInstance());
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (surfaceRendererComponent != null) {
            entity.add(surfaceRendererComponent);
        }
    }
}
