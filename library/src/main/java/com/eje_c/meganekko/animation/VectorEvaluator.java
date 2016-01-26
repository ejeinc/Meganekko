package com.eje_c.meganekko.animation;

import android.animation.TypeEvaluator;

import org.joml.Vector3f;

public class VectorEvaluator implements TypeEvaluator {
    private final Vector3f result = new Vector3f();

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Vector3f v1 = (Vector3f) startValue;
        Vector3f v2 = (Vector3f) endValue;
        return v1.lerp(v2, fraction, result);
    }
}
