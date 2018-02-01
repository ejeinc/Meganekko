package org.meganekkovr.xml

import android.content.Context
import android.util.Log

import org.meganekkovr.Component
import org.meganekkovr.Entity
import org.meganekkovr.util.ObjectFactory

/**
 * Define `component` attribute. Attribute value can be a class name or names separated by space.
 */
internal class ComponentHandler : XmlAttributeParser.XmlAttributeHandler {

    override val attributeName = "component"

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        rawValue.split("\\s+".toRegex()).forEach { className ->

            val clazz = Class.forName(className.trim())

            // Ignore if class is not child of Component
            if (!Component::class.java.isAssignableFrom(clazz)) {
                Log.e(TAG, "Class ${className} does not extend Component!")
                return@forEach
            }

            val component = ObjectFactory.newInstance(clazz, context) as Component
            entity.add(component)

        }
    }

    companion object {
        private const val TAG = "ComponentHandler"
    }
}
