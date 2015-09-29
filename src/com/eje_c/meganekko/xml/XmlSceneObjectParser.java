/* Copyright 2015 eje inc.
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.Future;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.eje_c.meganekko.FutureWrapper;
import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.BitmapTexture;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.Texture;
import com.eje_c.meganekko.Transform;
import com.eje_c.meganekko.RenderData.GVRRenderingOrder;
import com.eje_c.meganekko.scene_objects.ConeSceneObject;
import com.eje_c.meganekko.scene_objects.CubeSceneObject;
import com.eje_c.meganekko.scene_objects.CylinderSceneObject;
import com.eje_c.meganekko.scene_objects.SphereSceneObject;
import com.eje_c.meganekko.scene_objects.TextSceneObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Xml;

public class XmlSceneObjectParser {

    public static final float DEFAULT_TEXT_SIZE = 30.0f;
    private final VrContext context;
    private boolean useAsyncLoading = true;

    public XmlSceneObjectParser(VrContext context) {
        this.context = context;
    }

    public SceneObject parse(XmlPullParser parser) throws XmlPullParserException, IOException {

        // Skip until start tag appears
        while (parser.getEventType() != XmlPullParser.START_TAG) {
            parser.next();
            if (parser.getEventType() == XmlPullParser.END_DOCUMENT)
                throw new XmlPullParserException("Unexpected END_DOCUMENT");
        }

        SceneObject object = createSceneObject(parser.getName());

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
        Future<Texture> texture = null;

        // Assign mesh if exists
        RenderData renderData = object.getRenderData();
        if (renderData != null) {
            mesh = new FutureWrapper<Mesh>(renderData.getMesh());
        }

        // Read attributes
        AttributeSet attributeSet = Xml.asAttributeSet(parser);
        for (int i = 0; i < attributeSet.getAttributeCount(); ++i) {

            switch (attributeSet.getAttributeName(i)) {

            case "name":
                object.setName(attributeSet.getAttributeValue(i));
                break;

            case "visible":
                visible = attributeSet.getAttributeBooleanValue(i, visible);
                break;

            case "opacity":
                opacity = attributeSet.getAttributeFloatValue(i, opacity);
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
                width = attributeSet.getAttributeFloatValue(i, width);
                break;

            case "height":
                height = attributeSet.getAttributeFloatValue(i, height);
                break;

            case "renderingOrder":
                renderingOrder = attributeSet.getAttributeIntValue(i, renderingOrder);
                break;

            case "mesh":
                if (useAsyncLoading) {
                    mesh = parseMesh(attributeSet.getAttributeValue(i));
                } else {
                    mesh = new FutureWrapper<Mesh>(parseMeshSync(attributeSet.getAttributeValue(i)));
                }
                break;

            case "texture":
                if (useAsyncLoading) {
                    texture = parseTexture(attributeSet.getAttributeValue(i));
                } else {
                    texture = new FutureWrapper<Texture>(parseTextureSync(attributeSet.getAttributeValue(i)));
                }
                break;

            case "text":
                if (object instanceof TextSceneObject) {
                    ((TextSceneObject) object).setText(attributeSet.getAttributeValue(i));
                }
                break;

            case "textColor":
                if (object instanceof TextSceneObject) {
                    int textColor = Color.parseColor(attributeSet.getAttributeValue(i));
                    ((TextSceneObject) object).setColor(textColor);
                } else {
                    color = attributeSet.getAttributeIntValue(i, color);
                }
                break;

            case "textSize":
                if (object instanceof TextSceneObject) {
                    ((TextSceneObject) object).setTextSize(attributeSet.getAttributeFloatValue(i, 0));
                } else {
                    textSize = attributeSet.getAttributeFloatValue(i, textSize);
                }
                break;

            case "textScale":
                if (object instanceof TextSceneObject) {
                    ((TextSceneObject) object).setTextScale(attributeSet.getAttributeFloatValue(i, 0));
                }
                break;

            case "textAlign":
                textAlign = Paint.Align.valueOf(attributeSet.getAttributeValue(i).toUpperCase(Locale.ENGLISH));
                break;

            case "drawable":
                int res = attributeSet.getAttributeResourceValue(i, -1);
                if (res != -1) {
                    Texture t = parseDrawableAsTexture(res);
                    if (t != null) {
                        texture = new FutureWrapper<Texture>(t);
                    }
                }
                break;

            case "color":
                String colorVal = attributeSet.getAttributeValue(i);
                if (colorVal.startsWith("@color")) {
                    int colorRes = attributeSet.getAttributeResourceValue(i, -1);
                    if (colorRes != -1) {
                        color = getColorResourceValue(colorRes);
                    }
                } else {
                    color = Color.parseColor(colorVal);
                }
                break;

            // Simple position
            case "x":
                object.getTransform().setPositionX(attributeSet.getAttributeFloatValue(i, 0.0f));
                break;

            case "y":
                object.getTransform().setPositionY(attributeSet.getAttributeFloatValue(i, 0.0f));
                break;

            case "z":
                object.getTransform().setPositionZ(attributeSet.getAttributeFloatValue(i, 0.0f));
                break;

            // Simple scale
            case "scaleX":
                object.getTransform().setScaleX(attributeSet.getAttributeFloatValue(i, 1.0f));
                break;

            case "scaleY":
                object.getTransform().setScaleY(attributeSet.getAttributeFloatValue(i, 1.0f));
                break;

            case "scaleZ":
                object.getTransform().setScaleZ(attributeSet.getAttributeFloatValue(i, 1.0f));
                break;
            }
        }

        // Parse text node
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
                renderingOrder = GVRRenderingOrder.TRANSPARENT;
            }

            texture = new FutureWrapper<Texture>(new BitmapTexture(context, bitmap));
        }

        // Create quad mesh if needed
        if (mesh == null && width >= 0.0f && height >= 0.0f) {
            mesh = new FutureWrapper<Mesh>(context.createQuad(width, height));
        }

        // Set mesh if needed
        if (mesh != null) {
            ensureHavingRenderData(object).getRenderData().setMesh(mesh);
        }

        // Set texture
        if (texture != null) {
            Material material = new Material(context);
            material.setMainTexture(texture);

            if (color != -1) {
                material.setColor(color);
            }

            ensureHavingRenderData(object).getRenderData().setMaterial(material);
        }

        // Apply renderingOrder
        if (renderingOrder >= 0 && object.getRenderData() != null) {
            object.getRenderData().setRenderingOrder(renderingOrder);
        }

        // Parse children
        while (parser.getEventType() != XmlPullParser.END_TAG) {

            if (parser.getEventType() == XmlPullParser.START_TAG) {
                SceneObject child = parse(parser);
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

    @SuppressLint("NewApi")
    private int getColorResourceValue(int colorRes) {
        return Build.VERSION.SDK_INT >= 23
                ? context.getContext().getColor(colorRes)
                : context.getContext().getResources().getColor(colorRes, context.getActivity().getTheme());
    }

    @SuppressLint("NewApi")
    private Texture parseDrawableAsTexture(int res) {
        Drawable drawable = Build.VERSION.SDK_INT >= 21
                ? context.getContext().getDrawable(res)
                : context.getContext().getResources().getDrawable(res, context.getActivity().getTheme());
        Bitmap bitmap = drawableToBitmap(drawable);
        return new BitmapTexture(context, bitmap);
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

    private SceneObject createSceneObject(String name) {
        switch (name) {

        case "object":
            return new SceneObject(context);
        case "cube":
            return new CubeSceneObject(context);
        case "sphere":
            return new SphereSceneObject(context);
        case "cone":
            return new ConeSceneObject(context);
        case "cylinder":
            return new CylinderSceneObject(context);
        case "text":
            return new TextSceneObject(context);

        default:

            try {
                Class<?> clazz = Class.forName(name);

                if (SceneObject.class.isAssignableFrom(clazz)) {
                    return (SceneObject) clazz.getConstructor(VrContext.class).newInstance(context);
                }

            } catch (ClassNotFoundException e) {
                //                e.printStackTrace();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            return null;
        }
    }

    private SceneObject ensureHavingRenderData(SceneObject object) {
        if (object.getRenderData() == null) {
            object.attachRenderData(new RenderData(context));
        }
        return object;
    }

    private Future<Mesh> parseMesh(String value) throws IOException {
        return context.loadFutureMesh(new AndroidResource(context, value));
    }

    private Mesh parseMeshSync(String value) throws IOException {
        return context.loadMesh(new AndroidResource(context, value));
    }

    private Future<Texture> parseTexture(String value) throws IOException {
        return context.loadFutureTexture(new AndroidResource(context, value));
    }

    private Texture parseTextureSync(String value) throws IOException {
        return context.loadTexture(new AndroidResource(context, value));
    }

    public void setAsyncTextureLoading(boolean useAsyncLoading) {
        this.useAsyncLoading = useAsyncLoading;
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
}