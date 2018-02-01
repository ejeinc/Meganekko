package org.meganekkovr.xml

import android.content.Context

import org.meganekkovr.Entity

/**
 * Define `visible` attribute.
 */
internal class VisibleHandler : XmlAttributeParser.XmlAttributeHandler {

    override val attributeName = "visible"

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        val visible = rawValue.toBoolean()
        entity.isVisible = visible
    }
}
