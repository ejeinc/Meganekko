package org.meganekkovr.xml;

import android.content.Context;
import android.util.Log;

import org.meganekkovr.Component;
import org.meganekkovr.Entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Define {@code component} attribute. Attribute value can be a class name or names separated by space.
 */
public class ComponentHandler implements XmlAttributeParser.XmlAttributeHandler {
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

                Component component = null;

                // Support AndroidAnnotations @EBean
                for (Method method : clazz.getDeclaredMethods()) {

                    // Generated class has static getInstance_(Context) method. Use it if exists.
                    if ("getInstance_".equals(method.getName())
                            && method.getParameterTypes().length == 1
                            && method.getParameterTypes()[0].equals(Context.class)) {
                        component = (Component) method.invoke(null, context);
                        break;
                    }
                }

                // Or use default constructor
                if (component == null) {
                    component = (Component) clazz.newInstance();
                }

                entity.add(component);

            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
