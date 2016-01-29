package com.eje_c.meganekko.animation;

import android.animation.TypeEvaluator;

import org.joml.Matrix4f;

public class MatrixEvaluator implements TypeEvaluator {
    private final Matrix4f result = new Matrix4f();

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Matrix4f q1 = (Matrix4f) startValue;
        Matrix4f q2 = (Matrix4f) endValue;
        return q1.fma4x3(q2, fraction, result);
    }
}
