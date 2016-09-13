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
import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                Class<?> clazz = Class.forName(className);
                if (Entity.class.isAssignableFrom(clazz)) {

                    // Support AndroidAnnotations @EBean
                    for (Method method : clazz.getDeclaredMethods()) {

                        // Generated class has static getInstance_(Context) method. Use it if exists.
                        if ("getInstance_".equals(method.getName())
                                && method.getParameterTypes().length == 1
                                && method.getParameterTypes()[0].equals(Context.class)) {
                            return (Entity) method.invoke(null, context);
                        }
                    }

                    // Default constructor
                    return (Entity) clazz.newInstance();
                }
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
