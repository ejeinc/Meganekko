package com.eje_c.meganekko.animation;

import android.animation.TypeEvaluator;

import org.joml.Vector3f;

/**
 * This evaluator can be used to perform type interpolation between {@link Vector3f} values.
 */
public class VectorEvaluator implements TypeEvaluator<Vector3f> {
    private final Vector3f mResult = new Vector3f();

    /**
     * This function returns the result of linearly interpolating the start and end values,
     * with fraction representing the proportion between the start and end values.
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public Vector3f evaluate(float fraction, Vector3f startValue, Vector3f endValue) {
        return startValue.lerp(endValue, fraction, mResult);
    }
}
