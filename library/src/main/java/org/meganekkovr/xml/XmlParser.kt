package org.meganekkovr.xml

import android.content.Context
import android.support.annotation.XmlRes
import org.meganekkovr.Entity
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * This creates [Entity] from XML. XMLs are loaded from asset file, local file, or internet.
 */
class XmlParser(private val context: Context) {

    fun parseAsset(assetName: String): Entity? {
        context.assets.open(assetName).use { stream -> return parse(stream) }
    }

    fun parseFile(file: File): Entity? {
        val document = documentBuilderFactory.newDocumentBuilder().parse(file)
        return parse(document.documentElement)
    }

    fun parseUri(uri: String): Entity? {
        val document = documentBuilderFactory.newDocumentBuilder().parse(uri)
        return parse(document.documentElement)
    }

    fun parse(inputStream: InputStream): Entity? {
        val document = documentBuilderFactory.newDocumentBuilder().parse(inputStream)
        return parse(document.documentElement)
    }

    fun parseXmlResource(@XmlRes xmlRes: Int): Entity? {
        val document = createDocumentFrom(context.resources.getXml(xmlRes))
        return parse(document.documentElement)
    }

    private fun parse(node: Node): Entity? {

        val entity = XmlPrimitiveFactory.parse(node, context) ?: return null

        // Parse components
        XmlAttributeParser.parse(entity, node, context)

        // Parse children
        val list = node.childNodes
        for (i in 0 until list.length) {
            val childNode = list.item(i)

            // Only process element node
            if (childNode.nodeType == Node.ELEMENT_NODE) {

                val childEntity = parse(childNode)

                if (childEntity != null) {
                    entity.add(childEntity)
                }
            }
        }

        return entity
    }

    companion object {

        private val documentBuilderFactory = DocumentBuilderFactory.newInstance()

        /**
         * Create new [Document] from [XmlPullParser].
         *
         * @param parser
         * @return
         */
        private fun createDocumentFrom(parser: XmlPullParser): Document {

            val document = documentBuilderFactory.newDocumentBuilder().newDocument()

            var parent: Node = document
            var type: Int

            while (true) {

                type = parser.next()
                if (type == XmlPullParser.END_DOCUMENT) break

                when (type) {

                    XmlPullParser.START_TAG -> {
                        val element = document.createElement(parser.name)

                        // Set attributes
                        for (i in 0 until parser.attributeCount) {
                            val attr = parser.getAttributeName(i)
                            val value = parser.getAttributeValue(i)
                            element.setAttribute(attr, value)
                        }

                        // Add to parent
                        parent.appendChild(element)

                        // I'm a parent
                        parent = element
                    }

                    XmlPullParser.END_TAG ->
                        // Next parent
                        parent = parent.parentNode
                }
            }

            return document
        }
    }
}
