package org.meganekkovr.animation

import android.animation.TypeEvaluator

import org.joml.Quaternionf

/**
 * This evaluator can be used to perform type interpolation between [Quaternionf] values.
 */
internal class QuaternionEvaluator : TypeEvaluator<Quaternionf> {
    private val mResult = Quaternionf()

    override fun evaluate(fraction: Float, startValue: Quaternionf, endValue: Quaternionf): Quaternionf {
        return startValue.slerp(endValue, fraction, mResult)
    }
}
