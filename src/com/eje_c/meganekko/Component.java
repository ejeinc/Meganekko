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

import java.util.List;

import com.eje_c.meganekko.utility.Exceptions;

/**
 * Base class for classes that can be attached to a {@link SceneObject scene
 * object}.
 */
class Component extends HybridObject {
    // private static final String TAG = Log.tag(GVRComponent.class);

    /**
     * Normal constructor
     * 
     * @param gvrContext
     *            The current GVRF context
     * @param nativePointer
     *            The native pointer, returned by the native constructor
     */
    protected Component(VrContext gvrContext, long ptr) {
        super(gvrContext, ptr);
    }

    /**
     * Special constructor, for descendants like {#link GVRMeshEyePointee} that
     * need to 'unregister' instances.
     * 
     * @param gvrContext
     *            The current GVRF context
     * @param nativePointer
     *            The native pointer, returned by the native constructor
     * @param cleanupHandlers
     *            Cleanup handler(s).
     * 
     *            <p>
     *            Normally, this will be a {@code private static} class
     *            constant, so that there is only one {@code List} per class.
     *            Descendants that supply a {@code List} and <em>also</em> have
     *            descendants that supply a {@code List} should use
     *            {@link CleanupHandlerListManager} to maintain a
     *            {@code Map<List<NativeCleanupHandler>, List<NativeCleanupHandler>>}
     *            whose keys are descendant lists and whose values are unique
     *            concatenated lists - see {@link EyePointeeHolder} for an
     *            example.
     */
    protected Component(VrContext gvrContext, long nativePointer,
            List<NativeCleanupHandler> cleanupHandlers) {
        super(gvrContext, nativePointer, cleanupHandlers);
    }

    protected SceneObject owner;

    /**
     * @return The {@link SceneObject} this object is currently attached to.
     */
    protected SceneObject getOwnerObject() {
        if (owner != null) {
            return owner;
        }

        throw Exceptions.RuntimeAssertion("No Java owner: %s", getClass()
                .getSimpleName());
    }

    protected void setOwnerObject(SceneObject owner) {
        this.owner = owner;
    }
}
