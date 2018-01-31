package org.meganekkovr.animation

import android.animation.ValueAnimator

import org.joml.Quaternionf
import org.meganekkovr.Entity

internal class RotationUpdateListener(target: Entity) : TransformUpdateListener(target) {

    override fun onAnimationUpdate(animation: ValueAnimator) {
        target.rotation = animation.animatedValue as Quaternionf
    }
}
