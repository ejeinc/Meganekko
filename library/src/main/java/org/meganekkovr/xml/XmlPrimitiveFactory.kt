package org.meganekkovr.xml

import android.content.Context

import org.meganekkovr.Entity
import org.w3c.dom.Node
import java.util.concurrent.CopyOnWriteArraySet

/**
 * To add custom primitive, use `XmlPrimitiveFactory.getInstance().install(new YourPrimitiveHandler())`.
 */
class XmlPrimitiveFactory private constructor() {

    private val handlers = CopyOnWriteArraySet<XmlPrimitiveHandler>()

    fun parse(node: Node, context: Context): Entity? {

        for (handler in handlers) {
            val entity = handler.createEntity(node, context)
            if (entity != null) return entity
        }

        return null
    }

    fun install(handler: XmlPrimitiveHandler) {
        handlers.add(handler)
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

    companion object {

        // singleton
        private var _instance: XmlPrimitiveFactory? = null

        init {
            XmlPrimitiveFactory.getInstance().install(DefautPrimitive())
        }

        @Synchronized
        @JvmStatic
        fun getInstance(): XmlPrimitiveFactory {
            if (_instance == null) {
                _instance = XmlPrimitiveFactory()
            }
            return _instance!!
        }
    }
}
