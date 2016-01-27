/* Copyright 2015 Samsung Electronics Co., LTD
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

package com.eje_c.meganekko;

import android.graphics.Color;
import android.graphics.SurfaceTexture;

import com.eje_c.meganekko.utility.Colors;

/**
 * This is one of the key Meganekko classes: it holds shaders with textures.
 * <p/>
 * You can have invisible {@linkplain SceneObject scene objects:} these have
 * a location and a set of child objects. This can be useful, to move a set of
 * scene objects as a unit, preserving their relative geometry. Invisible scene
 * objects don't need any {@linkplain SceneObject#getRenderData() render
 * data.}
 * <p/>
 * <p/>
 * Visible scene objects must have render data
 * {@linkplain SceneObject#attachRenderData(RenderData) attached.} Each
 * {@link RenderData} has a {@link Mesh GL mesh} that defines its
 * geometry, and a {@link Material} that defines its surface.
 * <p/>
 * <p/>
 * Each {@link Material} contains two main things:
 * <ul>
 * <li>The id of a (stock or custom) shader, which is used to draw the mesh. See
 * {@link ShaderType} and {@link VrContext#getMaterialShaderManager()}.
 * <p/>
 * <li>Data to pass to the shader. This usually - but not always - means a
 * {@link Texture} and can include other named values to pass to the shader.
 * </ul>
 * <p/>
 * <p/>
 * The simplest way to create a {@link Material} is to call the
 * {@linkplain Material#Material(VrContext) constructor that takes only a
 * GVRContext.} Then you just {@link Material#setMainTexture(Texture)
 * setMainTexture()} and you're ready to draw with the default shader, which is
 * called 'unlit' because it simply drapes the texture over the mesh, without
 * any lighting or reflection effects.
 * <p/>
 * <pre>
 * // for example
 * GVRMaterial material = new GVRMaterial(vrContext);
 * material.setMainTexture(texture);
 * </pre>
 */
public class Material extends HybridObject {


    private static native float getFloat(long material, String key);

    private static native void setFloat(long material, String key, float value);

    private static native float[] getVec2(long material, String key);

    private static native void setVec2(long material, String key, float x, float y);

    private static native float[] getVec3(long material, String key);

    private static native void setVec3(long material, String key, float x, float y, float z);

    private static native float[] getVec4(long material, String key);

    private static native void setVec4(long material, String key, float x, float y, float z, float w);

    private static native void setMat4(long material, String key, float x1, float y1,
                                       float z1, float w1, float x2, float y2, float z2, float w2,
                                       float x3, float y3, float z3, float w3, float x4, float y4,
                                       float z4, float w4);

    private static native void setStereoMode(long material, int stereoMode);

    @Override
    protected native long initNativeInstance();

    /**
     * Get the {@code color} uniform.
     * <p/>
     * By convention, Meganekko shaders can use a {@code vec3} uniform named
     * {@code color}. With the default {@linkplain ShaderType.Unlit 'unlit'
     * shader,} this allows you to add an overlay color on top of the texture.
     *
     * @return The current {@code vec3 color} as a three-element array
     */
    public float[] getColor() {
        return getVec3(getNative(), "color");
    }

    /**
     * A convenience overload of {@link #setColor(float, float, float)} that
     * lets you use familiar Android {@link Color} values.
     *
     * @param color Any Android {@link Color}; the alpha byte is ignored.
     */
    public void setColor(int color) {
        setColor(Colors.byteToGl(Color.red(color)), //
                Colors.byteToGl(Color.green(color)), //
                Colors.byteToGl(Color.blue(color)));
    }

    /**
     * A convenience method that wraps {@link #getColor()} and returns an
     * Android {@link Color}
     *
     * @return An Android {@link Color}
     */
    public int getRgbColor() {
        return Colors.toColor(getColor());
    }

    /**
     * Set the {@code color} uniform.
     * <p/>
     * By convention, Meganekko shaders can use a {@code vec3} uniform named
     * {@code color}. With the default {@linkplain ShaderType.Unlit 'unlit'
     * shader,} this allows you to add an overlay color on top of the texture.
     * Values are between {@code 0.0f} and {@code 1.0f}, inclusive.
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     */
    public void setColor(float r, float g, float b) {
        setVec3(getNative(), "color", r, g, b);
    }

    /**
     * Get the opacity.
     * <p/>
     * This method returns the {@code opacity} uniform.
     * <p/>
     * The {@linkplain #setOpacity(float) setOpacity() documentation} explains
     * what the {@code opacity} uniform does.
     *
     * @return The {@code opacity} uniform used to render this material
     */
    public float getOpacity() {
        return getFloat(getNative(), "opacity");
    }

    /**
     * Set the opacity, in a complicated way.
     * <p/>
     * There are two things you need to know, how opacity is applied, and how
     * opacity is implemented.
     * <p/>
     * <p/>
     * First, Meganekko does not sort by distance every object it can see, then draw
     * from back to front. Rather, it sorts every object by
     * {@linkplain RenderData#getRenderingOrder() render order,} then draws
     * the {@linkplain Scene scene graph} in traversal order. So, if you want
     * to see a scene object through another scene object, you have to
     * explicitly {@linkplain RenderData#setRenderingOrder(int) set the
     * rendering order} so that the translucent object draws after the opaque
     * object. You can use any integer values you like, but Meganekko supplies
     * {@linkplain RenderData.RenderingOrder four standard values;} the
     * {@linkplain RenderData#getRenderingOrder() default value} is
     * {@linkplain RenderData.RenderingOrder#GEOMETRY GEOMETRY.}
     * <p/>
     * <p/>
     * Second, technically all this method does is set the {@code opacity}
     * uniform. What this does depends on the actual shader. If you don't
     * specify a shader (or you specify the
     * {@linkplain Material.ShaderType.Unlit#ID unlit} shader) setting
     * {@code opacity} does exactly what you expect; you only have to worry
     * about the render order. However, it is totally up to a custom shader
     * whether or how it will handle opacity.
     *
     * @param opacity Value between {@code 0.0f} and {@code 1.0f}, inclusive.
     */
    public void setOpacity(float opacity) {
        setFloat(getNative(), "opacity", opacity);
    }

    public SurfaceTexture getSurfaceTexture() {
        return getSurfaceTexture(getNative());
    }

    private native SurfaceTexture getSurfaceTexture(long nativePtr);

    public void setStereoMode(StereoMode stereoMode) {
        setStereoMode(getNative(), stereoMode.ordinal());
    }

    public enum StereoMode {
        NORMAL, TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT
    }
}
