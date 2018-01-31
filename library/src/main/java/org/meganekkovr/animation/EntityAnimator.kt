package org.meganekkovr.animation

import android.animation.*
import org.joml.Quaternionf
import org.joml.Vector3f
import org.meganekkovr.Entity

class EntityAnimator(private val target: Entity) {
    private val animators = mutableListOf<Animator>()
    private var callback: (() -> Unit)? = null
    private var duration: Long = -1
    private var interpolator: TimeInterpolator? = null
    private var sequential: Boolean = false
    private var delay: Long = 0
    private var animator: AnimatorSet? = null

    // For sequential animation
    private var lastPos: Vector3f? = null
    private var lastScale: Vector3f? = null
    private var lastRotation: Quaternionf? = null
    private var lastOpacity = target.opacity

    fun moveTo(position: Vector3f): EntityAnimator {
        val fromPos = lastPos ?: target.position
        val animator = ValueAnimator.ofObject(VectorEvaluator(), Vector3f(fromPos), Vector3f(position))
        animator.addUpdateListener(PositionUpdateListener(target))
        animators.add(animator)
        lastPos = position
        return this
    }

    fun moveBy(translation: Vector3f): EntityAnimator {
        val fromPos = lastPos ?: target.position
        val toPosition = Vector3f()
        fromPos.add(translation, toPosition)
        return moveTo(toPosition)
    }

    fun scaleTo(scale: Vector3f): EntityAnimator {
        val fromScale = lastScale ?: target.scale
        val animator = ValueAnimator.ofObject(VectorEvaluator(), Vector3f(fromScale), Vector3f(scale))
        animator.addUpdateListener(ScaleUpdateListener(target))
        animators.add(animator)
        lastScale = scale
        return this
    }

    fun scaleBy(scale: Vector3f): EntityAnimator {
        val fromScale = lastScale ?: target.scale
        val toScale = Vector3f()
        fromScale.mul(scale, toScale)
        return scaleTo(toScale)
    }

    fun rotateTo(rotation: Quaternionf): EntityAnimator {
        val fromRotation = lastRotation ?: target.rotation
        val animator = ValueAnimator.ofObject(QuaternionEvaluator(), Quaternionf(fromRotation), Quaternionf(rotation))
        animator.addUpdateListener(RotationUpdateListener(target))
        animators.add(animator)
        lastRotation = rotation
        return this
    }

    fun rotateTo(x: Float, y: Float, z: Float): EntityAnimator {
        val q = Quaternionf()
        q.rotate(x, y, z)
        return rotateTo(q)
    }

    fun rotateBy(rotate: Quaternionf): EntityAnimator {
        val fromRotation = lastRotation ?: target.rotation
        val toRotation = Quaternionf()
        fromRotation.mul(rotate, toRotation)
        return rotateTo(toRotation)
    }

    fun rotateBy(x: Float, y: Float, z: Float): EntityAnimator {
        val q = Quaternionf()
        q.rotate(x, y, z)
        return rotateBy(q)
    }

    /**
     * This method will cause the Entity's opacity property to be animated to the specified value.
     *
     * @param opacity The value to be animated to.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    fun opacity(opacity: Float): EntityAnimator {
        val animator = ObjectAnimator.ofFloat(target, "opacity", lastOpacity, opacity)
        animators.add(animator)
        lastOpacity = opacity
        return this
    }

    /**
     * Set callback for animation.
     *
     * @param callback Action which will be called after animation.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    fun onEnd(callback: (() -> Unit)?): EntityAnimator {
        this.callback = callback
        return this
    }

    /**
     * Sets the interpolator for the underlying animator that animates the requested properties.
     * By default, the animator uses the default interpolator for ValueAnimator.
     * Calling this method will cause the declared object to be used instead.
     *
     * @param interpolator The TimeInterpolator to be used for ensuing property animations. A value of null will result in linear interpolation.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    fun interpolator(interpolator: TimeInterpolator?): EntityAnimator {
        this.interpolator = interpolator
        return this
    }

    /**
     * Sets the duration for the underlying animator that animates the requested properties.
     * By default, the animator uses the default value for ValueAnimator.
     * Calling this method will cause the declared value to be used instead.
     *
     * @param duration The length of ensuing property animations, in milliseconds. The value cannot be negative.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    fun duration(duration: Long): EntityAnimator {
        this.duration = duration
        return this
    }

    /**
     * @param sequential true to sequential animation.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    fun sequential(sequential: Boolean): EntityAnimator {
        this.sequential = sequential
        return this
    }

    /**
     * Sets the startDelay for the underlying animator that animates the requested properties.
     *
     * @param delay The delay of ensuing property animations, in milliseconds. The value cannot be negative.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    fun delay(delay: Long): EntityAnimator {
        this.delay = delay
        return this
    }

    /**
     * Starts animation.
     */
    fun start() {

        if (animator == null) {
            setupAnimator()
        }

        target.app!!.animate(animator!!, callback)
    }

    /**
     * Cancels all property animations that are currently running or pending.
     */
    fun cancel() {

        if (animator == null) return

        target.app!!.cancel(animator!!, null)
    }

    private fun setupAnimator() {
        this.animator = AnimatorSet()

        if (sequential) {
            animator!!.playSequentially(animators)
        } else {
            animator!!.playTogether(animators)
        }

        if (duration >= 0) {
            animator!!.duration = duration
        }

        if (interpolator != null) {
            animator!!.interpolator = interpolator
        }

        if (delay > 0) {
            animator!!.startDelay = delay
        }
    }

    /**
     * Get `Animator`.
     *
     * @return Animator
     */
    fun getAnimator(): Animator {
        if (animator == null) {
            setupAnimator()
        }
        return animator!!
    }
}
