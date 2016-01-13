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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Xml;

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.FutureWrapper;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.RenderData.RenderingOrder;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.Transform;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.scene_objects.CanvasSceneObject;
import com.eje_c.meganekko.scene_objects.ConeSceneObject;
import com.eje_c.meganekko.scene_objects.CubeSceneObject;
import com.eje_c.meganekko.scene_objects.CylinderSceneObject;
import com.eje_c.meganekko.scene_objects.SphereSceneObject;
import com.eje_c.meganekko.scene_objects.VideoSceneObject;
import com.eje_c.meganekko.scene_objects.ViewSceneObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Future;

/**
 * Internally used from {@link XmlSceneParser}. This creates {@link SceneObject} and set properties from XML attributes.
 */
public class XmlSceneObjectParser {

    public static final float DEFAULT_TEXT_SIZE = 30.0f;
    private final VrContext mContext;

    public XmlSceneObjectParser(VrContext context) {
        this.mContext = context;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = null;

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();

            if (width <= 0 || height <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }
    }

    private static void parsePosition(Transform transform, String value) {

        String[] arr = value.split("\\s+");

        if (arr.length == 3) {
            float x = Float.parseFloat(arr[0]);
            float y = Float.parseFloat(arr[1]);
            float z = Float.parseFloat(arr[2]);
            transform.setPosition(x, y, z);
        }
    }

    private static void parseScale(Transform transform, String value) {

        String[] arr = value.split("\\s+");

        if (arr.length == 3) {
            float x = Float.parseFloat(arr[0]);
            float y = Float.parseFloat(arr[1]);
            float z = Float.parseFloat(arr[2]);
            transform.setScale(x, y, z);
        } else if (arr.length == 1) {
            float val = Float.parseFloat(arr[0]);
            transform.setScale(val, val, val);
        }
    }

    private static void parseRotationAngleAxis(Transform transform, String value) {

        String[] arr = value.split("\\s+");

        if (arr.length == 4) {
            float angle = Float.parseFloat(arr[0]);
            float axisX = Float.parseFloat(arr[1]);
            float axisY = Float.parseFloat(arr[2]);
            float axisZ = Float.parseFloat(arr[3]);
            transform.setRotationByAxis(angle, axisX, axisY, axisZ);
        }
    }

    private static void parseCanvasSize(CanvasSceneObject canvasSceneObject, String value) {

        String[] arr = value.split("\\s+");

        if (arr.length == 2) {
            int width = Integer.parseInt(arr[0]);
            int height = Integer.parseInt(arr[1]);
            canvasSceneObject.setCanvasSize(width, height);
        }
    }

    private static Bitmap textAsBitmap(String text, float textSize, int textColor, Align align) {

        Paint paint = new Paint();

        paint.setTextAlign(align);
        paint.setTextSize(textSize);
        paint.setColor(textColor);

        int width = 1;
        int height = 1;
        String[] lines = text.split("\n");
        int lineCount = lines.length;
        int[] heights = new int[lineCount];
        float[] baselines = new float[lineCount];

        for (int i = 0; i < lineCount; i++) {

            int w = (int) (paint.measureText(lines[i]) + 0.5f);// round
            width = Math.max(w, width);

            baselines[i] = (int) (-paint.ascent() + 0.5f);// ascent() is
            // negative
            int h = (int) (baselines[i] + paint.descent() + 0.5f);

            height += h;
            heights[i] = h;
        }

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        float x = 0.0f;

        if (align == Paint.Align.CENTER) {
            x = width * 0.5f;
        } else if (align == Paint.Align.RIGHT) {
            x = width;
        }

        float y = 0;
        for (int i = 0; i < lineCount; i++) {
            String line = lines[i];

            canvas.drawText(line, x, y + baselines[i], paint);
            y += heights[i];
        }

        return image;
    }

    public SceneObject parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        return parse(parser, false);
    }

    public SceneObject parse(XmlPullParser parser, boolean useAsyncLoading) throws XmlPullParserException, IOException {

        // Skip until start tag appears
        while (parser.getEventType() != XmlPullParser.START_TAG) {
            parser.next();
            if (parser.getEventType() == XmlPullParser.END_DOCUMENT)
                throw new XmlPullParserException("Unexpected END_DOCUMENT");
        }

        SceneObject object = createSceneObject(parser);

        if (object == null)
            return null;

        Paint.Align textAlign = Paint.Align.LEFT;
        float textSize = DEFAULT_TEXT_SIZE;
        float width = -1;
        float height = -1;
        float opacity = -1;
        boolean visible = object.isVisible();
        int renderingOrder = -1;
        int color = -1;
        Future<Mesh> mesh = null;

        // Assign mesh if exists
        RenderData renderData = object.getRenderData();
        if (renderData != null) {
            Mesh m = renderData.getMesh();
            if (m != null) {
                mesh = new FutureWrapper<>(m);
            }
        }

        // Read attributes
        AttributeSet attributeSet = Xml.asAttributeSet(parser);
        for (int i = 0; i < attributeSet.getAttributeCount(); ++i) {

            switch (attributeSet.getAttributeName(i)) {

                case "id":
                    object.setId(attributeSet.getIdAttributeResourceValue(0));
                    break;

                case "name":
                    object.setName(attributeSet.getAttributeValue(i));
                    break;

                case "visible":
                    visible = attributeSet.getAttributeBooleanValue(i, visible);
                    break;

                case "opacity":
                    opacity = Float.parseFloat(parser.getAttributeValue(i));
                    break;

                case "position":
                    parsePosition(object.getTransform(), attributeSet.getAttributeValue(i));
                    break;

                case "scale":
                    parseScale(object.getTransform(), attributeSet.getAttributeValue(i));
                    break;

                case "rotation":
                    parseRotationAngleAxis(object.getTransform(), attributeSet.getAttributeValue(i));
                    break;

                case "width":
                    width = Float.parseFloat(parser.getAttributeValue(i));
                    break;

                case "height":
                    height = Float.parseFloat(parser.getAttributeValue(i));
                    break;

                case "renderingOrder":
                    renderingOrder = attributeSet.getAttributeIntValue(i, renderingOrder);
                    break;

                case "mesh":
                    if (useAsyncLoading) {
                        mesh = parseMesh(attributeSet.getAttributeValue(i));
                    } else {
                        mesh = new FutureWrapper<>(parseMeshSync(attributeSet.getAttributeValue(i)));
                    }
                    break;

                case "color":
                    String colorVal = attributeSet.getAttributeValue(i);
                    if (colorVal.startsWith("@color")) {
                        int colorRes = attributeSet.getAttributeResourceValue(i, -1);
                        if (colorRes != -1) {
                            color = ContextCompat.getColor(mContext.getContext(), colorRes);
                        }
                    } else {
                        color = Color.parseColor(colorVal);
                    }
                    break;

                // Simple position
                case "x":
                    object.getTransform().setPositionX(Float.parseFloat(parser.getAttributeValue(i)));
                    break;

                case "y":
                    object.getTransform().setPositionY(Float.parseFloat(parser.getAttributeValue(i)));
                    break;

                case "z":
                    object.getTransform().setPositionZ(Float.parseFloat(parser.getAttributeValue(i)));
                    break;

                // Simple scale
                case "scaleX":
                    object.getTransform().setScaleX(Float.parseFloat(parser.getAttributeValue(i)));
                    break;

                case "scaleY":
                    object.getTransform().setScaleY(Float.parseFloat(parser.getAttributeValue(i)));
                    break;

                case "scaleZ":
                    object.getTransform().setScaleZ(Float.parseFloat(parser.getAttributeValue(i)));
                    break;

                // CanvasSceneObject
                case "canvasSize":
                    if (object instanceof CanvasSceneObject) {
                        parseCanvasSize((CanvasSceneObject) object, attributeSet.getAttributeValue(i));
                    }
                    break;

                // ViewSceneObject
                case "layout":
                    if (object instanceof ViewSceneObject) {
                        int layoutRes = attributeSet.getAttributeResourceValue(i, -1);
                        if (layoutRes != -1) {
                            ((ViewSceneObject) object).setView(layoutRes);
                        }
                    }
                    break;
            }
        }

        // Parse text node (deprecated)
        if (parser.next() == XmlPullParser.TEXT) {
            String text = parser.getText();
            Bitmap bitmap = textAsBitmap(text, textSize, color, textAlign);

            // Auto size
            if (width < 0.0f && height < 0.0f) {
                width = bitmap.getWidth() * 0.01f;
                height = bitmap.getHeight() * 0.01f;
            } else {
                float aspect = (float) bitmap.getWidth() / (float) bitmap.getHeight();

                if (width < 0.0f) {
                    width = height * aspect;
                } else if (height < 0.0f) {
                    height = width / aspect;
                }
            }

            // Set renderingOrder to TRANSPARENT if not specified
            if (renderingOrder < 0) {
                renderingOrder = RenderingOrder.TRANSPARENT;
            }
        }

        // Create quad mesh if needed
        if (mesh == null && width >= 0.0f && height >= 0.0f) {
            mesh = new FutureWrapper<>(mContext.createQuad(width, height));
        }

        // Set mesh if needed
        if (mesh != null) {
            ensureHavingRenderData(object).getRenderData().setMesh(mesh);
        }

        // Apply renderingOrder
        if (renderingOrder >= 0 && object.getRenderData() != null) {
            object.getRenderData().setRenderingOrder(renderingOrder);
        }

        // Parse children
        while (parser.getEventType() != XmlPullParser.END_TAG) {

            if (parser.getEventType() == XmlPullParser.START_TAG) {
                SceneObject child = parse(parser, useAsyncLoading);
                if (child != null) {
                    object.addChildObject(child);
                }
            }

            parser.next();
        }

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
                        return new SceneObject(mContext);
                    } else {
                        return ObjectFactory.newInstance(className, mContext);
                    }
                case "cube":
                    return new CubeSceneObject(mContext);
                case "sphere":
                    return new SphereSceneObject(mContext);
                case "cone":
                    return new ConeSceneObject(mContext);
                case "cylinder":
                    return new CylinderSceneObject(mContext);
                case "video":
                    return new VideoSceneObject(mContext);
                case "canvas":
                    return new CanvasSceneObject(mContext);
                case "view":
                    return new ViewSceneObject(mContext);
                default:
                    return ObjectFactory.newInstance(name, mContext);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot instantiate " + name, e);
        }
    }

    private SceneObject ensureHavingRenderData(SceneObject object) {
        if (object.getRenderData() == null) {
            object.attachRenderData(new RenderData(mContext));
        }
        return object;
    }

    private Future<Mesh> parseMesh(String value) throws IOException {
        return mContext.loadFutureMesh(new AndroidResource(mContext, value));
    }

    private Mesh parseMeshSync(String value) throws IOException {
        return mContext.loadMesh(new AndroidResource(mContext, value));
    }

    @Deprecated
    public void setAsyncTextureLoading(boolean useAsyncLoading) {
        // NOOP
    }
}
