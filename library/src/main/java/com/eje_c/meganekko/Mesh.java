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

import com.eje_c.meganekko.utility.Exceptions;

import java.util.EnumSet;

import static com.eje_c.meganekko.utility.Assert.checkDivisibleDataLength;
import static com.eje_c.meganekko.utility.Assert.checkStringNotNullOrEmpty;

/**
 * This is one of the key Meganekko classes: It holds GL meshes.
 * <p/>
 * A GL mesh is a net of triangles that define an object's surface geometry.
 */
public class Mesh extends HybridObject {

    public Mesh() {
    }

    Mesh(long ptr) {
        super(ptr);
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

        float[] vertices = {
                width * -0.5f, height * 0.5f, 0.0f,
                width * -0.5f, height * -0.5f, 0.0f,
                width * 0.5f, height * 0.5f, 0.0f,
                width * 0.5f, height * -0.5f, 0.0f
        };
        mesh.setVertices(vertices);

        final float[] normals = {
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        };
        mesh.setNormals(normals);

        final float[] texCoords = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };
        mesh.setTexCoords(texCoords);

        char[] triangles = {0, 1, 2, 1, 3, 2};
        mesh.setTriangles(triangles);

        return mesh;
    }

    /**
     * Loads a file as a {@link Mesh}.
     * <p/>
     * Note that this method can be quite slow; we recommend never calling it
     * from the GL thread.
     *
     * @param androidResource Basically, a stream containing a 3D model. The
     *                        {@link AndroidResource} class has six constructors to handle a
     *                        wide variety of Android resource types. Taking a
     *                        {@code AndroidResource} here eliminates six overloads.
     * @return The file as a GL mesh.
     */
    public static Mesh from(AndroidResource androidResource) {
        return from(androidResource, ImportSettings.getRecommendedSettings());
    }

    /**
     * Loads a file as a {@link Mesh}.
     * <p/>
     * Note that this method can be quite slow; we recommend never calling it
     * from the GL thread.
     *
     * @param androidResource Basically, a stream containing a 3D model. The
     *                        {@link AndroidResource} class has six constructors to handle a
     *                        wide variety of Android resource types. Taking a
     *                        {@code AndroidResource} here eliminates six overloads.
     * @param settings        Additional import {@link ImportSettings settings}.
     * @return The file as a GL mesh.
     */
    public static Mesh from(AndroidResource androidResource, EnumSet<ImportSettings> settings) {
        AssimpImporter assimpImporter = Importer.readFileFromResources(androidResource, settings);
        return assimpImporter.getMesh(0);
    }

    private static native float[] getVertices(long mesh);

    private static native void setVertices(long mesh, float[] vertices);

    private static native float[] getNormals(long mesh);

    private static native void setNormals(long mesh, float[] normals);

    private static native float[] getTexCoords(long mesh);

    private static native void setTexCoords(long mesh, float[] texCoords);

    private static native char[] getTriangles(long mesh);

    private static native void setTriangles(long mesh, char[] triangles);

    private static native float[] getFloatVector(long mesh, String key);

    private static native void setFloatVector(long mesh, String key, float[] floatVector);

    private static native float[] getVec2Vector(long mesh, String key);

    private static native void setVec2Vector(long mesh, String key, float[] vec2Vector);

    private static native float[] getVec3Vector(long mesh, String key);

    private static native void setVec3Vector(long mesh, String key, float[] vec3Vector);

    private static native float[] getVec4Vector(long mesh, String key);

    private static native void setVec4Vector(long mesh, String key, float[] vec4Vector);

    private static native long getBoundingBox(long mesh);

    @Override
    protected native long initNativeInstance();

    /**
     * Get the 3D vertices of the mesh. Each vertex is represented as a packed
     * {@code float} triplet:
     * <p/>
     * <code>
     * { x0, y0, z0, x1, y1, z1, x2, y2, z2, ... }
     * </code>
     *
     * @return Array with the packed vertex data.
     */
    public float[] getVertices() {
        return getVertices(getNative());
    }

    /**
     * Sets the 3D vertices of the mesh. Each vertex is represented as a packed
     * {@code float} triplet:
     * <p/>
     * <code>{ x0, y0, z0, x1, y1, z1, x2, y2, z2, ...}</code>
     *
     * @param vertices Array containing the packed vertex data.
     */
    public void setVertices(float[] vertices) {
        checkValidFloatArray("vertices", vertices, 3);
        setVertices(getNative(), vertices);
    }

    /**
     * Get the normal vectors of the mesh. Each normal vector is represented as
     * a packed {@code float} triplet:
     * <p/>
     * <code>{ x0, y0, z0, x1, y1, z1, x2, y2, z2, ...}</code>
     *
     * @return Array with the packed normal data.
     */
    public float[] getNormals() {
        return getNormals(getNative());
    }

    /**
     * Sets the normal vectors of the mesh. Each normal vector is represented as
     * a packed {@code float} triplet:
     * <p/>
     * <code>{ x0, y0, z0, x1, y1, z1, x2, y2, z2, ...}</code>
     *
     * @param normals Array containing the packed normal data.
     */
    public void setNormals(float[] normals) {
        checkValidFloatArray("normals", normals, 3);
        setNormals(getNative(), normals);
    }

    /**
     * Get the u,v texture coordinates for the mesh. Each texture coordinate is
     * represented as a packed {@code float} pair:
     * <p/>
     * <code>{ u0, v0, u1, v1, u2, v2, ...}</code>
     *
     * @return Array with the packed texture coordinate data.
     */
    public float[] getTexCoords() {
        return getTexCoords(getNative());
    }

    /**
     * Sets the texture coordinates for the mesh. Each texture coordinate is
     * represented as a packed {@code float} pair:
     * <p/>
     * <code>{ u0, v0, u1, v1, u2, v2, ...}</code>
     *
     * @param texCoords Array containing the packed texture coordinate data.
     */
    public void setTexCoords(float[] texCoords) {
        checkValidFloatArray("texCoords", texCoords, 2);
        setTexCoords(getNative(), texCoords);
    }

    /**
     * Get the triangle vertex indices of the mesh. The indices for each
     * triangle are represented as a packed {@code char} triplet, where
     * {@code t0} is the first triangle, {@code t1} is the second, etc.:
     * <p/>
     * <code>
     * { t0[0], t0[1], t0[2], t1[0], t1[1], t1[2], ...}
     * </code>
     *
     * @return Array with the packed triangle index data.
     */
    public char[] getTriangles() {
        return getTriangles(getNative());
    }

    /**
     * Sets the triangle vertex indices of the mesh. The indices for each
     * triangle are represented as a packed {@code int} triplet, where
     * {@code t0} is the first triangle, {@code t1} is the second, etc.:
     * <p/>
     * <code>
     * { t0[0], t0[1], t0[2], t1[0], t1[1], t1[2], ...}
     * </code>
     *
     * @param triangles Array containing the packed triangle index data.
     */
    public void setTriangles(char[] triangles) {
        checkDivisibleDataLength("triangles", triangles, 3);
        setTriangles(getNative(), triangles);
    }

    /**
     * Get the array of {@code float} scalars bound to the shader attribute
     * {@code key}.
     *
     * @param key Name of the shader attribute
     * @return Array of {@code float} scalars.
     */
    public float[] getFloatVector(String key) {
        return getFloatVector(getNative(), key);
    }

    /**
     * Bind an array of {@code float} scalars to the shader attribute
     * {@code key}.
     *
     * @param key         Name of the shader attribute
     * @param floatVector Data to bind to the shader attribute.
     */
    public void setFloatVector(String key, float[] floatVector) {
        checkValidFloatVector("key", key, "floatVector", floatVector, 1);
        setFloatVector(getNative(), key, floatVector);
    }

    /**
     * Get the array of two-component {@code float} vectors bound to the shader
     * attribute {@code key}.
     *
     * @param key Name of the shader attribute
     * @return Array of two-component {@code float} vectors.
     */
    public float[] getVec2Vector(String key) {
        return getVec2Vector(getNative(), key);
    }

    /**
     * Bind an array of two-component {@code float} vectors to the shader
     * attribute {@code key}.
     *
     * @param key        Name of the shader attribute
     * @param vec2Vector Two-component {@code float} vector data to bind to the shader
     *                   attribute.
     */
    public void setVec2Vector(String key, float[] vec2Vector) {
        checkValidFloatVector("key", key, "vec2Vector", vec2Vector, 2);
        setVec2Vector(getNative(), key, vec2Vector);
    }

    /**
     * Get the array of three-component {@code float} vectors bound to the
     * shader attribute {@code key}.
     *
     * @param key Name of the shader attribute
     * @return Array of three-component {@code float} vectors.
     */
    public float[] getVec3Vector(String key) {
        return getVec3Vector(getNative(), key);
    }

    /**
     * Bind an array of three-component {@code float} vectors to the shader
     * attribute {@code key}.
     *
     * @param key        Name of the shader attribute
     * @param vec3Vector Three-component {@code float} vector data to bind to the
     *                   shader attribute.
     */
    public void setVec3Vector(String key, float[] vec3Vector) {
        checkValidFloatVector("key", key, "vec3Vector", vec3Vector, 3);
        setVec3Vector(getNative(), key, vec3Vector);
    }

    /**
     * Get the array of four-component {@code float} vectors bound to the shader
     * attribute {@code key}.
     *
     * @param key Name of the shader attribute
     * @return Array of four-component {@code float} vectors.
     */
    public float[] getVec4Vector(String key) {
        return getVec4Vector(getNative(), key);
    }

    /**
     * Bind an array of four-component {@code float} vectors to the shader
     * attribute {@code key}.
     *
     * @param key        Name of the shader attribute
     * @param vec4Vector Four-component {@code float} vector data to bind to the shader
     *                   attribute.
     */
    public void setVec4Vector(String key, float[] vec4Vector) {
        checkValidFloatVector("key", key, "vec4Vector", vec4Vector, 4);
        setVec4Vector(getNative(), key, vec4Vector);
    }

    private void checkValidFloatVector(String keyName, String key, String vectorName, float[] vector, int expectedComponents) {
        checkStringNotNullOrEmpty(keyName, key);
        checkDivisibleDataLength(vectorName, vector, expectedComponents);
        checkVectorLengthWithVertices(vectorName, vector.length, expectedComponents);
    }

    private void checkValidFloatArray(String parameterName, float[] data, int expectedComponents) {
        checkDivisibleDataLength(parameterName, data, expectedComponents);
    }

    private void checkVectorLengthWithVertices(String parameterName, int dataLength, int expectedComponents) {
        int verticesNumber = getVertices().length / 3;
        int numberOfElements = dataLength / expectedComponents;
        if (dataLength / expectedComponents != verticesNumber) {
            throw Exceptions
                    .IllegalArgument(
                            "The input array %s should be an array of %d-component elements and the number of elements should match the number of vertices. The current number of elements is %d, but the current number of vertices is %d.",
                            parameterName, expectedComponents,
                            numberOfElements, verticesNumber);
        }
    }
}
