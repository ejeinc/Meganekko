package org.meganekkovr.animation

import android.animation.ValueAnimator

import org.meganekkovr.Entity

internal abstract class TransformUpdateListener(protected val target: Entity) : ValueAnimator.AnimatorUpdateListener
