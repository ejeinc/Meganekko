package com.eje_c.meganekko.animation;

import android.animation.TypeEvaluator;

import org.joml.Quaternionf;

/**
 * This evaluator can be used to perform type interpolation between {@link Quaternionf} values.
 */
public class QuaternionEvaluator implements TypeEvaluator<Quaternionf> {
    private final Quaternionf result = new Quaternionf();

    @Override
    public Quaternionf evaluate(float fraction, Quaternionf startValue, Quaternionf endValue) {
        return startValue.slerp(endValue, fraction, result);
    }
}
