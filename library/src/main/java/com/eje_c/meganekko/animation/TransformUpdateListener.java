package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.SceneObject;

public abstract class TransformUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    protected final SceneObject target;

    public TransformUpdateListener(SceneObject target) {
        this.target = target;
    }
}
