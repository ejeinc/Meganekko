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
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue The start value.
     * @param endValue   The end value.
     * @return A linear interpolation between the start and end values, given the <code>fraction</code> parameter.
     */
    @Override
    public Vector3f evaluate(float fraction, Vector3f startValue, Vector3f endValue) {
        return startValue.lerp(endValue, fraction, mResult);
    }
}
