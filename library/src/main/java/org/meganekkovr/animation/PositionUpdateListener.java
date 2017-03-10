package org.meganekkovr.animation;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

import org.joml.Vector3f;
import org.meganekkovr.Entity;

class PositionUpdateListener extends TransformUpdateListener {

    public PositionUpdateListener(@NonNull Entity target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
        target.setPosition((Vector3f) animation.getAnimatedValue());
    }
}
