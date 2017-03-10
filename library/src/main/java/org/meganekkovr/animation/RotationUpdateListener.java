package org.meganekkovr.animation;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

import org.joml.Quaternionf;
import org.meganekkovr.Entity;

class RotationUpdateListener extends TransformUpdateListener {

    public RotationUpdateListener(@NonNull Entity target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
        target.setRotation((Quaternionf) animation.getAnimatedValue());
    }
}
