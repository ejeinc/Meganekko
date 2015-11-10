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
import com.eje_c.meganekko.Transform;

/** Rotation animation. */
@Deprecated
public class RotationByAxisAnimation extends TransformAnimation {

    private final Orientation mOrientation;
    private final float mAngle, mX, mY, mZ;

    /**
     * Use {@link Transform#rotateByAxis(float, float, float, float)} to do
     * an animated rotation about a specific axis.
     * 
     * @param target
     *            {@link Transform} to animate.
     * @param duration
     *            The animation duration, in seconds.
     * @param angle
     *            the rotation angle, in degrees
     * @param x
     *            the normalized axis x component
     * @param y
     *            the normalized axis y component
     * @param z
     *            the normalized axis z component
     */
    public RotationByAxisAnimation(Transform target, float duration,
            float angle, float x, float y, float z) {
        super(target, duration);

        mOrientation = new Orientation();

        mAngle = angle;
        mX = x;
        mY = y;
        mZ = z;
    }

    /**
     * Use {@link Transform#rotateByAxis(float, float, float, float)} to do
     * an animated rotation about a specific axis.
     * 
     * @param target
     *            {@link SceneObject} containing a {@link Transform}
     * @param duration
     *            The animation duration, in seconds.
     * @param angle
     *            the rotation angle, in degrees
     * @param x
     *            the normalized axis x component
     * @param y
     *            the normalized axis y component
     * @param z
     *            the normalized axis z component
     */
    public RotationByAxisAnimation(SceneObject target, float duration,
            float angle, float x, float y, float z) {
        this(getTransform(target), duration, angle, x, y, z);
    }

    @Override
    protected void animate(HybridObject target, float ratio) {
        // Reset rotation (this is pretty cheap - Meganekko uses a 'lazy update'
        // policy on the matrix, so two changes don't cost all that much more
        // than one)
        mOrientation.setOrientation();

        // Rotate from start
        float angle = ratio * mAngle;
        mTransform.rotateByAxis(angle, mX, mY, mZ);
    }
}
