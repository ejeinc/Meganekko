package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.Transform;

import org.joml.Matrix4f;

public class MatrixUpdateListener extends TransformUpdateListener {

    public MatrixUpdateListener(Transform transform) {
        super(transform);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        transform.setModelMatrix((Matrix4f) animation.getAnimatedValue());
    }
}
