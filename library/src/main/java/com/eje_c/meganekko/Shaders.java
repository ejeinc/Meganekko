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

/**
 * The API shared by {@link Material} and {@link GVRPostEffect}.
 * <p/>
 * <p>
 * <table border="1">
 * <tr>
 * <td>{@link MaterialShaderManager} {@code implements}
 * {@link ShaderManagers}</td>
 * <td>{@link MaterialMap} {@code implements} {@link ShaderMaps}</td>
 * <td>{@link Material} {@code implements} {@link Shaders}</td>
 * </tr>
 * </table>
 * </p>
 */
public interface Shaders<ID> {

    static final String MAIN_TEXTURE = "main_texture";

    /**
     * @return The current shader id.
     */
    public ID getShaderType();

    /**
     * Set shader id
     *
     * @param shaderId The new shader id. This is an opaque type, used to keep object
     *                 and scene shader ids in distinct namespaces.
     */
    public void setShaderType(ID shaderId);

    /**
     * Get the {@code float} bound to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform
     * @return The bound {@code float} value.
     */
    public float getFloat(String key);

    /**
     * Bind a {@code float} to the shader uniform {@code key}.
     *
     * @param key   Name of the shader uniform
     * @param value New data
     */
    public void setFloat(String key, float value);

    /**
     * Get the {@code float[2]} vector bound to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform
     * @return The {@code vec2} as a Java {@code float[2]}
     */
    public float[] getVec2(String key);

    /**
     * Bind a {@code vec2} to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform
     * @param x   First component of the vector.
     * @param y   Second component of the vector.
     */
    public void setVec2(String key, float x, float y);

    /**
     * Get the {@code float[3]} vector bound to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform
     * @return The {@code vec3} as a Java {@code float[3]}
     */
    public float[] getVec3(String key);

    /**
     * Bind a {@code vec3} to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform to bind the data to.
     * @param x   First component of the vector.
     * @param y   Second component of the vector.
     * @param z   Third component of the vector.
     */
    public void setVec3(String key, float x, float y, float z);

    /**
     * Get the {@code float[4]} vector bound to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform
     * @return The {@code vec4} as a Java {@code float[3]}
     */
    public float[] getVec4(String key);

    /**
     * Bind a {@code vec4} to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform to bind the data to.
     * @param x   First component of the vector.
     * @param y   Second component of the vector.
     * @param z   Third component of the vector.
     * @param w   Fourth component of the vector.
     */
    public void setVec4(String key, float x, float y, float z, float w);

    /**
     * Bind a {@code mat4} to the shader uniform {@code key}.
     *
     * @param key Name of the shader uniform to bind the data to.
     */
    public void setMat4(String key, float x1, float y1, float z1, float w1,
                        float x2, float y2, float z2, float w2, float x3, float y3,
                        float z3, float w3, float x4, float y4, float z4, float w4);
}
