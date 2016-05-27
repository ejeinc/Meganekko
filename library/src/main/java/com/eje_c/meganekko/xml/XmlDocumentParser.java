package com.eje_c.meganekko.xml;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.javascript.JS;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Creates {@link Scene} and {@link SceneObject}s from XML.
 * This class enables loading {@link Scene} from remote.
 */
public class XmlDocumentParser {

    private static final String[] EVENT_NAMES = {
            "update",
            "swipeforward", "swipeback", "swipeup", "swipedown",
            "touchsingle", "touchdouble", "touchlongpress",
            "keyshortpress", "keydoubletap", "keylongpress", "keydown", "keyup", "keymax"
    };
    private static DocumentBuilderFactory sDocumentBuilderFactory;
    private final Context mContext;
    private final List<AttributeParser> mAttributeParsers = new ArrayList<>();

    public XmlDocumentParser(Context context) {
        this.mContext = context;
        mAttributeParsers.add(new IdParser());
        mAttributeParsers.add(new PositionParser());
        mAttributeParsers.add(new ScaleParser());
        mAttributeParsers.add(new RotationParser());
        mAttributeParsers.add(new WidthHeightParser());
        mAttributeParsers.add(new TextureParser());
        mAttributeParsers.add(new OpacityParser());
        mAttributeParsers.add(new VisibleParser());
        mAttributeParsers.add(new RenderingOrderParser());
        mAttributeParsers.add(new ScriptParser());
    }

    private static DocumentBuilder defaultDocumentBuilder() throws ParserConfigurationException {
        if (sDocumentBuilderFactory == null) {
            sDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        }
        return sDocumentBuilderFactory.newDocumentBuilder();
    }

    private static boolean isEmpty(String id) {
        return id == null || id.isEmpty();
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
     * @param documentBuilder DocumentBuilder.
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

    /**
     * Parse {@link Scene} from raw resource, such as {@code R.raw.scene}.
     *
     * @param rawRes raw resource.
     * @return Scene
     * @throws XmlDocumentParserException
     */
    public Scene parseScene(@RawRes int rawRes) throws XmlDocumentParserException {
        InputStream is = mContext.getResources().openRawResource(rawRes);
        final String resName = mContext.getResources().getResourceEntryName(rawRes);
        return parseScene(is, "res:///raw/" + resName);
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
            return parseScene(is, "asset:///" + assetPath);
        } catch (IOException e) {
            throw new XmlDocumentParserException("Error in parsing XML from asset " + assetPath, e);
        }
    }

    /**
     * Parse {@link Scene} from {@code InputStream}.
     *
     * @param is          InputStream
     * @param documentUri URI passed to {@link Document#setDocumentURI(String)}.
     * @return Scene
     * @throws XmlDocumentParserException
     */
    public Scene parseScene(InputStream is, String documentUri) throws XmlDocumentParserException {
        try {
            return parseScene(is, documentUri, defaultDocumentBuilder());
        } catch (ParserConfigurationException e) {
            throw new XmlDocumentParserException("Error in parsing XML from " + is.toString(), e);
        }
    }

    /**
     * Parse {@link Scene} from {@code InputStream}.
     *
     * @param is              InputStream
     * @param documentUri     URI passed to {@link Document#setDocumentURI(String)}.
     * @param documentBuilder DocumentBuilder.
     * @return Scene
     * @throws XmlDocumentParserException
     */
    public Scene parseScene(InputStream is, String documentUri, DocumentBuilder documentBuilder) throws XmlDocumentParserException {
        try {
            Document document = documentBuilder.parse(is);
            document.setDocumentURI(documentUri);
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

        /*
         * Parse <script> elements to execute JavaScript.
         */

        Scene scene = (Scene) root;

        try {
            parseScriptNodes(document, scene);
        } catch (IOException e) {
            throw new XmlDocumentParserException("Error while parsing JavaScript in XML", e);
        }

        return (Scene) root;
    }

    private void parseScriptNodes(Document document, Scene scene) throws IOException {

        NodeList scripts = document.getElementsByTagName("script");

        for (int i = 0, len = scripts.getLength(); i < len; ++i) {

            Node scriptNode = scripts.item(i);
            Node src = scriptNode.getAttributes().getNamedItem("src");

            if (src != null) {

                // execte <script src="{code}" />
                String srcVal = src.getNodeValue();
                if (isEmpty(srcVal)) continue;

                // src="@raw/script"
                if (srcVal.startsWith("@raw")) {
                    int rawRes = mContext.getResources().getIdentifier(srcVal.substring(1), "raw", mContext.getPackageName());
                    JS.execRawResource(scene, rawRes);
                    continue;
                }

                // src="url"
                URI srcUri = URI.create(srcVal);

                // if src is not absolute URI, resolve from document URI
                if (!srcUri.isAbsolute()) {
                    srcUri = URI.create(document.getDocumentURI()).resolve(srcUri);
                }

                JS.execURL(scene, srcUri);
            } else {
                // execute <script> {code} </script>
                String code = scriptNode.getTextContent();
                if (!isEmpty(code)) {
                    JS.exec(scene, code);
                }
            }
        }
    }

    @Nullable
    private SceneObject parseSceneObject(Document document) throws XmlDocumentParserException {
        try {
            return parse(document.getDocumentElement());
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new XmlDocumentParserException("Error in parsing XML.", e);
        }
    }

    /**
     * Parse {@link Element} to create {@link SceneObject}.
     * If element is not for a {@link SceneObject}, return {@code null}.
     *
     * @param element Element
     * @return created sceneObject or null.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Nullable
    private SceneObject parse(final Element element) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        final SceneObject object = createSceneObject(element);
        if (object == null) return null;

        // Attach SceneObject to DOM Element
        element.setUserData("sceneObject", object, null);

        final ParserChain chain = new ParserChain(mContext, mAttributeParsers.iterator(), element, object);
        chain.next();

        parseChildren(element, object);

        return object;
    }

    private void parseChildren(Element element, SceneObject object) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                SceneObject child = parse((Element) childNode);
                if (child != null) {
                    object.addChildObject(child);
                }
            }
        }
    }

    /**
     * Create {@link SceneObject} from {@link Element}.
     * If an {@link Element} is not for a {@link SceneObject}, return {@code null}.
     *
     * @param element Element
     * @return Created SceneObject or null.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    @Nullable
    private SceneObject createSceneObject(Element element) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        final String className = element.getAttribute("class");
        if (!isEmpty(className)) {
            return (SceneObject) Class.forName(className).newInstance();
        }

        final String tagName = element.getTagName();

        switch (tagName) {
            case "object":
                return new SceneObject();
            case "globe":
                return new GlobeSceneObject();
            case "scene":
                return new Scene();
        }

        return null;
    }

    public static class ParserChain {
        private final Iterator<AttributeParser> it;
        private final Element element;
        private final SceneObject object;
        private final Context context;

        public ParserChain(Context context, Iterator<AttributeParser> it, Element element, SceneObject object) {
            this.it = it;
            this.element = element;
            this.object = object;
            this.context = context.getApplicationContext();
        }

        public void next() {
            if (it.hasNext()) {
                AttributeParser parser = it.next();
                parser.parse(context, element, object, this);
            }
        }
    }

    /*
     * Attribute parsers
     */

    public interface AttributeParser {
        void parse(Context context, Element element, SceneObject object, ParserChain chain);
    }


    private static class IdParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {
            String id = element.getAttribute("id");

            if (isEmpty(id)) {
                chain.next();
                return;
            }

            if (id.startsWith("@+id")) {
                int res = context.getResources().getIdentifier(id.substring(2), "id", context.getPackageName());
                object.setId(res);
            } else if (id.startsWith("@id")) {
                int res = context.getResources().getIdentifier(id.substring(1), "id", context.getPackageName());
                object.setId(res);
            } else {
                object.setId(id.hashCode());
            }

            chain.next();
        }
    }

    private static class PositionParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            String positionAttr = element.getAttribute("position");
            String x = element.getAttribute("x");
            String y = element.getAttribute("y");
            String z = element.getAttribute("z");

            if (isEmpty(positionAttr) && isEmpty(x) && isEmpty(y) && isEmpty(z)) {
                chain.next();
                return;
            }

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

            chain.next();
        }
    }

    private static class ScaleParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            String scaleAttr = element.getAttribute("scale");
            String x = element.getAttribute("scaleX");
            String y = element.getAttribute("scaleY");
            String z = element.getAttribute("scaleZ");

            if (isEmpty(scaleAttr) && isEmpty(x) && isEmpty(y) && isEmpty(z)) {
                chain.next();
                return;
            }

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

            chain.next();
        }
    }

    private static class WidthHeightParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            String width = element.getAttribute("width");
            String height = element.getAttribute("height");

            if (isEmpty(width) && isEmpty(height)) {
                chain.next();
                return;
            }

            final float w = Float.parseFloat(width);
            final float h = Float.parseFloat(height);
            object.mesh(Mesh.createQuad(w, h));

            chain.next();
        }
    }

    private static class RotationParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            String rotationAttr = element.getAttribute("rotation");
            String quaternionAttr = element.getAttribute("quaternion");
            String rotationXYZAttr = element.getAttribute("rotationXYZ");
            String rotationYXZAttr = element.getAttribute("rotationYXZ");

            if (isEmpty(rotationAttr) && isEmpty(quaternionAttr) && isEmpty(rotationXYZAttr) && isEmpty(rotationYXZAttr)) {
                chain.next();
                return;
            }

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

            chain.next();
        }
    }

    private static class TextureParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            String texture = element.getAttribute("texture");

            if (isEmpty(texture)) {
                chain.next();
                return;
            }

            if (texture.startsWith("@drawable") || texture.startsWith("@mipmap")) {

                int res = context.getResources().getIdentifier(texture.substring(1), "drawable", context.getPackageName());
                Drawable drawable = ContextCompat.getDrawable(context, res);
                Material material = Material.from(drawable);
                object.material(material);

                if (object.mesh() == null) {
                    object.mesh(Mesh.from(drawable));
                }

            } else if (texture.startsWith("@layout")) {

                int res = context.getResources().getIdentifier(texture.substring(1), "layout", context.getPackageName());
                View view = LayoutInflater.from(context).inflate(res, null);
                object.material(Material.from(view));

                if (object.mesh() == null) {
                    object.mesh(Mesh.from(view));
                }
            }

            chain.next();
        }
    }

    private static class OpacityParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            // process other parsers first
            chain.next();

            String opacity = element.getAttribute("opacity");
            if (!isEmpty(opacity)) {
                object.setOpacity(Float.parseFloat(opacity));
            }
        }
    }

    private static class VisibleParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            // process other parsers first
            chain.next();

            String visible = element.getAttribute("visible");
            if (!isEmpty(visible)) {
                object.setVisible(Boolean.parseBoolean(visible));
            }
        }
    }

    private static class RenderingOrderParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            // process other parsers first
            chain.next();

            String renderingOrder = element.getAttribute("renderingOrder");
            if (isEmpty(renderingOrder)) return;

            final RenderData renderData = object.getRenderData();
            if (renderData == null) return;

            renderData.setRenderingOrder(Integer.parseInt(renderingOrder));
        }
    }

    private static class ScriptParser implements AttributeParser {

        @Override
        public void parse(Context context, Element element, SceneObject object, ParserChain chain) {

            // process other parsers first
            chain.next();

            for (String eventName : EVENT_NAMES) {
                String attr = element.getAttribute("on" + eventName);
                if (!isEmpty(attr)) {
                    object.on(eventName, JS.createEventHandler(object, attr));
                }
            }
        }
    }
}
