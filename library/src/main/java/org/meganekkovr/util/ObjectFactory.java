package org.meganekkovr.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectFactory {

    public static Object newInstance(@NonNull String className, @NonNull Context context) throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        return newInstance(Class.forName(className), context);
    }

    public static Object newInstance(@NonNull Class<?> clazz, @NonNull Context context) throws IllegalAccessException, InstantiationException, InvocationTargetException {

        for (Method method : clazz.getDeclaredMethods()) {

            // AndroidAnnotations support.
            // Generated class has static getInstance_(Context) method.
            // Use it if exists.
            if ("getInstance_".equals(method.getName())
                    && method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0].equals(Context.class)) {

                return method.invoke(context);
            }
        }

        // Search Constructor(Context)
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterTypes().length == 1
                    && constructor.getParameterTypes()[0].equals(Context.class)) {
                return constructor.newInstance(context);
            }
        }

        // Use default constructor
        return clazz.newInstance();
    }
}
