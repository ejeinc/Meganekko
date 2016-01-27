/*
 * Copyright 2015 eje inc.
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

package com.eje_c.meganekko;

import android.support.annotation.NonNull;

/**
 * Detects if user is looking at target object automatically.
 * The {@link ObjectLookingStateDetector.ObjectLookingStateListener} callback will notify users
 * when start or end looking at target object. To use this class:
 * <ul>
 * <li>Create an instance of the {@code ObjectLookingStateDetector} with {@code ObjectLookingStateListener}.</li>
 * <li>Call {@link ObjectLookingStateDetector#update(VrFrame)} in {@link MeganekkoApp#update(Meganekko, VrFrame)} or {@link Scene#update(VrFrame)}.</li>
 * </ul>
 */
public class ObjectLookingStateDetector {

    private final SceneObject mTarget;
    private final ObjectLookingStateListener mListener;
    private final Meganekko mMeganekko;
    private boolean mLooking;

    /**
     * Creates a ObjectLookingStateDetector with the supplied listener.
     *
     * @param target   the target object.
     * @param listener the listener invoked for all callbacks, this must not be null.
     */
    public ObjectLookingStateDetector(@NonNull Meganekko meganekko, @NonNull SceneObject target, @NonNull ObjectLookingStateListener listener) {
        this.mTarget = target;
        this.mListener = listener;
        this.mMeganekko = meganekko;
    }

    public void update(VrFrame vrFrame) {

        if (!mTarget.isShown()) {
            if (mLooking) {
                mListener.onLookEnd(mTarget, vrFrame);
            }
            return;
        }

        boolean lookingNow = mMeganekko.isLookingAt(mTarget);

        if (lookingNow) {
            if (!mLooking) {
                mListener.onLookStart(mTarget, vrFrame);
            } else {
                mListener.onLooking(mTarget, vrFrame);
            }
        } else {
            if (mLooking) {
                mListener.onLookEnd(mTarget, vrFrame);
            }
        }

        mLooking = lookingNow;
    }

    /**
     * Get the target object.
     *
     * @return the target object passed to constructor.
     */
    public SceneObject getTarget() {
        return mTarget;
    }

    /**
     * The listener that is used to notify when user is looking at target object.
     */
    public interface ObjectLookingStateListener {

        /**
         * Notified when user starts looking at target object.
         *
         * @param targetObject
         * @param vrFrame
         */
        void onLookStart(SceneObject targetObject, VrFrame vrFrame);

        /**
         * Notified when user is still looking at target object.
         *
         * @param targetObject
         * @param vrFrame
         */
        void onLooking(SceneObject targetObject, VrFrame vrFrame);

        /**
         * Notified when user ends looking at target object.
         *
         * @param targetObject
         * @param vrFrame
         */
        void onLookEnd(SceneObject targetObject, VrFrame vrFrame);
    }
}
