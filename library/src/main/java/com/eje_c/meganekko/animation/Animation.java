/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko.animation;

import android.graphics.Color;
import android.view.animation.Interpolator;

import com.eje_c.meganekko.HybridObject;
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.SceneObject;

/**
 * The root of the animation tree.
 * <p/>
 * This class (and the {@linkplain AnimationEngine engine}) supply the common
 * functionality.
 * <p/>
 * <p/>
 * All animations have at least three or more required parameters: the object to
 * animate, the duration, and any animation parameters. In many cases, there are
 * overloaded methods to specify the animation parameters in convenient ways:
 * for example, you can specify a color as an Android {@link Color} or as a
 * {@code float[3]} of GL-compatible 0 to 1 values. In addition, all the stock
 * animations that animate a type like, say, {@link Material} 'know how' to
 * find the {@link Material} inside a {@link SceneObject}.
 * <p/>
 * <p/>
 * This means that most animations have two or four overloaded constructors.
 * This is trouble for the animation developer - who must keep four sets of
 * JavaDoc in sync - but probably clear enough for the animation user. However,
 * there are also four optional parameters: the interpolator, the repeat type,
 * the repeat count, and an on-finished callback. Adding these to an overload
 * tree would, well, overload both developers and users!
 * <p/>
 * <p/>
 * Thus, animations use a sort of Builder Pattern: you set the optional
 * parameters <i>via</i> set() methods that return {@code this}, so you can
 * chain them like
 * <p/>
 * <pre>
 *
 * new ScaleAnimation(sceneObject, 1.5f, 2f) //
 *         .setRepeatMode(RepeatMode.PINGPONG) //
 *         .start({@linkplain AnimationEngine animationEngine});
 * </pre>
 * <p/>
 * which will 'pulse' the size of the {@code sceneObject} from its current level
 * to double size, and back.
 * <p/>
 * If you start multiple animations (with the same duration and the same
 * interpolator!) in the same time, they will always be in
 * sync. That is, you can 'compose' animations simply by starting them together;
 * you do not need to write a composite animation that animates multiple
 * properties in a single method.
 */
@Deprecated
public abstract class Animation {

    /**
     * The default repeat count only applies to the two repeat modes, not to the
     * default run-once mode.
     * <p/>
     * The default repeat count is 2, so that a ping pong animation will return
     * to the start state, even if you don't
     * {@linkplain Animation#setRepeatCount(int) setRepeatCount(2).}
     */
    public static final int DEFAULT_REPEAT_COUNT = 2;

    // Immutable values, passed to constructor
    private final HybridObject mTarget;
    private final float mDuration;

    // Defaulted values, which should be set before start()
    private Interpolator mInterpolator = null;
    private int mRepeatMode = RepeatMode.ONCE;
    private int mRepeatCount = DEFAULT_REPEAT_COUNT;
    private OnFinish mOnFinish = null;

    /**
     * This is derived from {@link #mOnFinish}. Doing the {@code instanceof}
     * test in {@link #setOnFinish(OnFinish)} means we <em>don't</em> have to
     * do it on every call, in {@link #onDrawFrame(float)}
     */
    private OnRepeat mOnRepeat = null;

    // Running state
    private float mElapsedTime = 0f;
    private int mIterations = 0;

    /**
     * Base constructor.
     * <p/>
     * Sets required fields, initializes optional fields to default values.
     *
     * @param target   The object to animate. Note that this constructor makes a
     *                 <em>private<em> copy of the {@code target}
     *                 parameter: It is up to descendant classes to cast the
     *                 {@code target} to the expected type and save the typed value.
     * @param duration The animation duration, in seconds.
     */
    protected Animation(HybridObject target, float duration) {
        mTarget = target;
        mDuration = duration;
    }

    /**
     * Many animations can take multiple target types: for example,
     * {@link MaterialAnimation material animations} can work directly with
     * {@link Material} targets, but also 'know how' to get a
     * {@link Material} from a {@link SceneObject}. They can, of course,
     * just expose multiple constructors, but that makes for a combinatorial
     * explosion when the other parameters also 'want' to be overloaded. This
     * method allows them to just take a {@link HybridObject} and throw an
     * exception if they get a type they can't handle; it also returns the
     * matched type (which may not be equal to {@code target.getClass()}) so
     * that calling code doesn't have to do {@code instanceof} tests.
     *
     * @param target    A {@link HybridObject} instance
     * @param supported An array of supported types
     * @return The element of {@code supported} that matched
     * @throws IllegalArgumentException If {@code target} is not an instance of any of the
     *                                  {@code supported} types
     */
    protected static Class<?> checkTarget(HybridObject target,
                                          Class<?>... supported) {
        for (Class<?> type : supported) {
            if (type.isInstance(target)) {
                return type;
            }
        }
        // else
        throw new IllegalArgumentException();
    }

    /**
     * Set the interpolator.
     * <p/>
     * By default, animations proceed linearly: at X percent of the duration,
     * the animated property will be at X percent of the way from the start
     * state to the stop state. Specifying an explicit interpolator lets the
     * animation do other things, like accelerate and decelerate, overshoot,
     * bounce, and so on.
     *
     * @param interpolator An interpolator instance. {@code null} gives you the default,
     *                     linear animation.
     * @return {@code this}, so you can chain setProperty() calls.
     */
    public Animation setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        return this;
    }

    /**
     * Set the repeat type.
     * <p/>
     * In the default {@linkplain RepeatMode#ONCE run-once} mode, animations
     * run once, ignoring the {@linkplain #getRepeatCount() repeat count.} In
     * {@linkplain RepeatMode#PINGPONG ping pong} and
     * {@linkplain RepeatMode#REPEATED repeated} modes, animations do honor
     * the repeat count, which {@linkplain #DEFAULT_REPEAT_COUNT defaults} to 2.
     *
     * @param repeatMode One of the {@link RepeatMode} constants
     * @return {@code this}, so you can chain setProperty() calls.
     * @throws IllegalArgumentException If {@code repetitionType} is not one of the
     *                                  {@link RepeatMode} constants
     */
    public Animation setRepeatMode(int repeatMode) {
        if (RepeatMode.invalidRepeatMode(repeatMode)) {
            throw new IllegalArgumentException(repeatMode
                    + " is not a valid repetition type");
        }
        mRepeatMode = repeatMode;
        return this;
    }

    /**
     * Set the on-finish callback.
     * <p/>
     * The basic {@link OnFinish} callback will notify you when the animation
     * runs to completion. This is a good time to do things like removing
     * now-invisible objects from the scene graph.
     * <p/>
     * <p/>
     * The extended {@link OnRepeat} callback will be called after every
     * iteration of an indefinite (repeat count less than 0) animation, giving
     * you a way to stop the animation when it's not longer appropriate.
     *
     * @param callback A {@link OnFinish} or {@link OnRepeat} implementation.
     *                 <p/>
     *                 <em>Note</em>: Supplying a {@link OnRepeat} callback will
     *                 {@linkplain #setRepeatCount(int) set the repeat count} to a
     *                 negative number. Calling {@link #setRepeatCount(int)} with a
     *                 non-negative value after setting a {@link OnRepeat}
     *                 callback will effectively convert the callback to a
     *                 {@link OnFinish}.
     * @return {@code this}, so you can chain setProperty() calls.
     */
    public Animation setOnFinish(OnFinish callback) {
        mOnFinish = callback;

        // Do the instance-of test at set-time, not at use-time
        mOnRepeat = callback instanceof OnRepeat ? (OnRepeat) callback
                : null;
        if (mOnRepeat != null) {
            mRepeatCount = -1; // loop until iterate() returns false
        }

        return this;
    }

    /**
     * Start the animation.
     * <p/>
     * Changing properties once the animation is running can have unpredictable
     * results.
     * <p/>
     * <p/>
     * This method is exactly equivalent to
     * {@link AnimationEngine#start(Animation)} and is provided as a
     * convenience so you can write code like
     * <p/>
     * <pre>
     *
     * Animation animation = new AnimationDescendant(target, duration)
     *         .setOnFinish(callback).start(animationEngine);
     * </pre>
     * <p/>
     * instead of
     * <p/>
     * <pre>
     *
     * Animation animation = new AnimationDescendant(target, duration)
     *         .setOnFinish(callback);
     * animationEngine.start(animation);
     * </pre>
     *
     * @return {@code this}, so you can save the instance at the end of a chain
     * of calls
     */
    public Animation start(AnimationEngine engine) {
        engine.start(this);
        return this;
    }

    /**
     * Start the animation.
     * <p/>
     * Changing properties once the animation is running can have unpredictable
     * results.
     * <p/>
     * <p/>
     * This method is exactly equivalent to
     * {@link AnimationEngine#start(vrContext.getAnimationEngine())} and is provided as a convenience
     * so you can write code like
     * <p/>
     * <pre>
     *
     * Animation animation = new AnimationDescendant(target, duration)
     *         .setOnFinish(callback).start();
     * </pre>
     * <p/>
     * instead of
     * <p/>
     * <pre>
     *
     * Animation animation = new AnimationDescendant(target, duration)
     *         .setOnFinish(callback);
     * animationEngine.start(animation);
     * </pre>
     *
     * @return {@code this}, so you can save the instance at the end of a chain
     * of calls
     */
    public Animation start() {
        return start(mTarget.getVrContext().getAnimationEngine());
    }

    /**
     * Called by the animation engine. Uses the frame time, the interpolator,
     * and the repeat mode to generate a call to
     * {@link #animate(HybridObject, float)}.
     *
     * @param frameTime elapsed time since the previous animation frame, in seconds
     * @return {@code true} to keep running the animation; {@code false} to shut
     * it down
     */
    final boolean onDrawFrame(float frameTime) {
        final int previousCycleCount = (int) (mElapsedTime / mDuration);

        mElapsedTime += frameTime;

        final int currentCycleCount = (int) (mElapsedTime / mDuration);
        final float cycleTime = mElapsedTime % mDuration;

        final boolean cycled = previousCycleCount != currentCycleCount;
        boolean stillRunning = cycled != true;

        if (cycled && mRepeatMode != RepeatMode.ONCE) {
            // End of a cycle - see if we should continue
            mIterations += 1;
            if (mOnFinish != null && mOnRepeat == null) {
                mOnFinish.finished(this);
            }
            if (mRepeatCount == 0) {
                stillRunning = false; // last pass
            } else if (mRepeatCount > 0) {
                stillRunning = --mRepeatCount > 0;
            } else {
                // Negative repeat count - call mOnRepeat, if we can
                if (mOnRepeat != null) {
                    stillRunning = mOnRepeat.iteration(this, mIterations);
                } else {
                    stillRunning = true; // repeat indefinitely
                }
            }
        }

        if (stillRunning) {
            final boolean countDown = mRepeatMode == RepeatMode.PINGPONG
                    && (mIterations & 1) == 1;
            float elapsedRatio = //
                    countDown != true ? interpolate(cycleTime, mDuration)
                            : interpolate(mDuration - cycleTime, mDuration);

            animate(mTarget, elapsedRatio);
        } else {
            float endRatio = mRepeatMode == RepeatMode.ONCE ? 1f : 0f;

            animate(mTarget, endRatio);

            if (mOnFinish != null) {
                mOnFinish.finished(this);
            }
        }

        return stillRunning;
    }

    private float interpolate(float cycleTime, float duration) {
        float ratio = cycleTime / duration;
        return mInterpolator == null ? ratio : mInterpolator.getInterpolation(ratio);
    }

    /**
     * Checks whether the animation has run to completion.
     * <p/>
     * For {@linkplain RepeatMode#ONCE run-once} animations, this means only
     * that the animation has timed-out: generally, this means that the
     * (optional) onFinish callback has been invoked and the animation
     * 'unregistered' by the {@linkplain AnimationEngine animation engine}
     * but it's not impossible that there is some lag between time-out and
     * finalization.
     * <p/>
     * <p/>
     * For {@linkplain RepeatMode#REPEATED repeated} or
     * {@linkplain RepeatMode#PINGPONG ping pong} animations, this method can
     * tell you whether an animation is on its first iteration or one of the
     * repetitions. If you need to, you can terminate a repetitive animation
     * 'abruptly' by calling {@linkplain AnimationEngine#stop(Animation)}
     * or 'cleanly' by calling {@link #setRepeatCount(int) setRepeatCount(0).}
     * Do note that both these approaches are sort of 'legacy' - the clean way
     * to handle indeterminate animations is to use
     * {@link #setOnFinish(OnFinish)} to set an {@linkplain OnRepeat}
     * handler, before calling {@link #start(AnimationEngine)}.
     *
     * @return {@code true} if done or repeating; {@code false} if on first run.
     */
    public final boolean isFinished() {
        return mElapsedTime >= mDuration;
    }

    /**
     * Get the current repeat count.
     * <p/>
     * A negative number means the animation will repeat indefinitely; zero
     * means the animation will stop after the current cycle; a positive number
     * is the number of cycles after the current cycle.
     *
     * @return The current repeat count
     */
    public int getRepeatCount() {
        return mRepeatCount;
    }

    /**
     * Set the repeat count.
     *
     * @param repeatCount <table border="none">
     *                    <tr>
     *                    <td width="15%">A negative number</td>
     *                    <td>Means the animation will repeat indefinitely. See the
     *                    notes on {@linkplain OnFinish#finished(Animation)
     *                    stopping an animation.}</td>
     *                    </tr>
     *                    <p/>
     *                    <tr>
     *                    <td>0</td>
     *                    <td>After {@link #start(AnimationEngine) start()}, 0 means
     *                    'stop at the end' and schedules a clean shutdown. Calling
     *                    {@code setRepeatCount(0)} <em>before</em> {@code start()} is
     *                    really pointless and silly ... but {@code start()} is
     *                    special-cased so setting the repeat count to 0 will do what
     *                    you expect.</td>
     *                    </tr>
     *                    <p/>
     *                    <tr>
     *                    <td>A positive number</td>
     *                    <td>Specifies a repeat count</td>
     *                    </tr>
     *                    </table>
     * @return {@code this}, so you can chain setProperty() calls.
     */
    public Animation setRepeatCount(int repeatCount) {
        mRepeatCount = repeatCount;
        return this;
    }

    /**
     * The duration passed to {@linkplain #Animation(HybridObject, float)
     * the constructor.}
     * <p/>
     * This may be useful if you have to, say, 'undo' a running animation.
     *
     * @return The duration passed to the constructor.
     */
    public float getDuration() {
        return mDuration;
    }

    /**
     * How long the animation has been running.
     * <p/>
     * This may be useful if you have to, say, 'undo' a running animation. With
     * {@linkplain #getRepeatCount() repeated animations,} this may be longer
     * than the {@linkplain #getDuration() duration.}
     *
     * @return How long the animation has been running.
     */
    public float getElapsedTime() {
        return mElapsedTime;
    }

    /**
     * Override this to create a new animation. Generally, you do this by
     * changing some property of the {@code mTarget}, and letting Meganekko handle
     * screen updates automatically.
     *
     * @param target The Meganekko object to animate
     * @param ratio  The start state is 0; the stop state is 1.
     */
    protected abstract void animate(HybridObject target, float ratio);
}
