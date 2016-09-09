package org.meganekkovr.animation;

import android.animation.ValueAnimator;

import org.joml.Quaternionf;
import org.meganekkovr.Entity;

class RotationUpdateListener extends TransformUpdateListener {

    public RotationUpdateListener(Entity target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        target.setRotation((Quaternionf) animation.getAnimatedValue());
    }
}
