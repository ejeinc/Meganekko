package org.meganekkovr.animation;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

import org.meganekkovr.Entity;

abstract class TransformUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    protected final Entity target;

    public TransformUpdateListener(@NonNull Entity target) {
        this.target = target;
    }
}
