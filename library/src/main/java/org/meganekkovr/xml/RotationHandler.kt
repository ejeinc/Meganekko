package org.meganekkovr.xml

import android.content.Context
import org.joml.Quaternionf
import org.meganekkovr.Entity
import java.util.*

/**
 * Define `rotation` attribute.
 */
internal class RotationHandler : XmlAttributeParser.XmlAttributeHandler {

    override fun attributeName(): String {
        return "rotation"
    }

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        val strs = rawValue.split("\\s+".toRegex(), 3)
        if (strs.size >= 3) {

            val x = strs[0].toDoubleOrNull() ?: return
            val y = strs[1].toDoubleOrNull() ?: return
            val z = strs[2].toDoubleOrNull() ?: return

            val xRad = Math.toRadians(x).toFloat()
            val yRad = Math.toRadians(y).toFloat()
            val zRad = Math.toRadians(z).toFloat()

            val rotOrder = if (strs.size == 4) strs[3].toUpperCase(Locale.US) else "XYZ"
            val q = Quaternionf()
            when (rotOrder) {
                "XYZ" -> q.rotateXYZ(xRad, yRad, zRad)
                "YXZ" -> q.rotateYXZ(xRad, yRad, zRad)
                "ZYX" -> q.rotateZYX(xRad, yRad, zRad)
            }
            entity.rotation = q
        }
    }
}
