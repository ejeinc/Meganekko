package org.meganekkovr.util

import android.content.Context

object ObjectFactory {

    @JvmStatic
    fun newInstance(className: String, context: Context): Any {
        return newInstance(Class.forName(className), context)
    }

    @JvmStatic
    fun newInstance(clazz: Class<*>, context: Context): Any {

        for (method in clazz.declaredMethods) {

            // AndroidAnnotations support.
            // Generated class has static getInstance_(Context) method.
            // Use it if exists.
            if ("getInstance_" == method.name
                    && method.parameterTypes.size == 1
                    && method.parameterTypes[0] == Context::class.java) {

                return method.invoke(context)
            }
        }

        // Search Constructor(Context)
        for (constructor in clazz.constructors) {
            if (constructor.parameterTypes.size == 1 && constructor.parameterTypes[0] == Context::class.java) {
                return constructor.newInstance(context)
            }
        }

        // Use default constructor
        return clazz.newInstance()
    }
}
