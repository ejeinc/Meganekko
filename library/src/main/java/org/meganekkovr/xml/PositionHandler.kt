package org.meganekkovr.xml

import android.content.Context

import org.meganekkovr.Entity

/**
 * Define `position` attribute.
 */
internal class PositionHandler : XmlAttributeParser.XmlAttributeHandler {

    override fun attributeName(): String {
        return "position"
    }

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        val strs = rawValue.split("\\s+".toRegex(), 3)
        if (strs.size == 3) {
            val x = strs[0].toFloatOrNull() ?: return
            val y = strs[1].toFloatOrNull() ?: return
            val z = strs[2].toFloatOrNull() ?: return
            entity.setPosition(x, y, z)
        }
    }
}
