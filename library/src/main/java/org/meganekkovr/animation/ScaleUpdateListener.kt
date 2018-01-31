package org.meganekkovr.animation

import android.animation.ValueAnimator

import org.joml.Vector3f
import org.meganekkovr.Entity

internal class ScaleUpdateListener(target: Entity) : TransformUpdateListener(target) {

    override fun onAnimationUpdate(animation: ValueAnimator) {
        target.scale = animation.animatedValue as Vector3f
    }
}
