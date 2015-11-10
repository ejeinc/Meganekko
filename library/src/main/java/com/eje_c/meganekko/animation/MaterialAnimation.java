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
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.SceneObject;

/**
 * Animate a {@link Material}.
 * 
 * The constructors cast their {@code target} parameter to a
 * {@code protected final Material mMaterial} field.
 */
@Deprecated
public abstract class MaterialAnimation extends Animation {

    private final static Class<?>[] SUPPORTED = { Material.class,
            SceneObject.class };

    protected final Material mMaterial;

    /**
     * Sets the {@code protected final Material mMaterial} field.
     *
     * @param target
     *            May be a {@link Material} or a {@link SceneObject} -
     *            does runtime checks.
     * @param duration
     *            The animation duration, in seconds.
     * @throws IllegalArgumentException
     *             If {@code target} is neither a {@link Material} nor a
     *             {@link SceneObject}
     * @deprecated Using this overload reduces 'constructor fan-out' and thus
     *             makes your life a bit easier - but at the cost of replacing
     *             compile-time type checking with run-time type checking, which
     *             is more expensive <em>and</em> can miss errors in code if you
     *             don't test every path through your code.
     */
    protected MaterialAnimation(HybridObject target, float duration) {
        super(target, duration);
        Class<?> type = checkTarget(target, SUPPORTED);
        if (type == Material.class) {
            mMaterial = (Material) target;
        } else {
            SceneObject sceneObject = (SceneObject) target;
            mMaterial = sceneObject.getRenderData().getMaterial();
        }
    }

    /**
     * 'Knows how' to get a material from a scene object - a bit smaller than
     * inline code, and protects you from any changes (however unlikely) in the
     * object hierarchy.
     */
    protected static Material getMaterial(SceneObject sceneObject) {
        return sceneObject.getRenderData().getMaterial();
    }

    /**
     * Sets the {@code protected final Material mMaterial} field without
     * doing any runtime checks.
     * 
     * @param target
     *            {@link Material} to animate.
     * @param duration
     *            The animation duration, in seconds.
     */
    protected MaterialAnimation(Material target, float duration) {
        super(target, duration);
        mMaterial = target;
    }

    /**
     * Sets the {@code protected final Material mMaterial} field without
     * doing any runtime checks.
     * 
     * <p>
     * This constructor is included to be orthogonal ;-) but you probably won't
     * use it, as most derived classes will have final fields of their own to
     * set. Rather than replicate the final field setting code, the best pattern
     * is to write a 'master' constructor, and call it <i>via</i>
     * {@code this(getMaterial(target), duration), ...);} - see
     * {@link OpacityAnimation#OpacityAnimation(SceneObject, float, float)}
     * for an example.
     * 
     * @param target
     *            {@link SceneObject} containing a {@link Material}
     * @param duration
     *            The animation duration, in seconds.
     */
    protected MaterialAnimation(SceneObject target, float duration) {
        super(target, duration);
        mMaterial = getMaterial(target);
    }
}
