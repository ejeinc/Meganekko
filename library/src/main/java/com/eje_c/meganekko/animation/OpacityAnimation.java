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

import com.eje_c.meganekko.HybridObject;
import com.eje_c.meganekko.SceneObject;

/** Animate the opacity. */
@Deprecated
public class OpacityAnimation extends Animation {

    private final float mInitialOpacity;
    private final float mDeltaOpacity;

    /**
     * Animate the {@link SceneObject#setOpacity(float) opacity} property.
     * 
     * @param target
     *            {@link SceneObject} to animate.
     * @param duration
     *            The animation duration, in seconds.
     * @param opacity
     *            A value from 0 to 1
     */
    public OpacityAnimation(SceneObject target, float duration, float opacity) {
        super(target, duration);
        mInitialOpacity = target.getOpacity();
        mDeltaOpacity = opacity - mInitialOpacity;
    }

    @Override
    protected void animate(HybridObject target, float ratio) {
        ((SceneObject) target).setOpacity(mInitialOpacity + mDeltaOpacity * ratio);
    }
}
