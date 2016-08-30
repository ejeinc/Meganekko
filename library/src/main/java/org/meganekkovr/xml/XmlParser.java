package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.meganekkovr.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

    public XmlParser(Context context) {
        this.context = context;
    }

    public Entity parseAsset(String assetName) throws XmlParserException {
        try (InputStream stream = context.getAssets().open(assetName)) {
            return parse(stream);
        } catch (IOException e) {
            throw new XmlParserException(e);
        }
    }

    public Entity parseFile(File file) throws XmlParserException {
        try {
            Document document = documentBuilderFactory.newDocumentBuilder().parse(file);
            return parse(document.getDocumentElement());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new XmlParserException(e);
        }
    }

    public Entity parseUri(String uri) throws XmlParserException {
        try {
            Document document = documentBuilderFactory.newDocumentBuilder().parse(uri);
            return parse(document.getDocumentElement());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new XmlParserException(e);
        }
    }

    public Entity parse(InputStream is) throws XmlParserException {
        try {
            Document document = documentBuilderFactory.newDocumentBuilder().parse(is);
            return parse(document.getDocumentElement());
        } catch (SAXException | IOException | ParserConfigurationException e) {
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
