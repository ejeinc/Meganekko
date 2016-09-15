package org.meganekkovr.xml;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import org.meganekkovr.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * To add custom attribute, use {@code XmlAttributeParser.getInstance().install(new YourAttrHandler())}.
 */
public class XmlAttributeParser {

    static {
        // Install default attribute handlers
        XmlAttributeParser parser = XmlAttributeParser.getInstance();
        parser.install(new GeometryHandler());
        parser.install(new IdHandler());
        parser.install(new OpacityHandler());
        parser.install(new PositionHandler());
        parser.install(new RotationHandler());
        parser.install(new ScaleHandler());
        parser.install(new SurfaceHandler());
        parser.install(new VisibleHandler());
    }

    public interface XmlAttributeHandler {

        /**
         * @return Attribute name.
         */
        String attributeName();

        /**
         * Apply XML attribute to entity.
         *
         * @param entity   Entity
         * @param rawValue Attribute value
         * @param context  Context
         */
        void parse(Entity entity, String rawValue, Context context);
    }

    // singleton
    private static XmlAttributeParser instance;

    public static synchronized XmlAttributeParser getInstance() {
        if (instance == null) {
            instance = new XmlAttributeParser();
        }
        return instance;
    }

    private final Map<String, XmlAttributeHandler> handlers = new ArrayMap<>();

    private XmlAttributeParser() {
    }

    void parse(Entity entity, Node node, Context context) {

        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0, len = attrs.getLength(); i < len; ++i) {

            Node attr = attrs.item(i);

            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();

            // Skip unknown attribute
            XmlAttributeHandler attributeHandler = handlers.get(attrName);
            if (attributeHandler == null) continue;

            attributeHandler.parse(entity, attrValue, context);
        }
    }

    /**
     * Add custom {@link XmlAttributeHandler}.
     *
     * @param handler Custom attribute handler.
     */
    public void install(@NonNull XmlAttributeHandler handler) {
        handlers.put(handler.attributeName(), handler);
    }

    /**
     * Convert inline CSS like string {@code attributeName: attributeValue; ...} to Map.
     * <pre>
     * Map : {
     *   prop1: value1,
     *   prop2: value2,
     *   ...
     * }
     * </pre>
     *
     * @param inlineValue Inline value
     * @return Property : Value map.
     */
    @NonNull
    public static Map<String, String> parseInlineValue(@NonNull String inlineValue) {
        Map<String, String> map = new ArrayMap<>();

        // "prop1: value1; prop2: value2"
        // to
        // ["prop1: value1", " prop2: value2"]
        String[] propValues = inlineValue.split(";");

        for (String propValue : propValues) {

            // ["prop1: value1", " prop2: value2"]
            // to
            // [["prop1", " value1"], [" prop2", " value2"]]
            String[] strs = propValue.split(":", 2);
            if (strs.length == 2) {

                // [["prop1", " value1"], [" prop2", " value2"]]
                // to
                // [["prop1", "value1"], ["prop2", "value2"]]
                String name = strs[0].trim();
                String value = strs[1].trim();

                map.put(name, value);
            }
        }

        return map;
    }

    /**
     * @param str string
     * @return {@code true} if str represents raw resource.
     */
    public static boolean isRawResource(String str) {
        return str.startsWith("@raw/");
    }

    /**
     * @param str string
     * @return {@code true} if str represents layout resource.
     */
    public static boolean isLayoutResource(String str) {
        return str.startsWith("@layout/");
    }

    /**
     * @param str string
     * @return {@code true} if str represents drawable resource.
     */
    public static boolean isDrawableResource(String str) {
        return str.startsWith("@drawable/") || str.startsWith("@color/") || str.startsWith("@mipmap/");
    }

    /**
     * @param str string
     * @return {@code true} if str represents id resource.
     */
    public static boolean isIdResource(String str) {
        return str.matches("^@\\+?id/.+$");
    }

    /**
     * @param str     string
     * @param context Android context
     * @return Resurce ID or {@code 0} if str don't represent resource.
     */
    public static int toResourceId(String str, Context context) {

        Pattern pattern = Pattern.compile("@\\+?(.+)/(.+)");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            String name = matcher.group(2);
            String defType = matcher.group(1);
            return context.getResources().getIdentifier(name, defType, context.getPackageName());
        }

        return 0;
    }
}
