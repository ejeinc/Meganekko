package org.meganekkovr;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HeadTransform {

    // For validFlags
    private static final int VALID_FLAG_QUATERNION_BIT = 1;
    private static final int VALID_FLAG_FORWARD_VECTOR_BIT = 1 << 1;
    private static final int VALID_FLAG_UP_VECTOR_BIT = 1 << 2;
    private static final int VALID_FLAG_RIGHT_VECTOR_BIT = 1 << 3;

    private static final Vector3f WORLD_FORWARD = new Vector3f(0, 0, -1);
    private static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);
    private static final Vector3f WORLD_RIGHT = new Vector3f(1, 0, 0);

    private static HeadTransform instance; // singleton

    private final float[] tmpValues = new float[4]; // For JNI value getter

    // These are set when needed and cached.
    private final Quaternionf quaternion = new Quaternionf();
    private final Vector3f forward = new Vector3f();
    private final Vector3f up = new Vector3f();
    private final Vector3f right = new Vector3f();

    private final long appPtr;
    private int validFlags = 0;

    private HeadTransform(long appPtr) {
        this.appPtr = appPtr;
    }

    public synchronized static void init(long appPtr) {
        if (instance != null)
            throw new IllegalStateException("init was called twice!");

        instance = new HeadTransform(appPtr);
    }

    public static HeadTransform getInstance() {
        return instance;
    }

    private static native void getQuaternion(long appPtr, float[] values);

    /**
     * Called on every frame update.
     */
    void invalidate() {
        validFlags = 0;
    }

    /**
     * Provides the quaternion representing the head rotation.
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * <code>
     * Quaternionf q = new Quaternionf(HeadTransform.getInstance().getQuaternion());
     * q.rotateX(1); // modify
     * </code>
     * or
     * <code>
     * Quaternionf q = new Quaternionf();
     * q.set(HeadTransform.getInstance().getQuaternion());
     * q.x = 0; // modify
     * </code>
     *
     * @return The quaternion representing the head rotation.
     */
    public Quaternionf getQuaternion() {

        // Update at first time
        if ((validFlags & VALID_FLAG_QUATERNION_BIT) == 0) {
            getQuaternion(appPtr, tmpValues);
            quaternion.set(tmpValues[0], tmpValues[1], tmpValues[2], tmpValues[3]);
            validFlags |= VALID_FLAG_QUATERNION_BIT;
        }

        return quaternion;
    }

    /**
     * Provides the direction the head is looking towards as a 3x1 unit vector.
     * Note that in OpenGL the forward vector points into the -Z direction. Make sure to invert it if ever used to compute the basis of a right-handed system.
     * <br>
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * <code>
     * Vector3f v = new Vector3f(HeadTransform.getInstance().getForwardVector());
     * v.mul(3); // modify
     * </code>
     * or
     * <code>
     * Vector3f v = new Vector3f();
     * v.set(HeadTransform.getInstance().getForwardVector());
     * v.x = 0; // modify
     * </code>
     *
     * @return The forward vector.
     */
    public Vector3f getForwardVector() {

        // Update at first time
        if ((validFlags & VALID_FLAG_FORWARD_VECTOR_BIT) == 0) {
            Quaternionf q = getQuaternion();
            q.transform(WORLD_FORWARD, forward);
            validFlags |= VALID_FLAG_FORWARD_VECTOR_BIT;
        }

        return forward;
    }

    /**
     * Provides the upwards direction of the head.
     * <br>
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * <code>
     * Vector3f v = new Vector3f(HeadTransform.getInstance().getUpVector());
     * v.mul(3); // modify
     * </code>
     * or
     * <code>
     * Vector3f v = new Vector3f();
     * v.set(HeadTransform.getInstance().getUpVector());
     * v.x = 0; // modify
     * </code>
     *
     * @return The upwards direction of the head.
     */
    public Vector3f getUpVector() {

        // Update at first time
        if ((validFlags & VALID_FLAG_UP_VECTOR_BIT) == 0) {
            Quaternionf q = getQuaternion();
            q.transform(WORLD_UP, up);
            validFlags |= VALID_FLAG_UP_VECTOR_BIT;
        }

        return up;
    }

    /**
     * Provides the rightwards direction of the head.
     * <br>
     * Note that the result of this method is shared with other callers for performance.
     * You must not modify it directly. It will be a cause of unpredictable bug.
     * If you would like to modify the result, do like
     * <code>
     * Vector3f v = new Vector3f(HeadTransform.getInstance().getRightVector());
     * v.mul(3); // modify
     * </code>
     * or
     * <code>
     * Vector3f v = new Vector3f();
     * v.set(HeadTransform.getInstance().getRightVector());
     * v.x = 0; // modify
     * </code>
     *
     * @return The rightwards direction of the head.
     */
    public Vector3f getRightVector() {

        // Update at first time
        if ((validFlags & VALID_FLAG_RIGHT_VECTOR_BIT) == 0) {
            Quaternionf q = getQuaternion();
            q.transform(WORLD_RIGHT, right);
            validFlags |= VALID_FLAG_RIGHT_VECTOR_BIT;
        }

        return right;
    }
}
