package org.meganekkovr.xml

import android.content.Context
import android.view.LayoutInflater
import org.meganekkovr.Entity
import org.meganekkovr.SurfaceRendererComponent
import org.meganekkovr.util.ContextCompat
import org.meganekkovr.util.ObjectFactory

/**
 * Define `surface` attribute.
 */
internal class SurfaceHandler : XmlAttributeParser.XmlAttributeHandler {

    override val attributeName = "surface"

    override fun parse(entity: Entity, rawValue: String, context: Context) {

        val map = XmlAttributeParser.parseInlineValue(rawValue)
        val renderer = map["renderer"] ?: return

        val surfaceRendererComponent = if (XmlAttributeParser.isDrawableResource(renderer)) {

            // renderer = @drawable/xxx
            val resId = XmlAttributeParser.toResourceId(renderer, context)
            SurfaceRendererComponent.from(ContextCompat.getDrawable(context, resId))

        } else if (XmlAttributeParser.isLayoutResource(renderer)) {

            // renderer = @layout/xxx
            val resId = XmlAttributeParser.toResourceId(renderer, context)
            SurfaceRendererComponent.from(LayoutInflater.from(context).inflate(resId, null))

        } else {

            // renderer = class name

            val clazz = Class.forName(renderer)

            // renderer is a class that extends CanvasRenderer
            if (SurfaceRendererComponent.CanvasRenderer::class.java.isAssignableFrom(clazz)) {
                SurfaceRendererComponent().apply {
                    canvasRenderer = ObjectFactory.newInstance(clazz, context) as SurfaceRendererComponent.CanvasRenderer
                }
            } else {
                return
            }

        }

        entity.add(surfaceRendererComponent)
    }
}
