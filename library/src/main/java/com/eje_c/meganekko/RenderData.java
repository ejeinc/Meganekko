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

import com.eje_c.meganekko.utility.Threads;

import java.util.concurrent.Future;

import static android.opengl.GLES30.GL_LINES;
import static android.opengl.GLES30.GL_LINE_LOOP;
import static android.opengl.GLES30.GL_LINE_STRIP;
import static android.opengl.GLES30.GL_POINTS;
import static android.opengl.GLES30.GL_TRIANGLES;
import static android.opengl.GLES30.GL_TRIANGLE_FAN;
import static android.opengl.GLES30.GL_TRIANGLE_STRIP;

/**
 * One of the key Meganekko classes: Encapsulates the data associated with rendering
 * a mesh.
 * <p/>
 * This includes the {@link Mesh mesh} itself, the mesh's {@link Material
 * material}, camera association, rendering order, and various other parameters.
 */
public class RenderData extends Component {

    private static final String TAG = "Meganekko";
    private Mesh mMesh;
    private Material mMaterial;

    private static native void setMesh(long renderData, long mesh);

    private static native void setMaterial(long renderData, long material);

    private static native int getRenderMask(long renderData);

    private static native void setRenderMask(long renderData, int renderMask);

    private static native int getRenderingOrder(long renderData);

    private static native void setRenderingOrder(long renderData, int renderingOrder);

    private static native boolean getOffset(long renderData);

    private static native void setOffset(long renderData, boolean offset);

    private static native float getOffsetFactor(long renderData);

    private static native void setOffsetFactor(long renderData, float offsetFactor);

    private static native float getOffsetUnits(long renderData);

    private static native void setOffsetUnits(long renderData, float offsetUnits);

    private static native boolean getDepthTest(long renderData);

    private static native void setDepthTest(long renderData, boolean depthTest);

    private static native boolean getAlphaBlend(long renderData);

    private static native void setAlphaBlend(long renderData, boolean alphaBlend);

    private static native int getDrawMode(long renderData);

    private static native void setDrawMode(long renderData, int draw_mode);

    @Override
    protected native long initNativeInstance();

    @Override
    protected void delete() {

        long nativePtr = getNative();
        if (nativePtr != 0) {

            if (mMesh != null) {
                mMesh.delete();
                mMesh = null;
                setMesh(nativePtr, 0);
            }

            if (mMaterial != null) {
                mMaterial.delete();
                mMaterial = null;
                setMaterial(nativePtr, 0);
            }
        }

        super.delete();
    }

    /**
     * @return The {@link Mesh mesh} being rendered.
     */
    public Mesh getMesh() {
        return mMesh;
    }

    /**
     * Set the {@link Mesh mesh} to be rendered.
     *
     * @param mesh The mesh to be rendered.
     */
    public void setMesh(Mesh mesh) {
        synchronized (this) {
            mMesh = mesh;
        }
        setMesh(getNative(), mesh.getNative());
    }

    /**
     * Asynchronously set the {@link Mesh mesh} to be rendered.
     * <p/>
     * Uses a background thread from the thread pool to wait for the
     * {@code Future.get()} method; unless you are loading dozens of meshes
     * asynchronously, the extra overhead should be modest compared to the cost
     * of loading a mesh.
     *
     * @param mesh The mesh to be rendered.
     */
    public void setMesh(final Future<Mesh> mesh) {
        Threads.spawn(new Runnable() {

            @Override
            public void run() {
                try {
                    setMesh(mesh.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @return The {@link Material material} the {@link Mesh mesh} is
     * being rendered with.
     */
    public Material getMaterial() {
        return mMaterial;
    }

    /**
     * Set the {@link Material material} the mesh will be rendered with.
     *
     * @param material The {@link Material material} for rendering.
     */
    public void setMaterial(Material material) {
        this.mMaterial = material;
        setMaterial(getNative(), material.getNative());
    }

    /**
     * Get the rendering options bit mask.
     *
     * @return The rendering options bit mask.
     * @see RenderMaskBit
     */
    public int getRenderMask() {
        return getRenderMask(getNative());
    }

    /**
     * Set the rendering options bit mask.
     *
     * @param renderMask The rendering options bit mask.
     * @see RenderMaskBit
     */
    public void setRenderMask(int renderMask) {
        setRenderMask(getNative(), renderMask);
    }

    /**
     * @return The order in which this mesh will be rendered.
     * @see RenderingOrder
     */
    public int getRenderingOrder() {
        return getRenderingOrder(getNative());
    }

    /**
     * Set the order in which this mesh will be rendered.
     *
     * @param renderingOrder See {@link RenderingOrder}
     */
    public void setRenderingOrder(int renderingOrder) {
        setRenderingOrder(getNative(), renderingOrder);
    }

    /**
     * @return {@code true} if {@code GL_POLYGON_OFFSET_FILL} is enabled,
     * {@code false} if not.
     */
    public boolean getOffset() {
        return getOffset(getNative());
    }

    /**
     * Set the {@code GL_POLYGON_OFFSET_FILL} option
     *
     * @param offset {@code true} if {@code GL_POLYGON_OFFSET_FILL} should be
     *               enabled, {@code false} if not.
     */
    public void setOffset(boolean offset) {
        setOffset(getNative(), offset);
    }

    /**
     * @return The {@code factor} value passed to {@code glPolygonOffset()} if
     * {@code GL_POLYGON_OFFSET_FILL} is enabled.
     * @see #setOffset(boolean)
     */
    public float getOffsetFactor() {
        return getOffsetFactor(getNative());
    }

    /**
     * Set the {@code factor} value passed to {@code glPolygonOffset()} if
     * {@code GL_POLYGON_OFFSET_FILL} is enabled.
     *
     * @param offsetFactor Per OpenGL docs: Specifies a scale factor that is used to
     *                     create a variable depth offset for each polygon. The initial
     *                     value is 0.
     * @see #setOffset(boolean)
     */
    public void setOffsetFactor(float offsetFactor) {
        setOffsetFactor(getNative(), offsetFactor);
    }

    /**
     * @return The {@code units} value passed to {@code glPolygonOffset()} if
     * {@code GL_POLYGON_OFFSET_FILL} is enabled.
     * @see #setOffset(boolean)
     */
    public float getOffsetUnits() {
        return getOffsetUnits(getNative());
    }

    /**
     * Set the {@code units} value passed to {@code glPolygonOffset()} if
     * {@code GL_POLYGON_OFFSET_FILL} is enabled.
     *
     * @param offsetUnits Per OpenGL docs: Is multiplied by an implementation-specific
     *                    value to create a constant depth offset. The initial value is
     *                    0.
     * @see #setOffset(boolean)
     */
    public void setOffsetUnits(float offsetUnits) {
        setOffsetUnits(getNative(), offsetUnits);
    }

    /**
     * @return {@code true} if {@code GL_DEPTH_TEST} is enabled, {@code false}
     * if not.
     */
    public boolean getDepthTest() {
        return getDepthTest(getNative());
    }

    /**
     * Set the {@code GL_DEPTH_TEST} option
     *
     * @param depthTest {@code true} if {@code GL_DEPTH_TEST} should be enabled,
     *                  {@code false} if not.
     */
    public void setDepthTest(boolean depthTest) {
        setDepthTest(getNative(), depthTest);
    }

    /**
     * @return {@code true} if {@code GL_BLEND} is enabled, {@code false} if
     * not.
     */
    public boolean getAlphaBlend() {
        return getAlphaBlend(getNative());
    }

    /**
     * Set the {@code GL_BLEND} option
     *
     * @param alphaBlend {@code true} if {@code GL_BLEND} should be enabled,
     *                   {@code false} if not.
     */
    public void setAlphaBlend(boolean alphaBlend) {
        setAlphaBlend(getNative(), alphaBlend);
    }

    /**
     * @return The OpenGL draw mode (e.g. GL_TRIANGLES).
     */
    public int getDrawMode() {
        return getDrawMode(getNative());
    }

    /**
     * Set the draw mode for this mesh. Default is GL_TRIANGLES.
     *
     * @param drawMode
     */
    public void setDrawMode(int drawMode) {
        if (drawMode != GL_POINTS && drawMode != GL_LINES
                && drawMode != GL_LINE_STRIP && drawMode != GL_LINE_LOOP
                && drawMode != GL_TRIANGLES && drawMode != GL_TRIANGLE_STRIP
                && drawMode != GL_TRIANGLE_FAN) {
            throw new IllegalArgumentException(
                    "drawMode must be one of GL_POINTS, GL_LINES, GL_LINE_STRIP, GL_LINE_LOOP, GL_TRIANGLES, GL_TRIANGLE_FAN, GL_TRIANGLE_STRIP.");
        }
        setDrawMode(getNative(), drawMode);
    }

    /**
     * Rendering hints.
     * <p/>
     * You might expect the rendering process to sort the scene graph, from back
     * to front, so it can then draw translucent objects over the objects behind
     * them. But that's not how Meganekko works. Instead, it sorts the scene graph by
     * render order, then draws the sorted graph in traversal order. (Please
     * don't waste your time getting angry or trying to make sense of this;
     * please just take it as a bald statement of How Meganekko Currently Works.)
     * <p/>
     * The point is, to get transparency to work as you expect, you do need to
     * explicitly call {@link RenderData#setRenderingOrder(int)
     * setRenderingOrder():} objects are sorted from low render order to high
     * render order, so that a {@link #GEOMETRY} object will show through a
     * {@link #TRANSPARENT} object.
     */
    public abstract static class RenderingOrder {
        /**
         * Rendered first, below any other objects at the same distance from the
         * camera
         */
        public static final int BACKGROUND = 1000;
        /**
         * The default render order, if you don't explicitly call
         * {@link RenderData#setRenderingOrder(int)}
         */
        public static final int GEOMETRY = 2000;
        /**
         * The rendering order for see-through objects
         */
        public static final int TRANSPARENT = 3000;
        /**
         * The rendering order for sprites {@literal &c.}
         */
        public static final int OVERLAY = 4000;
    }

    /**
     * Items for the rendering options bit mask.
     */
    public abstract static class RenderMaskBit {
        /**
         * Render the mesh in the left {@link Camera camera}.
         */
        public static final int Left = 0x1;
        /**
         * Render the mesh in the right {@link Camera camera}.
         */
        public static final int Right = 0x2;
    }
}
