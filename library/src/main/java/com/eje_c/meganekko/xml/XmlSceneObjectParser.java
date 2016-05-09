/*
 * Copyright 2015 eje inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eje_c.meganekko.xml;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Xml;

import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.scene_objects.ConeSceneObject;
import com.eje_c.meganekko.scene_objects.CubeSceneObject;
import com.eje_c.meganekko.scene_objects.CylinderSceneObject;
import com.eje_c.meganekko.scene_objects.SphereSceneObject;
import com.eje_c.meganekko.xml.attribute_parser.BasicParser;
import com.eje_c.meganekko.xml.attribute_parser.DrawableParser;
import com.eje_c.meganekko.xml.attribute_parser.MeshParser;
import com.eje_c.meganekko.xml.attribute_parser.PositionParser;
import com.eje_c.meganekko.xml.attribute_parser.RotationParser;
import com.eje_c.meganekko.xml.attribute_parser.ScaleParser;
import com.eje_c.meganekko.xml.attribute_parser.ViewParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Internally used from {@link XmlSceneParser}. This creates {@link SceneObject} and set properties from XML attributes.
 */
public class XmlSceneObjectParser {

    private static final List<Class<? extends XmlAttributeParser>> sAttributeParsers = new ArrayList<>();
    private final Context mContext;
    private final List<XmlAttributeParser> mAttributeParsers = new ArrayList<>();

    static {
        sAttributeParsers.addAll(Arrays.asList(
                BasicParser.class,
                PositionParser.class,
                ScaleParser.class,
                RotationParser.class,
                MeshParser.class,
                ViewParser.class,
                DrawableParser.class
        ));
    }

    public static void installAttributeParser(Class<? extends XmlAttributeParser> attributeParserClass) {
        sAttributeParsers.add(attributeParserClass);
    }

    public XmlSceneObjectParser(Context context) {
        this.mContext = context;

        for (Class<? extends XmlAttributeParser> clazz : sAttributeParsers) {
            try {
                mAttributeParsers.add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public SceneObject parse(XmlPullParser parser) throws XmlPullParserException, IOException {

        // Skip until start tag appears
        while (parser.getEventType() != XmlPullParser.START_TAG) {
            parser.next();
            if (parser.getEventType() == XmlPullParser.END_DOCUMENT)
                throw new XmlPullParserException("Unexpected END_DOCUMENT");
        }

        SceneObject object = createSceneObject(parser);

        if (object == null)
            return null;

        float opacity = -1;
        boolean visible = object.isVisible();
        int renderingOrder = -1;

        // Parse attributes
        AttributeSet attributeSet = Xml.asAttributeSet(parser);
        for (XmlAttributeParser attributeParser : mAttributeParsers) {
            attributeParser.parse(mContext, object, attributeSet);
        }

        // TODO refactor
        for (int i = 0; i < attributeSet.getAttributeCount(); ++i) {

            switch (attributeSet.getAttributeName(i)) {

                case "visible":
                    visible = attributeSet.getAttributeBooleanValue(i, visible);
                    break;

                case "opacity":
                    opacity = Float.parseFloat(parser.getAttributeValue(i));
                    break;

                case "renderingOrder":
                    renderingOrder = attributeSet.getAttributeIntValue(i, renderingOrder);
                    break;
            }
        }

        // TODO refactor
        // Apply renderingOrder
        if (renderingOrder >= 0 && object.getRenderData() != null) {
            object.getRenderData().setRenderingOrder(renderingOrder);
        }

        // Parse children
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() == XmlPullParser.START_TAG) {
                SceneObject child = parse(parser);
                if (child != null) {
                    object.addChildObject(child);
                }
            }
        }

        // TODO refactor
        /*
         * These are propagated to children so must be called after children are
         * added.
         */

        // Apply opacity
        if (opacity >= 0.0f) {
            object.setOpacity(opacity);
        }

        // Apply visible
        object.setVisible(visible);

        return object;
    }

    private SceneObject createSceneObject(XmlPullParser parser) {

        String name = parser.getName();

        try {
            switch (name) {

                case "object":
                    final String className = parser.getAttributeValue(null, "class");
                    if (className == null) {
                        return new SceneObject();
                    } else {
                        return ObjectFactory.newInstance(className);
                    }
                case "cube":
                    return new CubeSceneObject();
                case "sphere":
                    return new SphereSceneObject();
                case "cone":
                    return new ConeSceneObject();
                case "cylinder":
                    return new CylinderSceneObject();
                default:
                    return ObjectFactory.newInstance(name);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate " + name, e);
        }
    }
}
