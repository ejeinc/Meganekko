package com.eje_c.meganekko.animation;

import android.animation.TypeEvaluator;

import org.joml.Matrix4f;

/**
 * This evaluator can be used to perform type interpolation between {@link Matrix4f} values.
 */
public class MatrixEvaluator implements TypeEvaluator<Matrix4f> {
    private final Matrix4f mResult = new Matrix4f();

    @Override
    public Matrix4f evaluate(float fraction, Matrix4f startValue, Matrix4f endValue) {
        return startValue.fma4x3(endValue, fraction, mResult);
    }
}
