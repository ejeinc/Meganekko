package org.meganekkovr.xml

import android.content.Context
import org.meganekkovr.Entity
import org.w3c.dom.Node

/**
 * To add custom primitive, use `XmlPrimitiveFactory.getInstance().install(new YourPrimitiveHandler())`.
 */
object XmlPrimitiveFactory {

    private val handlers = mutableListOf<XmlPrimitiveHandler>(DefautPrimitive())

    fun install(handler: XmlPrimitiveHandler) {
        handlers.add(handler)
    }

    fun parse(node: Node, context: Context): Entity? {

        for (handler in handlers) {
            val entity = handler.createEntity(node, context)
            if (entity != null) return entity
        }

        return null
    }

    interface XmlPrimitiveHandler {
        /**
         * Create [Entity] from [Node].
         * Typically, this checks [Node.getNodeName] and create instance.
         *
         * @param node    Node
         * @param context Context
         * @return Created entity or `null` if this handler does not support passed node.
         */
        fun createEntity(node: Node, context: Context): Entity?
    }

}
