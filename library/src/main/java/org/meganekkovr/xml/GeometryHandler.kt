package org.meganekkovr.xml

import android.content.Context

import org.meganekkovr.Entity
import org.meganekkovr.GeometryComponent

/**
 * Define `geometry` attribute.
 */
internal class GeometryHandler : XmlAttributeParser.XmlAttributeHandler {

    override fun attributeName(): String {
        return "geometry"
    }

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        val map = XmlAttributeParser.parseInlineValue(rawValue)
        val primitive = map["primitive"] ?: return

        val geometryComponent = GeometryComponent()

        when (primitive) {
            "plane" -> {
                val width = map["width"]?.toFloatOrNull() ?: 0.0f
                val height = map["height"]?.toFloatOrNull() ?: 0.0f
                geometryComponent.buildQuad(width, height)
            }
            "globe" -> {
                geometryComponent.buildGlobe()
            }
            "dome" -> {
                val lat = map["lat"]?.toFloatOrNull() ?: 0.0f
                geometryComponent.buildDome(Math.toRadians(lat.toDouble()).toFloat())
            }
            "spherePatch" -> {
                val fov = map["fov"]?.toFloatOrNull() ?: 0.0f
                geometryComponent.buildSpherePatch(fov)
            }
        }

        entity.add(geometryComponent)
    }
}
