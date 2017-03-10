package org.meganekkovr.animation;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

import org.joml.Vector3f;
import org.meganekkovr.Entity;

class ScaleUpdateListener extends TransformUpdateListener {

    public ScaleUpdateListener(@NonNull Entity target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
        target.setScale((Vector3f) animation.getAnimatedValue());
    }
}
