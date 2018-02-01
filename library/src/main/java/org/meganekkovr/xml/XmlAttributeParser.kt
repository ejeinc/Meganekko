package org.meganekkovr.xml

import android.content.Context
import org.meganekkovr.Entity
import org.w3c.dom.Node
import java.util.regex.Pattern

/**
 * To add custom attribute, use `XmlAttributeParser.getInstance().install(new YourAttrHandler())`.
 */
object XmlAttributeParser {

    private val handlers = mutableMapOf<String, XmlAttributeHandler>()

    init {
        // Install default attribute handlers
        XmlAttributeParser.install(ComponentHandler())
        XmlAttributeParser.install(GeometryHandler())
        XmlAttributeParser.install(IdHandler())
        XmlAttributeParser.install(OpacityHandler())
        XmlAttributeParser.install(PositionHandler())
        XmlAttributeParser.install(RotationHandler())
        XmlAttributeParser.install(ScaleHandler())
        XmlAttributeParser.install(SurfaceHandler())
        XmlAttributeParser.install(VisibleHandler())
    }

    /**
     * Add custom [XmlAttributeHandler].
     *
     * @param handler Custom attribute handler.
     */
    @JvmStatic
    fun install(handler: XmlAttributeHandler) {
        handlers[handler.attributeName] = handler
    }

    fun parse(entity: Entity, node: Node, context: Context) {

        val attrs = node.attributes
        for (i in 0 until attrs.length) {

            val attr = attrs.item(i)

            val attrName = attr.nodeName
            val attrValue = attr.nodeValue

            // Skip unknown attribute
            val attributeHandler = handlers[attrName] ?: continue

            attributeHandler.parse(entity, attrValue, context)
        }
    }

    interface XmlAttributeHandler {

        /**
         * @return Attribute name.
         */
        val attributeName: String

        /**
         * Apply XML attribute to entity.
         *
         * @param entity   Entity
         * @param rawValue Attribute value
         * @param context  Context
         */
        fun parse(entity: Entity, rawValue: String, context: Context)
    }

    /**
     * Convert inline CSS like string `attributeName: attributeValue; ...` to Map.
     * <pre>
     * Map : {
     * prop1: value1,
     * prop2: value2,
     * ...
     * }
    </pre> *
     *
     * @param inlineValue Inline value
     * @return Property : Value map.
     */
    @JvmStatic
    fun parseInlineValue(inlineValue: String): Map<String, String> {
        val map = mutableMapOf<String, String>()

        // "prop1: value1; prop2: value2"
        // to
        // ["prop1: value1", " prop2: value2"]
        inlineValue.split(";".toRegex()).forEach { propValue ->
            // ["prop1: value1", " prop2: value2"]
            // to
            // [["prop1", " value1"], [" prop2", " value2"]]
            val strs = propValue.split(":".toRegex(), 2)
            if (strs.size == 2) {

                // [["prop1", " value1"], [" prop2", " value2"]]
                // to
                // [["prop1", "value1"], ["prop2", "value2"]]
                val name = strs[0].trim()
                val value = strs[1].trim()

                map[name] = value
            }
        }

        return map
    }

    /**
     * @param str string
     * @return `true` if str represents raw resource.
     */
    @JvmStatic
    fun isRawResource(str: String): Boolean {
        return str.startsWith("@raw/")
    }

    /**
     * @param str string
     * @return `true` if str represents layout resource.
     */
    @JvmStatic
    fun isLayoutResource(str: String): Boolean {
        return str.startsWith("@layout/")
    }

    /**
     * @param str string
     * @return `true` if str represents drawable resource.
     */
    @JvmStatic
    fun isDrawableResource(str: String): Boolean {
        return str.startsWith("@drawable/") || str.startsWith("@color/") || str.startsWith("@mipmap/")
    }

    /**
     * @param str string
     * @return `true` if str represents id resource.
     */
    @JvmStatic
    fun isIdResource(str: String): Boolean {
        return str.matches("^@\\+?id/.+$".toRegex())
    }

    /**
     * @param str     string
     * @param context Android context
     * @return Resurce ID or `0` if str don't represent resource.
     */
    @JvmStatic
    fun toResourceId(str: String, context: Context): Int {

        // Android Gradle 3.0's xml resource
        if (str.matches("@\\d+".toRegex())) {
            return str.substring(1).toInt()
        }

        // @drawable/ @+id/ @layout etc...
        val pattern = Pattern.compile("@\\+?(.+)/(.+)")
        val matcher = pattern.matcher(str)

        if (matcher.find()) {
            val name = matcher.group(2)
            val defType = matcher.group(1)
            return context.resources.getIdentifier(name, defType, context.packageName)
        }

        return 0
    }
}

