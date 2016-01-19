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
 * One of the key Meganekko classes: Encapsulates a 4x4 matrix that controls how GL
 * draws a mesh.
 * <p/>
 * Every {@link SceneObject#getTransform() scene object} has a
 * {@code Transform} which exposes more-or-less convenient methods to do
 * translation, rotation and scaling. Rotations can be made in either quaternion
 * or angle/axis terms; rotation values can be retrieved as either quaternion
 * components or as Euler angles.
 */
public class Transform extends Component {
    Transform() {
    }

    private Transform(long ptr) {
        super(ptr);
    }

    @Override
    protected native long initNativeInstance();

    /**
     * Get the X component of the transform's position.
     *
     * @return 'X' component of the transform's position.
     */
    public float getPositionX() {
        return getPositionX(getNative());
    }

    /**
     * Set the 'X' component of absolute position.
     * <p/>
     * Use {@link #translate(float, float, float)} to <em>move</em> the object.
     *
     * @param x New 'X' component of the absolute position.
     */
    public void setPositionX(float x) {
        setPositionX(getNative(), x);
    }

    /**
     * Get the 'Y' component of the transform's position.
     *
     * @return 'Y' component of the transform's position.
     */
    public float getPositionY() {
        return getPositionY(getNative());
    }

    /**
     * Set the 'Y' component of the absolute position.
     * <p/>
     * Use {@link #translate(float, float, float)} to <em>move</em> the object.
     *
     * @param y New 'Y' component of the absolute position.
     */
    public void setPositionY(float y) {
        setPositionY(getNative(), y);
    }

    /**
     * Get the 'Z' component of the transform's position.
     *
     * @return 'Z' component of the transform's position.
     */
    public float getPositionZ() {
        return getPositionZ(getNative());
    }

    /**
     * Set the 'Z' component of the absolute position.
     * <p/>
     * Use {@link #translate(float, float, float)} to <em>move</em> the object.
     *
     * @param z New 'Z' component of the absolute position.
     */
    public void setPositionZ(float z) {
        setPositionZ(getNative(), z);
    }

    /**
     * Set absolute position.
     * <p/>
     * Use {@link #translate(float, float, float)} to <em>move</em> the object.
     *
     * @param x 'X' component of the absolute position.
     * @param y 'Y' component of the absolute position.
     * @param z 'Z' component of the absolute position.
     */
    public void setPosition(float x, float y, float z) {
        setPosition(getNative(), x, y, z);
    }

    /**
     * Get the quaternion 'W' component.
     *
     * @return 'W' component of the transform's rotation, treated as a
     * quaternion.
     */
    public float getRotationW() {
        return getRotationW(getNative());
    }

    /**
     * Get the quaternion 'X' component.
     *
     * @return 'X' component of the transform's rotation, treated as a
     * quaternion.
     */
    public float getRotationX() {
        return getRotationX(getNative());
    }

    /**
     * Get the quaternion 'Y' component.
     *
     * @return 'Y' component of the transform's rotation, treated as a
     * quaternion.
     */
    public float getRotationY() {
        return getRotationY(getNative());
    }

    /**
     * Get the quaternion 'Z' component.
     *
     * @return 'Z' component of the transform's rotation, treated as a
     * quaternion.
     */
    public float getRotationZ() {
        return getRotationZ(getNative());
    }

    /**
     * Get the rotation around the 'Y' axis, in degrees.
     *
     * @return The transform's current rotation around the 'Y' axis, in degrees.
     */
    public float getRotationYaw() {
        return getRotationYaw(getNative());
    }

    /**
     * Get the rotation around the 'X' axis, in degrees.
     *
     * @return The transform's rotation around the 'X' axis, in degrees.
     */
    public float getRotationPitch() {
        return getRotationPitch(getNative());
    }

    /**
     * Get the rotation around the 'Z' axis, in degrees.
     *
     * @return The transform's rotation around the 'Z' axis, in degrees.
     */
    public float getRotationRoll() {
        return getRotationRoll(getNative());
    }

    /**
     * Set rotation, as a quaternion.
     * <p/>
     * Sets the transform's current rotation in quaternion terms. Overrides any
     * previous rotations using {@link #rotate(float, float, float, float)
     * rotate()}, {@link #rotateByAxis(float, float, float, float)
     * rotateByAxis()} , or
     * {@link #rotateByAxisWithPivot(float, float, float, float, float, float, float)
     * rotateByAxisWithPivot()} .
     *
     * @param w 'W' component of the quaternion.
     * @param x 'X' component of the quaternion.
     * @param y 'Y' component of the quaternion.
     * @param z 'Z' component of the quaternion.
     */
    public void setRotation(float w, float x, float y, float z) {
        setRotation(getNative(), w, x, y, z);
    }

    /**
     * Get the 'X' scale
     *
     * @return The transform's current scaling on the 'X' axis.
     */
    public float getScaleX() {
        return getScaleX(getNative());
    }

    /**
     * Set the transform's current scaling on the 'X' axis.
     *
     * @param x Scaling factor on the 'X' axis.
     */
    public void setScaleX(float x) {
        setScaleX(getNative(), x);
    }

    /**
     * Get the 'Y' scale
     *
     * @return The transform's current scaling on the 'Y' axis.
     */
    public float getScaleY() {
        return getScaleY(getNative());
    }

    /**
     * Set the transform's current scaling on the 'Y' axis.
     *
     * @param y Scaling factor on the 'Y' axis.
     */
    public void setScaleY(float y) {
        setScaleY(getNative(), y);
    }

    /**
     * Get the 'Z' scale
     *
     * @return The transform's current scaling on the 'Z' axis.
     */
    public float getScaleZ() {
        return getScaleZ(getNative());
    }

    /**
     * Set the transform's current scaling on the 'Z' axis.
     *
     * @param z Scaling factor on the 'Z' axis.
     */
    public void setScaleZ(float z) {
        setScaleZ(getNative(), z);
    }

    /**
     * Set [X, Y, Z] current scale
     *
     * @param x Scaling factor on the 'X' axis.
     * @param y Scaling factor on the 'Y' axis.
     * @param z Scaling factor on the 'Z' axis.
     */
    public void setScale(float x, float y, float z) {
        setScale(getNative(), x, y, z);
    }

    /**
     * Get the 4x4 single matrix.
     *
     * @return An array of 16 {@code float}s representing a 4x4 matrix in
     * OpenGL-compatible column-major format.
     */
    public float[] getModelMatrix() {
        return getModelMatrix(getNative());
    }

    /**
     * Set the 4x4 model matrix and set current scaling, rotation, and
     * transformation based on this model matrix.
     *
     * @param mat An array of 16 {@code float}s representing a 4x4 matrix in
     *            OpenGL-compatible column-major format.
     */
    public void setModelMatrix(float[] mat) {
        if (mat.length != 16) {
            throw new IllegalArgumentException("Size not equal to 16.");
        }
        setModelMatrix(getNative(), mat);
    }

    /**
     * Move the object, relative to its current position.
     * <p/>
     * Modify the tranform's current translation by applying translations on all
     * 3 axes.
     *
     * @param x 'X' delta
     * @param y 'Y' delta
     * @param z 'Z' delta
     */
    public void translate(float x, float y, float z) {
        translate(getNative(), x, y, z);
    }

    /**
     * Sets the absolute rotation in angle/axis terms.
     * <p/>
     * Rotates using the right hand rule.
     * <p/>
     * <p/>
     * Contrast this with {@link #rotate(float, float, float, float) rotate()},
     * {@link #rotateByAxis(float, float, float, float) rotateByAxis()}, or
     * {@link #rotateByAxisWithPivot(float, float, float, float, float, float, float)
     * rotateByAxisWithPivot()}, which all do relative rotations.
     *
     * @param angle Angle of rotation in degrees.
     * @param x     'X' component of the axis.
     * @param y     'Y' component of the axis.
     * @param z     'Z' component of the axis.
     */
    public void setRotationByAxis(float angle, float x, float y, float z) {
        setRotationByAxis(getNative(), angle, x, y, z);
    }

    /**
     * Modify the tranform's current rotation in quaternion terms.
     *
     * @param w 'W' component of the quaternion.
     * @param x 'X' component of the quaternion.
     * @param y 'Y' component of the quaternion.
     * @param z 'Z' component of the quaternion.
     */
    public void rotate(float w, float x, float y, float z) {
        rotate(getNative(), w, x, y, z);
    }

    /**
     * Modify the transform's current rotation in angle/axis terms.
     *
     * @param angle Angle of rotation in degrees.
     * @param x     'X' component of the axis.
     * @param y     'Y' component of the axis.
     * @param z     'Z' component of the axis.
     */
    public void rotateByAxis(float angle, float x, float y, float z) {
        rotateByAxis(getNative(), angle, x, y, z);
    }

    /**
     * Modify the transform's current rotation in angle/axis terms, around a
     * pivot other than the origin.
     *
     * @param angle  Angle of rotation in degrees.
     * @param axisX  'X' component of the axis.
     * @param axisY  'Y' component of the axis.
     * @param axisZ  'Z' component of the axis.
     * @param pivotX 'X' component of the pivot's location.
     * @param pivotY 'Y' component of the pivot's location.
     * @param pivotZ 'Z' component of the pivot's location.
     */
    public void rotateByAxisWithPivot(float angle, float axisX, float axisY,
                                      float axisZ, float pivotX, float pivotY, float pivotZ) {
        rotateByAxisWithPivot(getNative(), angle, axisX, axisY,
                axisZ, pivotX, pivotY, pivotZ);
    }


    /**
     * Reset the transform
     * <p/>
     * This will undo any translations, rotations, or scaling and reset the Transform back to default values.  This is the equivilent to setting the Transform to an identity matrix.
     */
    public void reset() {
        setPosition(0, 0, 0);
        setRotation(1, 0, 0, 0);
        setScale(1, 1, 1);
    }

    @Override
    public String toString() {
        return "Transform " + Integer.toHexString(hashCode()) + ", positionX = " + getPositionX()
                + ", positionY = " + getPositionY() + ", positionZ = " + getPositionZ()
                + ", scaleX = " + getScaleX() + ", scaleY = " + getScaleY() + ", scaleZ = "
                + getScaleZ() + ", rotationW = " + getRotationW() + ", rotationX = "
                + getRotationX() + ", rotationY = " + getRotationY() + ", rotationZ = "
                + getRotationZ();
    }

    private static native float getPositionX(long transform);

    private static native float getPositionY(long transform);

    private static native float getPositionZ(long transform);

    private static native void setPosition(long transform, float x, float y, float z);

    private static native void setPositionX(long transform, float x);

    private static native void setPositionY(long transform, float y);

    private static native void setPositionZ(long transform, float z);

    private static native float getRotationW(long transform);

    private static native float getRotationX(long transform);

    private static native float getRotationY(long transform);

    private static native float getRotationZ(long transform);

    private static native float getRotationYaw(long transform);

    private static native float getRotationPitch(long transform);

    private static native float getRotationRoll(long transform);

    private static native void setRotation(long transform, float w, float x, float y, float z);

    private static native float getScaleX(long transform);

    private static native float getScaleY(long transform);

    private static native float getScaleZ(long transform);

    private static native void setScale(long transform, float x, float y, float z);

    private static native void setScaleX(long transform, float x);

    private static native void setScaleY(long transform, float y);

    private static native void setScaleZ(long transform, float z);

    private static native float[] getModelMatrix(long transform);

    private static native void setModelMatrix(long tranform, float[] mat);

    private static native void translate(long transform, float x, float y, float z);

    private static native void setRotationByAxis(long transform, float angle, float x, float y, float z);

    private static native void rotate(long transform, float w, float x, float y, float z);

    private static native void rotateByAxis(long transform, float angle, float x, float y, float z);

    private static native void rotateByAxisWithPivot(long transform, float angle,
                                                     float axisX, float axisY, float axisZ, float pivotX, float pivotY,
                                                     float pivotZ);
}
