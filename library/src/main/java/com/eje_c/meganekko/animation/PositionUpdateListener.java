package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.SceneObject;

import org.joml.Vector3f;

public class PositionUpdateListener extends TransformUpdateListener {

    public PositionUpdateListener(SceneObject target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        target.position((Vector3f) animation.getAnimatedValue());
    }
}
