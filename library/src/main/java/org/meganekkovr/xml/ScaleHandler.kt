package org.meganekkovr.xml

import android.content.Context

import org.meganekkovr.Entity

/**
 * Define `scale` attribute.
 */
internal class ScaleHandler : XmlAttributeParser.XmlAttributeHandler {

    override fun attributeName(): String {
        return "scale"
    }

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        val strs = rawValue.split("\\s+".toRegex(), 3)
        when (strs.size) {
            1 -> {

                // ex. scale="1.2"
                // Apply scaling to all axises.

                val scale = java.lang.Float.parseFloat(strs[0])
                entity.setScale(scale, scale, scale)

            }
            2 -> {

                // ex. scale="0.5 2.0"
                // Apply scaling to X and Y axises.

                val x = java.lang.Float.parseFloat(strs[0])
                val y = java.lang.Float.parseFloat(strs[1])
                entity.setScale(x, y, 1.0f)

            }
            3 -> {

                // ex. scale="1.1 1.25 2.0"
                // Apply scaling to each axises.

                val x = java.lang.Float.parseFloat(strs[0])
                val y = java.lang.Float.parseFloat(strs[1])
                val z = java.lang.Float.parseFloat(strs[2])
                entity.setScale(x, y, z)

            }
        }
    }
}
