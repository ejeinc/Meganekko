package org.meganekkovr.animation

import android.animation.TypeEvaluator

import org.joml.Vector3f

/**
 * This evaluator can be used to perform type interpolation between [Vector3f] values.
 */
internal class VectorEvaluator : TypeEvaluator<Vector3f> {
    private val mResult = Vector3f()

    /**
     * This function returns the result of linearly interpolating the start and end values,
     * with fraction representing the proportion between the start and end values.
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue The start value.
     * @param endValue   The end value.
     * @return A linear interpolation between the start and end values, given the `fraction` parameter.
     */
    override fun evaluate(fraction: Float, startValue: Vector3f, endValue: Vector3f): Vector3f {
        return startValue.lerp(endValue, fraction, mResult)
    }
}
