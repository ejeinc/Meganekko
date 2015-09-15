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

import java.util.ArrayList;
import java.util.List;

import com.eje_c.meganekko.FrameListener;
import com.eje_c.meganekko.GLContext;
import com.eje_c.meganekko.SceneObject;

/**
 * This class runs {@linkplain Animation animations}.
 * 
 * You can animate changes in just about any property of a
 * {@linkplain SceneObject scene object}.
 * 
 * <p>
 * The animation engine is an optional part of GVRF: to use it, you must call
 * {@link #getInstance(GLContext)} to lazy-create the singleton.
 * 
 * <p>
 * You can {@link #stop(Animation)} a running animation at any time, but
 * usually you will either
 * <ul>
 * 
 * <li>Use {@link Animation#setRepeatCount(int) setRepeatCount(0)} to
 * 'schedule' termination at the end of the current repetition, or
 * 
 * <li>{@linkplain Animation#setOnFinish(GVROnFinish) Set} a
 * {@linkplain OnRepeat callback,} which allows you to terminate the
 * animation before the next loop.
 * </ul>
 */
public class AnimationEngine {

    private static AnimationEngine sInstance = null;

    static {
        GLContext.addResetOnRestartHandler(new Runnable() {

            @Override
            public void run() {
                sInstance = null;
            }
        });
    }

    private final List<Animation> mAnimations = new ArrayList<Animation>();
    private final FrameListener mOnDrawFrame = new DrawFrame();
    private final GLContext gvrContext;

    protected AnimationEngine(GLContext gvrContext) {
        gvrContext.registerFrameListener(mOnDrawFrame);
        this.gvrContext = gvrContext;
    }

    /**
     * The animation engine is an optional part of GVRF: You do have to call
     * {@code getInstance()} to lazy-create the singleton.
     * 
     * @param gvrContext
     *            current GVR context
     */
    public static synchronized AnimationEngine getInstance(
            GLContext gvrContext) {
        if (sInstance == null) {
            sInstance = new AnimationEngine(gvrContext);
        }
        return sInstance;
    }

    /**
     * Registers an animation with the engine: It will start running
     * immediately.
     * 
     * You will usually use {@link Animation#start(AnimationEngine)}
     * instead of this method:
     * 
     * <pre>
     * 
     * new GVRSomeAnimation(object, duration, parameter)//
     *         .setOnFinish(handler)//
     *         .start(animationEngine);
     * </pre>
     * 
     * reads better than
     * 
     * <pre>
     * 
     * animationEngine.start(//
     *         new GVRSomeAnimation(object, duration, parameter)//
     *                 .setOnFinish(handler)//
     * );
     * </pre>
     * 
     * @param animation
     *            an animation
     * @return The animation that was passed in.
     */
    public Animation start(Animation animation) {
        if (animation.getRepeatCount() != 0) {
            synchronized (mAnimations) {
                mAnimations.add(animation);
            }
        }
        return animation;
    }

    /**
     * Stop the animation, even if it is still running: the animated object will
     * be left in its current state, not reset to the start or end values.
     * 
     * This is probably not what you want to do! Usually you will either
     * <ul>
     * <li>Use {@link Animation#setRepeatCount(int) setRepeatCount(0)} to
     * 'schedule' termination at the end of the current repetition, or
     * <li>{@linkplain Animation#setOnFinish(GVROnFinish) Set} a
     * {@linkplain OnRepeat callback,} which allows you to terminate the
     * animation before the next loop.
     * </ul>
     * You <em>may</em> want to {@code stop()} an animation if you are also
     * removing the animated object the same time. For example, you may be
     * spinning some sort of In Progress object. In a case like this, stopping
     * in mid-animation is harmless.
     * 
     * @param animation
     *            an animation
     */
    public void stop(Animation animation) {
        synchronized (mAnimations) {
            mAnimations.remove(animation);
        }
    }

    private final class DrawFrame implements FrameListener {

        @Override
        public void frame() {
            synchronized (mAnimations) {
                List<Animation> animations = new ArrayList<Animation>(
                        mAnimations);
                for (Animation animation : animations) {
                    if (animation.onDrawFrame(gvrContext.getActivity().getVrFrame().getDeltaSeconds()) == false) {
                        mAnimations.remove(animation);
                    }
                }
            }
        }
    }
}
