package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.Transform;

import org.joml.Quaternionf;

public class RotationUpdateListener extends TransformUpdateListener {

    public RotationUpdateListener(Transform transform) {
        super(transform);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        transform.setRotation((Quaternionf) animation.getAnimatedValue());
    }
}
