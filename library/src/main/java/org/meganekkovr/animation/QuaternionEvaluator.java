package org.meganekkovr.animation;

import android.animation.TypeEvaluator;

import org.joml.Quaternionf;

/**
 * This evaluator can be used to perform type interpolation between {@link Quaternionf} values.
 */
class QuaternionEvaluator implements TypeEvaluator<Quaternionf> {
    private final Quaternionf mResult = new Quaternionf();

    @Override
    public Quaternionf evaluate(float fraction, Quaternionf startValue, Quaternionf endValue) {
        return startValue.slerp(endValue, fraction, mResult);
    }
}
