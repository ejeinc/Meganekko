package org.meganekkovr.animation;

import android.animation.ValueAnimator;

import org.meganekkovr.Entity;

abstract class TransformUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    protected final Entity target;

    public TransformUpdateListener(Entity target) {
        this.target = target;
    }
}
