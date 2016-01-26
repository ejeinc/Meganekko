package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.Transform;

public abstract class TransformUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    protected final Transform transform;

    public TransformUpdateListener(Transform transform) {
        this.transform = transform;
    }
}
