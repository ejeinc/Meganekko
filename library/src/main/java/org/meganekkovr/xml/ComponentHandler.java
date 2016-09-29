package org.meganekkovr.xml;

import android.content.Context;
import android.util.Log;

import org.meganekkovr.Component;
import org.meganekkovr.Entity;
import org.meganekkovr.util.ObjectFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Define {@code component} attribute. Attribute value can be a class name or names separated by space.
 */
class ComponentHandler implements XmlAttributeParser.XmlAttributeHandler {
    private static final String TAG = "ComponentHandler";

    @Override
    public String attributeName() {
        return "component";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        String[] classNames = rawValue.split("\\s+");

        for (String className : classNames) {

            try {

                Class<?> clazz = Class.forName(className.trim());

                // Ignore if class is not child of Component
                if (!Component.class.isAssignableFrom(clazz)) {
                    Log.e(TAG, "Class " + className.trim() + " does not extend Component!");
                    continue;
                }

                Component component = (Component) ObjectFactory.newInstance(clazz, context);
                entity.add(component);

            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
