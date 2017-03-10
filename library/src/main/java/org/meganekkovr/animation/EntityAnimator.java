package org.meganekkovr.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.meganekkovr.Entity;

import java.util.ArrayList;
import java.util.List;

public class EntityAnimator {

    private final Entity target;
    private final List<Animator> animators = new ArrayList<>();
    private Runnable callback;
    private long duration = -1;
    private TimeInterpolator interpolator;
    private boolean sequential;
    private long delay;
    private AnimatorSet animator;

    // For sequential animation
    private Vector3f lastPos, lastScale;
    private Quaternionf lastRotation;
    private float lastOpacity;

    public EntityAnimator(@NonNull Entity target) {
        this.target = target;
        this.lastOpacity = target.getOpacity();
    }

    @NonNull
    public EntityAnimator moveTo(@NonNull Vector3f position) {
        Vector3f fromPos = lastPos != null ? lastPos : target.getPosition();
        ValueAnimator animator = ValueAnimator.ofObject(new VectorEvaluator(), new Vector3f(fromPos), new Vector3f(position));
        animator.addUpdateListener(new PositionUpdateListener(target));
        animators.add(animator);
        lastPos = position;
        return this;
    }

    @NonNull
    public EntityAnimator moveBy(@NonNull Vector3f translation) {
        Vector3f fromPos = lastPos != null ? lastPos : target.getPosition();
        Vector3f toPosition = new Vector3f();
        fromPos.add(translation, toPosition);
        return moveTo(toPosition);
    }

    @NonNull
    public EntityAnimator scaleTo(@NonNull Vector3f scale) {
        Vector3f fromScale = lastScale != null ? lastScale : target.getScale();
        ValueAnimator animator = ValueAnimator.ofObject(new VectorEvaluator(), new Vector3f(fromScale), new Vector3f(scale));
        animator.addUpdateListener(new ScaleUpdateListener(target));
        animators.add(animator);
        lastScale = scale;
        return this;
    }

    @NonNull
    public EntityAnimator scaleBy(@NonNull Vector3f scale) {
        Vector3f fromScale = lastScale != null ? lastScale : target.getScale();
        Vector3f toScale = new Vector3f();
        fromScale.mul(scale, toScale);
        return scaleTo(toScale);
    }

    @NonNull
    public EntityAnimator rotateTo(@NonNull Quaternionf rotation) {
        Quaternionf fromRotation = lastRotation != null ? lastRotation : target.getRotation();
        ValueAnimator animator = ValueAnimator.ofObject(new QuaternionEvaluator(), new Quaternionf(fromRotation), new Quaternionf(rotation));
        animator.addUpdateListener(new RotationUpdateListener(target));
        animators.add(animator);
        lastRotation = rotation;
        return this;
    }

    @NonNull
    public EntityAnimator rotateTo(float x, float y, float z) {
        Quaternionf q = new Quaternionf();
        q.rotate(x, y, z);
        return rotateTo(q);
    }

    @NonNull
    public EntityAnimator rotateBy(@NonNull Quaternionf rotate) {
        Quaternionf fromRotation = lastRotation != null ? lastRotation : target.getRotation();
        Quaternionf toRotation = new Quaternionf();
        fromRotation.mul(rotate, toRotation);
        return rotateTo(toRotation);
    }

    @NonNull
    public EntityAnimator rotateBy(float x, float y, float z) {
        Quaternionf q = new Quaternionf();
        q.rotate(x, y, z);
        return rotateBy(q);
    }

    /**
     * This method will cause the Entity's opacity property to be animated to the specified value.
     *
     * @param opacity The value to be animated to.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @NonNull
    public EntityAnimator opacity(float opacity) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "opacity", lastOpacity, opacity);
        animators.add(animator);
        lastOpacity = opacity;
        return this;
    }

    /**
     * Set callback for animation.
     *
     * @param callback Action which will be called after animation.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @NonNull
    public EntityAnimator onEnd(@Nullable Runnable callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Sets the interpolator for the underlying animator that animates the requested properties.
     * By default, the animator uses the default interpolator for ValueAnimator.
     * Calling this method will cause the declared object to be used instead.
     *
     * @param interpolator The TimeInterpolator to be used for ensuing property animations. A value of null will result in linear interpolation.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @NonNull
    public EntityAnimator interpolator(@Nullable TimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    /**
     * Sets the duration for the underlying animator that animates the requested properties.
     * By default, the animator uses the default value for ValueAnimator.
     * Calling this method will cause the declared value to be used instead.
     *
     * @param duration The length of ensuing property animations, in milliseconds. The value cannot be negative.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @NonNull
    public EntityAnimator duration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * @param sequential true to sequential animation.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @NonNull
    public EntityAnimator sequential(boolean sequential) {
        this.sequential = sequential;
        return this;
    }

    /**
     * Sets the startDelay for the underlying animator that animates the requested properties.
     *
     * @param delay The delay of ensuing property animations, in milliseconds. The value cannot be negative.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @NonNull
    public EntityAnimator delay(long delay) {
        this.delay = delay;
        return this;
    }

    /**
     * Starts animation.
     */
    public void start() {

        if (animator == null) {
            setupAnimator();
        }

        target.getApp().animate(animator, callback);
    }

    /**
     * Cancels all property animations that are currently running or pending.
     */
    public void cancel() {

        if (animator == null) return;

        target.getApp().cancel(animator, null);
    }

    private void setupAnimator() {
        this.animator = new AnimatorSet();

        if (sequential) {
            animator.playSequentially(animators);
        } else {
            animator.playTogether(animators);
        }

        if (duration >= 0) {
            animator.setDuration(duration);
        }

        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }

        if (delay > 0) {
            animator.setStartDelay(delay);
        }
    }

    /**
     * Get {@code Animator}.
     *
     * @return Animator
     */
    @NonNull
    public Animator getAnimator() {
        if (animator == null) {
            setupAnimator();
        }
        return animator;
    }
}
