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

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * This is one of the key Meganekko classes: It holds GL meshes.
 * <p>
 * A GL mesh is a net of triangles that define an object's surface geometry.
 */
public class Mesh extends HybridObject {

    public Mesh() {
    }

    Mesh(long ptr) {
        super(ptr);
    }

    private static native void build(long renderData, float[] positions, float[] colors, float[] uvs, int[] triangles);

    private static native void buildQuad(long renderData, float width, float heigh);

    private static native void buildTesselatedQuad(long renderData, int horizontal, int vertical, boolean twoSided);

    private static native void buildFadedScreenMask(long renderData, float xFraction, float yFraction);

    private static native void buildVignette(long renderData, float xFraction, float yFraction);

    private static native void buildTesselatedCylinder(long renderData, float radius, float height, int horizontal, int vertical, float uScale, float vScale);

    private static native void buildDome(long renderData, float latRads, float uScale, float vScale);

    private static native void buildGlobe(long renderData, float uScale, float vScale);

    private static native void buildSpherePatch(long renderData, float fov);

    private static native void buildCalibrationLines(long renderData, int extraLines, boolean fullGrid);

    private static native void buildUnitCubeLines(long renderData);

    public void build(float[] positions, float[] colors, float[] uvs, int[] triangles) {

        if (positions.length % 3 != 0) {
            throw new IllegalArgumentException("positions element count must be multiple of 3.");
        } else if (colors.length % 4 != 0) {
            throw new IllegalArgumentException("positions element count must be multiple of 4.");
        } else if (uvs.length % 2 != 0) {
            throw new IllegalArgumentException("positions element count must be multiple of 2.");
        } else if (triangles.length % 3 != 0) {
            throw new IllegalArgumentException("triangles element count must be multiple of 3.");
        }

        int positionSize = positions.length / 3;
        int colorSize = colors.length / 4;
        int uvSize = uvs.length / 2;

        if (positionSize != colorSize) {
            throw new IllegalArgumentException("position elements are " + positionSize + " but color elements are " + colorSize + ".");
        } else if (colorSize != uvSize) {
            throw new IllegalArgumentException("color elements are " + colorSize + " but uv elements are " + uvSize + ".");
        }

        build(getNative(), positions, colors, uvs, triangles);
    }

    /**
     * Build quad mesh
     *
     * @param width
     * @param height
     */
    public void buildQuad(float width, float height) {

        /*
         * 0    2
         * *----*
         * |  / |
         * | /  |
         * *----*
         * 1    3
         */

        float[] positions = {
                width * -0.5f, height * 0.5f, 0.0f,  // Left Top
                width * -0.5f, height * -0.5f, 0.0f, // Left Bottom
                width * 0.5f, height * 0.5f, 0.0f,   // Right Top
                width * 0.5f, height * -0.5f, 0.0f   // Right Bottom
        };

        float[] colors = {
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };

        float[] uvs = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        int[] triangles = {
                0, 1, 2,
                1, 3, 2
        };

        build(positions, colors, uvs, triangles);
    }

    public void buildTesselatedQuad(int horizontal, int vertical, boolean twoSided) {
        buildTesselatedQuad(getNative(), horizontal, vertical, twoSided);
    }

    public void buildFadedScreenMask(float xFraction, float yFraction) {
        buildFadedScreenMask(getNative(), xFraction, yFraction);
    }

    public void buildVignette(float xFraction, float yFraction) {
        buildVignette(getNative(), xFraction, yFraction);
    }

    public void buildTesselatedCylinder(float radius, float height, int horizontal, int vertical, float uScale, float vScale) {
        buildTesselatedCylinder(getNative(), radius, height, horizontal, vertical, uScale, vScale);
    }

    public void buildDome(float latRads, float uScale, float vScale) {
        buildDome(getNative(), latRads, uScale, vScale);
    }

    public void buildGlobe(float uScale, float vScale) {
        buildGlobe(getNative(), uScale, vScale);
    }

    public void buildSpherePatch(float fov) {
        buildSpherePatch(getNative(), fov);
    }

    public void buildCalibrationLines(int extraLines, boolean fullGrid) {
        buildCalibrationLines(getNative(), extraLines, fullGrid);
    }

    public void buildUnitCubeLines() {
        buildUnitCubeLines(getNative());
    }

    /**
     * Creates a quad consisting of two triangles, with the specified width and
     * height.
     *
     * @param width  the quad's width
     * @param height the quad's height
     * @return A 2D, rectangular mesh with four vertices and two triangles
     */
    public static Mesh createQuad(float width, float height) {
        Mesh mesh = new Mesh();
        mesh.buildQuad(width, height);
        return mesh;
    }

    public static Mesh from(View view) {
        return from(view, getDefaultScaleFactor());
    }

    public static Mesh from(View view, float scaleFactor) {
        return createQuad(scaleFactor * view.getMeasuredWidth(), scaleFactor * view.getMeasuredHeight());
    }

    public static Mesh from(Drawable drawable) {
        return from(drawable, getDefaultScaleFactor());
    }

    public static Mesh from(Drawable drawable, float scaleFactor) {
        return createQuad(scaleFactor * drawable.getIntrinsicWidth(), scaleFactor * drawable.getIntrinsicHeight());
    }

    public static float getDefaultScaleFactor() {
        return 0.006f;
    }

    @Override
    protected native long initNativeInstance();
}
