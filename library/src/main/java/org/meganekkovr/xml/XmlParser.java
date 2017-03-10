package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;

import org.meganekkovr.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This creates {@link Entity} from XML. XMLs are loaded from asset file, local file, or internet.
 */
public class XmlParser {

    private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final Context context;

    public XmlParser(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Create new {@link Document} from {@link XmlPullParser}.
     *
     * @param parser
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XmlPullParserException
     */
    @NonNull
    private static Document createDocumentFrom(@NonNull XmlPullParser parser) throws ParserConfigurationException, IOException, XmlPullParserException {

        Document document = documentBuilderFactory.newDocumentBuilder().newDocument();

        Node parent = document;
        int type;

        while (true) {

            type = parser.next();
            if (type == XmlPullParser.END_DOCUMENT) break;

            switch (type) {

                case XmlPullParser.START_TAG:
                    Element element = document.createElement(parser.getName());

                    // Set attributes
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String attr = parser.getAttributeName(i);
                        String value = parser.getAttributeValue(i);
                        element.setAttribute(attr, value);
                    }

                    // Add to parent
                    parent.appendChild(element);

                    // I'm a parent
                    parent = element;
                    break;

                case XmlPullParser.END_TAG:
                    // Next parent
                    parent = parent.getParentNode();
                    break;
            }
        }

        return document;
    }

    @Nullable
    public Entity parseAsset(@NonNull String assetName) throws XmlParserException {
        try (InputStream stream = context.getAssets().open(assetName)) {
            return parse(stream);
        } catch (IOException e) {
            throw new XmlParserException(e);
        }
    }

    @Nullable
    public Entity parseFile(@NonNull File file) throws XmlParserException {
        try {
            Document document = documentBuilderFactory.newDocumentBuilder().parse(file);
            return parse(document.getDocumentElement());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new XmlParserException(e);
        }
    }

    @Nullable
    public Entity parseUri(@NonNull String uri) throws XmlParserException {
        try {
            Document document = documentBuilderFactory.newDocumentBuilder().parse(uri);
            return parse(document.getDocumentElement());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new XmlParserException(e);
        }
    }

    @Nullable
    public Entity parse(@NonNull InputStream is) throws XmlParserException {
        try {
            Document document = documentBuilderFactory.newDocumentBuilder().parse(is);
            return parse(document.getDocumentElement());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new XmlParserException(e);
        }
    }

    @Nullable
    public Entity parseXmlResource(@XmlRes int xmlRes) throws XmlParserException {
        try {
            Document document = createDocumentFrom(context.getResources().getXml(xmlRes));
            return parse(document.getDocumentElement());
        } catch (ParserConfigurationException | IOException | XmlPullParserException e) {
            throw new XmlParserException(e);
        }
    }

    @Nullable
    private Entity parse(@NonNull Node node) {

        Entity entity = XmlPrimitiveFactory.getInstance().parse(node, context);

        // Ignore unknown element
        if (entity == null) return null;

        // Parse components
        XmlAttributeParser.getInstance().parse(entity, node, context);

        // Parse children
        NodeList list = node.getChildNodes();
        for (int i = 0, len = list.getLength(); i < len; ++i) {
            Node childNode = list.item(i);

            // Only process element node
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {

                Entity childEntity = parse(childNode);

                if (childEntity != null) {
                    entity.add(childEntity);
                }
            }
        }

        return entity;
    }
}
