package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.SceneObject;

import org.joml.Quaternionf;

public class RotationUpdateListener extends TransformUpdateListener {

    public RotationUpdateListener(SceneObject target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        target.rotation((Quaternionf) animation.getAnimatedValue());
    }
}
