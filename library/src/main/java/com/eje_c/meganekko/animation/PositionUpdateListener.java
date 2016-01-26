package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.Transform;

import org.joml.Vector3f;

public class PositionUpdateListener extends TransformUpdateListener {

    public PositionUpdateListener(Transform transform) {
        super(transform);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        transform.setPosition((Vector3f) animation.getAnimatedValue());
    }
}
