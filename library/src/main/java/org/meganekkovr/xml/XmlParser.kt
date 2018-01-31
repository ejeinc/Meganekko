package org.meganekkovr.xml

import android.content.Context
import android.support.annotation.XmlRes
import org.meganekkovr.Entity
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * This creates [Entity] from XML. XMLs are loaded from asset file, local file, or internet.
 */
class XmlParser(private val context: Context) {

    @Throws(XmlParserException::class)
    fun parseAsset(assetName: String): Entity? {
        try {
            context.assets.open(assetName).use { stream -> return parse(stream) }
        } catch (e: IOException) {
            throw XmlParserException(e)
        }
    }

    @Throws(XmlParserException::class)
    fun parseFile(file: File): Entity? {
        try {
            val document = documentBuilderFactory.newDocumentBuilder().parse(file)
            return parse(document.documentElement)
        } catch (e: Exception) {
            throw XmlParserException(e)
        }
    }

    @Throws(XmlParserException::class)
    fun parseUri(uri: String): Entity? {
        try {
            val document = documentBuilderFactory.newDocumentBuilder().parse(uri)
            return parse(document.documentElement)
        } catch (e: Exception) {
            throw XmlParserException(e)
        }
    }

    @Throws(XmlParserException::class)
    fun parse(`is`: InputStream): Entity? {
        try {
            val document = documentBuilderFactory.newDocumentBuilder().parse(`is`)
            return parse(document.documentElement)
        } catch (e: Exception) {
            throw XmlParserException(e)
        }
    }

    @Throws(XmlParserException::class)
    fun parseXmlResource(@XmlRes xmlRes: Int): Entity? {
        try {
            val document = createDocumentFrom(context.resources.getXml(xmlRes))
            return parse(document.documentElement)
        } catch (e: Exception) {
            throw XmlParserException(e)
        }
    }

    private fun parse(node: Node): Entity? {

        val entity = XmlPrimitiveFactory.getInstance().parse(node, context) ?: return null

        // Parse components
        XmlAttributeParser.getInstance().parse(entity, node, context)

        // Parse children
        val list = node.childNodes
        var i = 0
        val len = list.length
        while (i < len) {
            val childNode = list.item(i)

            // Only process element node
            if (childNode.nodeType == Node.ELEMENT_NODE) {

                val childEntity = parse(childNode)

                if (childEntity != null) {
                    entity.add(childEntity)
                }
            }
            ++i
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
         * @throws ParserConfigurationException
         * @throws IOException
         * @throws XmlPullParserException
         */
        @Throws(ParserConfigurationException::class, IOException::class, XmlPullParserException::class)
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
