package org.meganekkovr.animation;

import android.animation.ValueAnimator;

import org.joml.Vector3f;
import org.meganekkovr.Entity;

class PositionUpdateListener extends TransformUpdateListener {

    public PositionUpdateListener(Entity target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        target.setPosition((Vector3f) animation.getAnimatedValue());
    }
}
