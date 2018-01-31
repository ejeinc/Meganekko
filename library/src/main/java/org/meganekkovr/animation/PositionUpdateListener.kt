package org.meganekkovr.animation

import android.animation.ValueAnimator

import org.joml.Vector3f
import org.meganekkovr.Entity

internal class PositionUpdateListener(target: Entity) : TransformUpdateListener(target) {

    override fun onAnimationUpdate(animation: ValueAnimator) {
        target.position = animation.animatedValue as Vector3f
    }
}
