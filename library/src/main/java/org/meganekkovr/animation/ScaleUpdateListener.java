package org.meganekkovr.animation;

import android.animation.ValueAnimator;

import org.joml.Vector3f;
import org.meganekkovr.Entity;

class ScaleUpdateListener extends TransformUpdateListener {

    public ScaleUpdateListener(Entity target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        target.setScale((Vector3f) animation.getAnimatedValue());
    }
}
