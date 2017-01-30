package org.meganekkovr.xml;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import org.meganekkovr.CameraComponent;
import org.meganekkovr.Entity;
import org.meganekkovr.Scene;
import org.meganekkovr.util.ObjectFactory;
import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Define default primitives such as &lt;scene&gt;, &lt;entity&gt;, &lt;view&gt;, &lt;img&gt;, and &lt;camera&gt;.
 */
class DefautPrimitive implements XmlPrimitiveFactory.XmlPrimitiveHandler {

    @Override
    public Entity createEntity(@NonNull Node node, @NonNull Context context) {

        // if node has class attribute, instantiate from class.
        Node classAttr = node.getAttributes().getNamedItem("class");
        if (classAttr != null) {

            String className = classAttr.getNodeValue();

            try {
                return (Entity) ObjectFactory.newInstance(className, context);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Instantiate from node name
        String name = node.getNodeName();

        switch (name) {
            case "scene":
                return new Scene();

            case "entity":
                return new Entity();

            case "view":
                return createViewEntity(node, context);

            case "img":
                return createImgEntity(node, context);

            case "camera":
                return createCameraEntity();
        }

        return null;
    }

    @Nullable
    private Entity createViewEntity(@NonNull Node node, @NonNull Context context) {

        Node src = node.getAttributes().getNamedItem("src");

        if (src != null) {

            String srcVal = src.getNodeValue();

            // src="@layout/xxx"
            Pattern pattern = Pattern.compile("@layout/(.+)");
            Matcher matcher = pattern.matcher(srcVal);
            if (matcher.find()) {
                String layoutName = matcher.group(1);
                int id = context.getResources().getIdentifier(layoutName, "layout", context.getPackageName());
                if (id != 0) {
                    View view = LayoutInflater.from(context).inflate(id, null);
                    return Entity.from(view);
                }
            }

            // src="com.my.ViewClassName"
            try {
                Class<?> clazz = Class.forName(srcVal);
                if (View.class.isAssignableFrom(clazz)) {
                    View view = (View) ObjectFactory.newInstance(clazz, context);
                    return Entity.from(view);
                }
            } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Nullable
    private Entity createImgEntity(@NonNull Node node, @NonNull Context context) {

        Node src = node.getAttributes().getNamedItem("src");

        if (src != null) {

            String srcVal = src.getNodeValue();
            Pattern pattern = Pattern.compile("@drawable/(.+)");
            Matcher matcher = pattern.matcher(srcVal);
            if (matcher.find()) {
                String drawableName = matcher.group(1);
                int id = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
                if (id != 0) {
                    Drawable drawable = ContextCompat.getDrawable(context, id);
                    return Entity.from(drawable);
                }
            }
        }

        return null;
    }

    @NonNull
    private Entity createCameraEntity() {
        Entity entity = new Entity();
        entity.add(new CameraComponent());
        return entity;
    }
}
