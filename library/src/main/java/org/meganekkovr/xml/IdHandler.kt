package org.meganekkovr.xml

import android.content.Context

import org.meganekkovr.Entity

/**
 * Define `id` attribute.
 */
internal class IdHandler : XmlAttributeParser.XmlAttributeHandler {

    override fun attributeName(): String {
        return "id"
    }

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        // Android Gradle 3.0's xml resource
        if (rawValue.matches("@\\d+".toRegex())) {
            val id = rawValue.substring(1).toInt()
            entity.id = id
            return
        }

        if (XmlAttributeParser.isIdResource(rawValue)) {

            // rawValue = @id/xxx or @+id/xxx
            val id = XmlAttributeParser.toResourceId(rawValue, context)
            if (id != 0) {
                entity.id = id
            }

        } else {
            entity.setId(rawValue)
        }
    }
}
