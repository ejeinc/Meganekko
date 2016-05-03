package com.eje_c.meganekko.animation;

import android.animation.ValueAnimator;

import com.eje_c.meganekko.SceneObject;

public class MatrixUpdateListener extends TransformUpdateListener {

    public MatrixUpdateListener(SceneObject target) {
        super(target);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
//        target.setModelMatrix((Matrix4f) animation.getAnimatedValue());
    }
}
