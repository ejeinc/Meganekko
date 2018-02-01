package org.meganekkovr.xml

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import org.meganekkovr.CameraComponent
import org.meganekkovr.Entity
import org.meganekkovr.Scene
import org.meganekkovr.util.ContextCompat
import org.meganekkovr.util.ObjectFactory
import org.w3c.dom.Node
import java.util.regex.Pattern

/**
 * Define default primitives such as &lt;scene&gt;, &lt;entity&gt;, &lt;view&gt;, &lt;img&gt;, and &lt;camera&gt;.
 */
internal class DefautPrimitive : XmlPrimitiveFactory.XmlPrimitiveHandler {

    override fun createEntity(node: Node, context: Context): Entity? {

        // if node has class attribute, instantiate from class.
        val classAttr = node.attributes.getNamedItem("class")
        if (classAttr != null) {

            val className = classAttr.nodeValue
            return ObjectFactory.newInstance(className, context) as Entity

        }

        // Instantiate from node name
        val name = node.nodeName

        when (name) {
            "scene" -> return Scene()
            "entity" -> return Entity()
            "view" -> return createViewEntity(node, context)
            "img" -> return createImgEntity(node, context)
            "camera" -> return createCameraEntity()
        }

        return null
    }

    private fun createViewEntity(node: Node, context: Context): Entity? {

        val src = node.attributes.getNamedItem("src") ?: return null
        val srcVal = src.nodeValue

        // Android Gradle 3.0's xml resource
        if (srcVal.matches("@\\d+".toRegex())) {
            val id = srcVal.substring(1).toInt()
            val view = LayoutInflater.from(context).inflate(id, null)
            return Entity.from(view)
        }

        // src="@layout/xxx"
        val pattern = Pattern.compile("@layout/(.+)")
        val matcher = pattern.matcher(srcVal)
        if (matcher.find()) {
            val layoutName = matcher.group(1)
            val id = context.resources.getIdentifier(layoutName, "layout", context.packageName)
            if (id != 0) {
                val view = LayoutInflater.from(context).inflate(id, null)
                return Entity.from(view)
            }
        }

        // src="com.my.ViewClassName"
        val clazz = Class.forName(srcVal)
        if (View::class.java.isAssignableFrom(clazz)) {
            val view = ObjectFactory.newInstance(clazz, context) as View
            return Entity.from(view)
        }

        return null
    }

    private fun createImgEntity(node: Node, context: Context): Entity? {

        val src = node.attributes.getNamedItem("src")
        val srcVal = src.nodeValue

        // Android Gradle 3.0's xml resource
        if (srcVal.matches("@\\d+".toRegex())) {
            val id = srcVal.substring(1).toInt()
            val drawable = ContextCompat.getDrawable(context, id)
            return Entity.from(drawable)
        }

        // src="@drawable/xxx"
        val pattern = Pattern.compile("@drawable/(.+)")
        val matcher = pattern.matcher(srcVal)
        if (matcher.find()) {
            val drawableName = matcher.group(1)
            val id = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            if (id != 0) {
                val drawable = ContextCompat.getDrawable(context, id)
                return Entity.from(drawable)
            }
        }

        return null
    }

    private fun createCameraEntity(): Entity {
        val entity = Entity()
        entity.add(CameraComponent())
        return entity
    }
}
