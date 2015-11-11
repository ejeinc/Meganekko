package com.eje_c.meganekko.xml;

import com.eje_c.meganekko.VrContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ObjectFactory {

    /**
     * Create instance from class name. Class must have ClassName(VrContext) constructor.
     *
     * @param className
     * @param vrContext
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    static <T> T newInstance(String className, VrContext vrContext) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = Class.forName(className);
        Constructor<?> constructor = clazz.getConstructor(VrContext.class);
        return (T) constructor.newInstance(vrContext);
    }
}
