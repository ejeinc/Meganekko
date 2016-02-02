package com.eje_c.meganekko.animation;

import android.animation.TypeEvaluator;

import org.joml.Vector3f;

public class VectorEvaluator implements TypeEvaluator<Vector3f> {
    private final Vector3f result = new Vector3f();

    @Override
    public Vector3f evaluate(float fraction, Vector3f startValue, Vector3f endValue) {
        return startValue.lerp(endValue, fraction, result);
    }
}
