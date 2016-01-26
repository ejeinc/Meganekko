package com.eje_c.meganekko.animation;

import android.animation.TypeEvaluator;

import org.joml.Quaternionf;

public class QuaternionEvaluator implements TypeEvaluator {
    private final Quaternionf result = new Quaternionf();

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Quaternionf q1 = (Quaternionf) startValue;
        Quaternionf q2 = (Quaternionf) endValue;
        return q1.slerp(q2, fraction, result);
    }
}
