package org.meganekkovr

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class HeadTransform private constructor(private val appPtr: Long) {

    private val tmpValues = FloatArray(16) // For JNI value getter
    private var validFlags = 0

    // These are set when needed and cached.
    val matrix = Matrix4f()
        get() {

            // Update at first time
            if (validFlags and VALID_FLAG_MATRIX_BIT == 0) {
                getCenterEyeViewMatrix(appPtr, tmpValues)
                field.set(tmpValues)
                validFlags = validFlags or VALID_FLAG_MATRIX_BIT
            }

            return field
        }

    /**
     * Provides the quaternion representing the head rotation.
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * `
     * Quaternionf q = new Quaternionf(HeadTransform.getInstance().quaternion);
     * q.rotateX(1); // modify
    ` *
     * or
     * `
     * Quaternionf q = new Quaternionf();
     * q.set(HeadTransform.getInstance().quaternion);
     * q.x = 0; // modify
    ` *
     *
     * @return The quaternion representing the head rotation.
     */
    val position = Vector3f()
        get() {

            // Update at first time
            if (validFlags and VALID_FLAG_POSITION_BIT == 0) {
                matrix.transformPosition(0f, 0f, 0f, field)
                validFlags = validFlags or VALID_FLAG_POSITION_BIT
            }

            return field
        }

    /**
     * Provides the quaternion representing the head rotation.
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * `
     * Quaternionf q = new Quaternionf(HeadTransform.getInstance().getQuaternion());
     * q.rotateX(1); // modify
     * `
     * or
     * `
     * Quaternionf q = new Quaternionf();
     * q.set(HeadTransform.getInstance().getQuaternion());
     * q.x = 0; // modify
     * `
     *
     * @return The quaternion representing the head rotation.
     */
    val quaternion = Quaternionf()
        get() {

            // Update at first time
            if (validFlags and VALID_FLAG_QUATERNION_BIT == 0) {
                field.setFromNormalized(matrix)
                validFlags = validFlags or VALID_FLAG_QUATERNION_BIT
            }

            return field
        }

    /**
     * Provides the direction the head is looking towards as a 3x1 unit vector.
     * Note that in OpenGL the forward vector points into the -Z direction. Make sure to invert it if ever used to compute the basis of a right-handed system.
     * <br></br>
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * `
     * Vector3f v = new Vector3f(HeadTransform.getInstance().getForwardVector());
     * v.mul(3); // modify
     * `
     * or
     * `
     * Vector3f v = new Vector3f();
     * v.set(HeadTransform.getInstance().getForwardVector());
     * v.x = 0; // modify
     * `
     *
     * @return The forward vector.
     */
    val forwardVector = Vector3f()
        get() {
            if (validFlags and VALID_FLAG_FORWARD_VECTOR_BIT == 0) {
                quaternion.transform(WORLD_FORWARD, field)
                validFlags = validFlags or VALID_FLAG_FORWARD_VECTOR_BIT
            }

            return field
        }

    /**
     * Provides the upwards direction of the head.
     * <br></br>
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * `
     * Vector3f v = new Vector3f(HeadTransform.getInstance().getUpVector());
     * v.mul(3); // modify
    ` *
     * or
     * `
     * Vector3f v = new Vector3f();
     * v.set(HeadTransform.getInstance().getUpVector());
     * v.x = 0; // modify
    ` *
     *
     * @return The upwards direction of the head.
     */
    val upVector = Vector3f()
        get() {
            if (validFlags and VALID_FLAG_UP_VECTOR_BIT == 0) {
                quaternion.transform(WORLD_UP, field)
                validFlags = validFlags or VALID_FLAG_UP_VECTOR_BIT
            }

            return field
        }

    /**
     * Provides the rightwards direction of the head.
     * <br></br>
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * `
     * Vector3f v = new Vector3f(HeadTransform.getInstance().getRightVector());
     * v.mul(3); // modify
    ` *
     * or
     * `
     * Vector3f v = new Vector3f();
     * v.set(HeadTransform.getInstance().getRightVector());
     * v.x = 0; // modify
    ` *
     *
     * @return The rightwards direction of the head.
     */
    val rightVector = Vector3f()
        get() {
            if (validFlags and VALID_FLAG_RIGHT_VECTOR_BIT == 0) {
                quaternion.transform(WORLD_RIGHT, field)
                validFlags = validFlags or VALID_FLAG_RIGHT_VECTOR_BIT
            }

            return field
        }

    /**
     * Called on every frame update.
     */
    fun invalidate() {
        validFlags = 0
    }

    private external fun getCenterEyeViewMatrix(appPtr: Long, values: FloatArray)

    companion object {

        // For validFlags
        private const val VALID_FLAG_QUATERNION_BIT = 1
        private const val VALID_FLAG_FORWARD_VECTOR_BIT = 1 shl 1
        private const val VALID_FLAG_UP_VECTOR_BIT = 1 shl 2
        private const val VALID_FLAG_RIGHT_VECTOR_BIT = 1 shl 3
        private const val VALID_FLAG_POSITION_BIT = 1 shl 4
        private const val VALID_FLAG_MATRIX_BIT = 1 shl 5

        private val WORLD_FORWARD = Vector3f(0f, 0f, -1f)
        private val WORLD_UP = Vector3f(0f, 1f, 0f)
        private val WORLD_RIGHT = Vector3f(1f, 0f, 0f)

        @JvmStatic
        lateinit var instance: HeadTransform

        @Synchronized
        @JvmStatic
        internal fun init(appPtr: Long) {
            instance = HeadTransform(appPtr)
        }
    }
}
