package com.eje_c.meganekko.xml;

import java.lang.reflect.InvocationTargetException;

class ObjectFactory {

    /**
     * Create instance from class name. Class must have ClassName(VrContext) constructor.
     *
     * @param className
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    static <T> T newInstance(String className) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = Class.forName(className);
        return (T) clazz.newInstance();
    }
}
