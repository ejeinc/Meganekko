package org.meganekkovr.xml

import android.content.Context
import android.util.Log

import org.meganekkovr.Component
import org.meganekkovr.Entity
import org.meganekkovr.util.ObjectFactory

import java.lang.reflect.InvocationTargetException

/**
 * Define `component` attribute. Attribute value can be a class name or names separated by space.
 */
internal class ComponentHandler : XmlAttributeParser.XmlAttributeHandler {

    override fun attributeName(): String {
        return "component"
    }

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        rawValue.split("\\s+".toRegex()).forEach { className ->
            try {

                val clazz = Class.forName(className.trim())

                // Ignore if class is not child of Component
                if (!Component::class.java.isAssignableFrom(clazz)) {
                    Log.e(TAG, "Class ${className} does not extend Component!")
                    return@forEach
                }

                val component = ObjectFactory.newInstance(clazz, context) as Component
                entity.add(component)

            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "ComponentHandler"
    }
}
