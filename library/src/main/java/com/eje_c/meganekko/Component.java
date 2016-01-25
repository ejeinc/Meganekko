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

package com.eje_c.meganekko;

import com.eje_c.meganekko.utility.Exceptions;

/**
 * Base class for classes that can be attached to a {@link SceneObject scene
 * object}.
 */
class Component extends HybridObject {

    protected SceneObject owner;

    /**
     * @return The {@link SceneObject} this object is currently attached to.
     */
    protected SceneObject getOwnerObject() {
        if (owner != null) {
            return owner;
        }

        throw Exceptions.RuntimeAssertion("No Java owner: %s", getClass().getSimpleName());
    }

    protected void setOwnerObject(SceneObject owner) {
        this.owner = owner;
    }
}
