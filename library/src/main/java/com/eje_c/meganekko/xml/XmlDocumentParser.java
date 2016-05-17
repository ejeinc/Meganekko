package com.eje_c.meganekko.xml;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RawRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.scene_objects.GlobeSceneObject;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Creates {@link Scene} and {@link SceneObject}s from XML.
 * This class enables loading {@link Scene} from remote.
 */
public class XmlDocumentParser {

    private final Context mContext;

    public XmlDocumentParser(Context context) {
        this.mContext = context;
    }

    /**
     * Parse {@link Scene} from URI.
     *
     * @param uri URI which points to XML data.
     * @return Scene
     * @throws XmlDocumentParserException
     */
    public Scene parseScene(String uri) throws XmlDocumentParserException {
        try {
            return parseScene(uri, defaultDocumentBuilder());
        } catch (ParserConfigurationException e) {
            throw new XmlDocumentParserException("Error in parsing XML from " + uri, e);
        }
    }

    /**
     * Parse {@link Scene} from URI.
     *
     * @param uri             URI which points to XML data.
     * @param documentBuilder
     * @return Scene
     * @throws XmlDocumentParserException
     */
    public Scene parseScene(String uri, DocumentBuilder documentBuilder) throws XmlDocumentParserException {
        try {
            return parseScene(documentBuilder.parse(uri));
        } catch (SAXException | IOException e) {
            throw new XmlDocumentParserException("Error in parsing XML from " + uri, e);
        }
    }

    public Scene parseScene(@RawRes int rawRes) throws XmlDocumentParserException {
        InputStream is = mContext.getResources().openRawResource(rawRes);
        return parseScene(is);
    }

    /**
     * Parse {@link Scene} from asset file.
     *
     * @param assetPath Asset path
     * @return Scene
     * @throws XmlDocumentParserException
     */
    public Scene parseSceneFromAsset(String assetPath) throws XmlDocumentParserException {
        try {
            InputStream is = mContext.getAssets().open(assetPath);
            return parseScene(is);
        } catch (IOException e) {
            throw new XmlDocumentParserException("Error in parsing XML from asset " + assetPath, e);
        }
    }

    /**
     * Parse {@link Scene} from {@code InputStream}.
     *
     * @param is InputStream
     * @return Scene
     * @throws XmlDocumentParserException
     */
    public Scene parseScene(InputStream is) throws XmlDocumentParserException {
        try {
            return parseScene(is, defaultDocumentBuilder());
        } catch (ParserConfigurationException e) {
            throw new XmlDocumentParserException("Error in parsing XML from " + is.toString(), e);
        }
    }

    public Scene parseScene(InputStream is, DocumentBuilder documentBuilder) throws XmlDocumentParserException {
        try {
            Document document = documentBuilder.parse(is);
            return parseScene(document);
        } catch (IOException | SAXException e) {
            throw new XmlDocumentParserException("Error in parsing XML from " + is.toString(), e);
        }
    }

    private Scene parseScene(Document document) throws XmlDocumentParserException {

        SceneObject root = parseSceneObject(document);

        if (!(root instanceof Scene)) {
            throw new XmlDocumentParserException("XML root element is not Scene");
        }

        return (Scene) root;
    }

    private SceneObject parseSceneObject(Document document) throws XmlDocumentParserException {
        try {
            return parse(document.getDocumentElement());
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new XmlDocumentParserException("Error in parsing XML.", e);
        }
    }

    private SceneObject parse(Element element) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        SceneObject object = createSceneObject(element);

        parseId(element, object);

        parsePosition(element, object);
        parseScale(element, object);
        parseRotation(element, object);

        parseWidthAndHeight(element, object);
        parseTexture(element, object);

        parseChildren(element, object);

        parseOpacity(element, object);
        parseVisible(element, object);
        parseRenderingOrder(element, object);

        return object;
    }

    private void parseRenderingOrder(Element element, SceneObject object) {
        String renderingOrder = element.getAttribute("renderingOrder");
        if (isEmpty(renderingOrder)) return;

        final RenderData renderData = object.getRenderData();
        if (renderData == null) return;

        renderData.setRenderingOrder(Integer.parseInt(renderingOrder));
    }

    private void parseVisible(Element element, SceneObject object) {
        String visible = element.getAttribute("visible");
        if (!isEmpty(visible)) {
            object.setVisible(Boolean.parseBoolean(visible));
        }
    }

    private void parseOpacity(Element element, SceneObject object) {
        String opacity = element.getAttribute("opacity");
        if (!isEmpty(opacity)) {
            object.setOpacity(Float.parseFloat(opacity));
        }
    }

    private void parseChildren(Element element, SceneObject object) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                SceneObject child = parse((Element) childNodes.item(i));
                object.addChildObject(child);
            }
        }
    }

    private void parseTexture(Element element, SceneObject object) {
        String texture = element.getAttribute("texture");

        if (isEmpty(texture)) return;

        if (texture.startsWith("@drawable") || texture.startsWith("@mipmap")) {
            int res = mContext.getResources().getIdentifier(texture.substring(1), "drawable", mContext.getPackageName());
            Drawable drawable = ContextCompat.getDrawable(mContext, res);
            Material material = Material.from(drawable);
            object.material(material);

            if (object.mesh() == null) {
                object.mesh(Mesh.from(drawable));
            }

        } else if (texture.startsWith("@layout")) {
            int res = mContext.getResources().getIdentifier(texture.substring(1), "layout", mContext.getPackageName());
            View view = LayoutInflater.from(mContext).inflate(res, null);
            object.material(Material.from(view));

            if (object.mesh() == null) {
                object.mesh(Mesh.from(view));
            }
        }
    }

    private void parseWidthAndHeight(Element element, SceneObject object) {
        String width = element.getAttribute("width");
        String height = element.getAttribute("height");

        if (isEmpty(width) && isEmpty(height)) return;

        final float w = Float.parseFloat(width);
        final float h = Float.parseFloat(height);
        object.mesh(Mesh.createQuad(w, h));
    }


    private void parseScale(Element element, SceneObject object) {
        String scaleAttr = element.getAttribute("scale");
        String x = element.getAttribute("scaleX");
        String y = element.getAttribute("scaleY");
        String z = element.getAttribute("scaleZ");

        if (isEmpty(scaleAttr) && isEmpty(x) && isEmpty(y) && isEmpty(z)) return;

        Vector3f scale = object.scale();

        if (!isEmpty(scaleAttr)) {
            String[] values = scaleAttr.split("\\s");
            scale.x = Float.parseFloat(values[0]);
            scale.y = Float.parseFloat(values[1]);
            scale.z = Float.parseFloat(values[2]);
        }

        if (!isEmpty(x)) {
            scale.x = Float.parseFloat(x);
        }

        if (!isEmpty(y)) {
            scale.y = Float.parseFloat(y);
        }

        if (!isEmpty(z)) {
            scale.z = Float.parseFloat(z);
        }

        object.scale(scale);
    }

    private void parsePosition(Element element, SceneObject object) {
        String positionAttr = element.getAttribute("position");
        String x = element.getAttribute("x");
        String y = element.getAttribute("y");
        String z = element.getAttribute("z");

        if (isEmpty(positionAttr) && isEmpty(x) && isEmpty(y) && isEmpty(z)) return;

        Vector3f pos = object.position();

        if (!isEmpty(positionAttr)) {
            String[] values = positionAttr.split("\\s");
            pos.x = Float.parseFloat(values[0]);
            pos.y = Float.parseFloat(values[1]);
            pos.z = Float.parseFloat(values[2]);
        }

        if (!isEmpty(x)) {
            pos.x = Float.parseFloat(x);
        }

        if (!isEmpty(y)) {
            pos.y = Float.parseFloat(y);
        }

        if (!isEmpty(z)) {
            pos.z = Float.parseFloat(z);
        }

        object.position(pos);
    }

    private void parseRotation(Element element, SceneObject object) {
        String rotationAttr = element.getAttribute("rotation");
        String quaternionAttr = element.getAttribute("quaternion");
        String rotationXYZAttr = element.getAttribute("rotationXYZ");
        String rotationYXZAttr = element.getAttribute("rotationYXZ");

        if (isEmpty(rotationAttr) && isEmpty(quaternionAttr) && isEmpty(rotationXYZAttr) && isEmpty(rotationYXZAttr))
            return;

        Quaternionf rotation = new Quaternionf();

        if (!isEmpty(rotationAttr)) {
            String[] values = rotationAttr.split("\\s");
            final float angle = Float.parseFloat(values[0]);
            final float axisX = Float.parseFloat(values[1]);
            final float axisY = Float.parseFloat(values[2]);
            final float axisZ = Float.parseFloat(values[3]);
            rotation.rotateAxis((float) Math.toRadians(angle), axisX, axisY, axisZ);
        } else if (!isEmpty(quaternionAttr)) {
            String[] values = quaternionAttr.split("\\s");
            rotation.x = Float.parseFloat(values[0]);
            rotation.y = Float.parseFloat(values[1]);
            rotation.z = Float.parseFloat(values[2]);
            rotation.w = Float.parseFloat(values[3]);
        } else if (!isEmpty(rotationXYZAttr)) {
            String[] values = rotationXYZAttr.split("\\s");
            float x = Float.parseFloat(values[0]);
            float y = Float.parseFloat(values[1]);
            float z = Float.parseFloat(values[2]);
            rotation.rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
        } else if (!isEmpty(rotationYXZAttr)) {
            String[] values = rotationYXZAttr.split("\\s");
            float x = Float.parseFloat(values[0]);
            float y = Float.parseFloat(values[1]);
            float z = Float.parseFloat(values[2]);
            rotation.rotateY((float) Math.toRadians(y))
                    .rotateX((float) Math.toRadians(x))
                    .rotateZ((float) Math.toRadians(z));
        }

        object.rotation(rotation);
    }

    private void parseId(Element element, SceneObject object) {
        String id = element.getAttribute("id");
        if (isEmpty(id)) return;

        if (id.startsWith("@+id")) {
            int res = mContext.getResources().getIdentifier(id.substring(2), "id", mContext.getPackageName());
            object.setId(res);
        } else if (id.startsWith("@id")) {
            int res = mContext.getResources().getIdentifier(id.substring(1), "id", mContext.getPackageName());
            object.setId(res);
        } else {
            object.setId(id.hashCode());
        }
    }

    private SceneObject createSceneObject(Element element) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        final String className = element.getAttribute("class");
        if (!isEmpty(className)) {
            return (Scene) Class.forName(className).newInstance();
        }

        final String tagName = element.getTagName();

        switch (tagName) {
            case "globe":
                return new GlobeSceneObject();
            case "scene":
                return new Scene();
        }

        return new SceneObject();
    }

    private static DocumentBuilder defaultDocumentBuilder() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    private static boolean isEmpty(String id) {
        return id == null || id.isEmpty();
    }
}
