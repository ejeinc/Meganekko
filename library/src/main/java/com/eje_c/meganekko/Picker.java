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

import com.eje_c.meganekko.utility.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Finds the scene objects you are pointing to.
 * <p/>
 * For a {@linkplain SceneObject scene object} to be pickable, it must have a
 * {@link EyePointeeHolder}
 * {@link SceneObject#attachEyePointeeHolder(EyePointeeHolder) attached}
 * and {@linkplain EyePointeeHolder#setEnable(boolean) enabled.}
 * <p/>
 * This picker "casts" a ray into the screen graph, and returns each enabled
 * {@link EyePointeeHolder} that the ray hits, sorted by distance from the
 * camera. Use {@link EyePointeeHolder#getOwnerObject()} to map the eye
 * pointee holder to a scene object.
 */
public class Picker {
    private static final String TAG = Log.tag(Picker.class);

    private Picker() {
    }

    /**
     * Casts a ray into the scene graph, and returns the objects it intersects.
     * <p/>
     * The ray is defined by its origin {@code [ox, oy, oz]} and its direction
     * {@code [dx, dy, dz]}.
     * <p/>
     * The ray origin may be [0, 0, 0] and the direction components should be
     * normalized from -1 to 1: Note that the y direction runs from -1 at the
     * bottom to 1 at the top. To construct a picking ray originating at the
     * user's head and pointing into the scene along the camera lookat vector,
     * pass in 0, 0, 0 for the origin and 0, 0, -1 for the direction.
     * <p/>
     * <em>Note:</em> The {@linkplain EyePointeeHolder#getHit() hit location}
     * is stored in the native eye pointee holder during the ray casting
     * operation: <em>It is only valid until the next ray cast operation.</em>
     * That is, either the {@linkplain #pickScene(Scene) short pickScene()},
     * or the
     * {@linkplain #pickScene(Scene, float, float, float, float, float, float)
     * long pickScene()}, or
     * {@linkplain #findObjects(Scene, float, float, float, float, float, float)
     * findObjects()} calls will invalidate previous hit data. There are two
     * ways to avoid getting invalid hit data:
     * <ul>
     * <li>Use the high-level
     * {@linkplain #findObjects(Scene, float, float, float, float, float, float)
     * findObjects()} method, which returns a list of {@link PickedObject}:
     * each picked object pairs a {@link SceneObject} with the hit data.
     * <li>Write your code so that you never call ray casting operation until
     * you have retrieved the previous operations hit data. (This is easy, if
     * you only ever do picking from the GL thread. It's significantly harder if
     * you are using multiple threads.)
     * </ul>
     *
     * @param scene The {@link Scene} with all the objects to be tested.
     * @param ox    The x coordinate of the ray origin.
     * @param oy    The y coordinate of the ray origin.
     * @param oz    The z coordinate of the ray origin.
     * @param dx    The x vector of the ray direction.
     * @param dy    The y vector of the ray direction.
     * @param dz    The z vector of the ray direction.
     * @return The {@linkplain EyePointeeHolder eye pointee holders}
     * penetrated by the ray, sorted by distance from the camera rig.
     * Use {@link EyePointeeHolder#getOwnerObject()} to get the
     * corresponding scene objects.
     */
    public static final EyePointeeHolder[] pickScene(Scene scene, float ox, float oy, float oz, float dx, float dy, float dz) {
        sFindObjectsLock.lock();
        try {
            long[] ptrs = NativePicker.pickScene(scene.getNative(), ox, oy, oz,
                    dx, dy, dz);
            EyePointeeHolder[] eyePointeeHolders = new EyePointeeHolder[ptrs.length];
            VrContext vrContext = scene.getVrContext();
            for (int i = 0, length = ptrs.length; i < length; ++i) {
                Log.d(TAG, "pickScene(): ptrs[%d] = %x", i, ptrs[i]);
                eyePointeeHolders[i] = EyePointeeHolder.lookup(vrContext,
                        ptrs[i]);
                Log.d(TAG, "pickScene(): eyePointeeHolders[%d] = %s", i,
                        eyePointeeHolders[i]);
            }
            return eyePointeeHolders;
        } finally {
            sFindObjectsLock.unlock();
        }
    }

    /**
     * Tests the {@link SceneObject}s contained within scene against the
     * camera rig's lookat vector.
     * <p/>
     * <em>Note:</em> The {@linkplain EyePointeeHolder#getHit() hit location}
     * is stored in the native eye pointee holder during the ray casting
     * operation: <em>It is only valid until the next ray cast operation.</em>
     * That is, either the {@linkplain #pickScene(Scene) short pickScene()},
     * or the
     * {@linkplain #pickScene(Scene, float, float, float, float, float, float)
     * long pickScene()}, or
     * {@linkplain #findObjects(Scene, float, float, float, float, float, float)
     * findObjects()} calls will invalidate previous hit data. There are two
     * ways to avoid getting invalid hit data:
     * <ul>
     * <li>Use the high-level
     * {@linkplain #findObjects(Scene, float, float, float, float, float, float)
     * findObjects()} method, which returns a list of {@link PickedObject}:
     * each picked object pairs a {@link SceneObject} with the hit data.
     * <li>Write your code so that you never call ray casting operation until
     * you have retrieved the previous operations hit data. (This is easy, if
     * you only ever do picking from the GL thread. It's significantly harder if
     * you are using multiple threads.)
     * </ul>
     *
     * @param scene The {@link Scene} with all the objects to be tested.
     * @return the {@link EyePointeeHolder}s which are penetrated by the
     * picking ray. The holders are sorted by distance from the camera
     * rig.
     */
    public static final EyePointeeHolder[] pickScene(Scene scene) {
        return pickScene(scene, 0, 0, 0, 0, 0, -1.0f);
    }

    /**
     * Tests the {@link SceneObject} against the camera's lookat vector.
     *
     * @param sceneObject The {@link SceneObject} to be tested.
     * @param camera      The {@link Camera} to use for ray testing.
     * @return the distance from the camera. It returns positive infinity if
     * the camera is not pointing to the sceneObject.
     */
    public static final float pickSceneObject(SceneObject sceneObject, Camera camera) {
        return NativePicker.pickSceneObject(sceneObject.getNative(), camera.getNative());
    }

    /**
     * Tests the {@link SceneObject} against the camera's lookat vector.
     *
     * @param sceneObject The {@link SceneObject} to be tested.
     * @param camera      The {@link Camera} to use for ray testing.
     * @return Hit location on object local coordinates.
     */
    public static final float[] pickSceneObjectv(SceneObject sceneObject, Camera camera) {
        return NativePicker.pickSceneObjectv(sceneObject.getNative(), camera.getNative());
    }

    /**
     * Casts a ray into the scene graph, and returns the objects it intersects.
     * <p/>
     * The ray is defined by its origin {@code [ox, oy, oz]} and its direction
     * {@code [dx, dy, dz]}.
     * <p/>
     * The ray origin may be [0, 0, 0] and the direction components should be
     * normalized from -1 to 1: Note that the y direction runs from -1 at the
     * bottom to 1 at the top. To construct a picking ray originating at the
     * user's head and pointing into the scene along the camera lookat vector,
     * pass in 0, 0, 0 for the origin and 0, 0, -1 for the direction.
     * <p/>
     * This method is higher-level and easier to use than
     * {@link #pickScene(Scene, float, float, float, float, float, float)
     * pickScene():} Not only does it return the hit scene object (not its
     * holder) and the hit location directly, it is thread safe in a way that
     * the lower-level methods are not: The
     * {@linkplain EyePointeeHolder#getHit() hit location} is stored in the
     * native eye pointee holder during the ray casting operation and it is only
     * valid until the next ray cast operation. This method guarantees that only
     * one thread at a time is doing a ray cast into a particular scene graph,
     * and it extracts the hit data during within its synchronized block. You
     * can then examine the return list without worrying about another thread
     * corrupting your hit data.
     *
     * @param scene The {@link Scene} with all the objects to be tested.
     * @param ox    The x coordinate of the ray origin.
     * @param oy    The y coordinate of the ray origin.
     * @param oz    The z coordinate of the ray origin.
     * @param dx    The x vector of the ray direction.
     * @param dy    The y vector of the ray direction.
     * @param dz    The z vector of the ray direction.
     * @return A list of {@link PickedObject}, sorted by distance from the
     * camera. Each {@link PickedObject} contains the object
     * within the {@link EyePointeeHolder} along with the hit
     * location. (Note that the hit location is actually the point where
     * the cast ray intersected the holder's axis-aligned bounding box,
     * which may not be exactly where the ray would intersect the scene
     * object itself.)
     */
    public static final List<PickedObject> findObjects(Scene scene, float ox, float oy, float oz, float dx, float dy, float dz) {
        sFindObjectsLock.lock();
        try {
            VrContext vrContext = scene.getVrContext();

            long[] pointers = NativePicker.pickScene(scene.getNative(), ox, oy,
                    oz, dx, dy, dz);
            List<PickedObject> result = new ArrayList<PickedObject>(
                    pointers.length);
            for (long pointer : pointers) {
                EyePointeeHolder holder = EyePointeeHolder.lookup(
                        vrContext, pointer);
                result.add(new PickedObject(holder));
            }
            return result;
        } finally {
            sFindObjectsLock.unlock();
        }
    }

    /**
     * Tests the {@link SceneObject}s contained within scene against the
     * camera's lookat vector.
     * <p/>
     * This method uses higher-level function
     * {@linkplain #findObjects(Scene, float, float, float, float, float, float)
     * findObjects()} internally.
     *
     * @param scene The {@link Scene} with all the objects to be tested.
     * @return A list of {@link PickedObject}, sorted by distance from the
     * camera. Each {@link PickedObject} contains the object
     * within the {@link EyePointeeHolder} along with the hit
     * location.
     */
    public static final List<PickedObject> findObjects(Scene scene) {
        return findObjects(scene, 0, 0, 0, 0, 0, -1.0f);
    }

    /**
     * The result of a
     * {@link Picker#findObjects(Scene, float, float, float, float, float, float)
     * findObjects()} call.
     */
    public static class PickedObject {
        private final SceneObject sceneObject;
        private final float[] hitLocation;

        private PickedObject(EyePointeeHolder holder) {
            sceneObject = holder.getOwnerObject();
            hitLocation = holder.getHit();
        }

        /**
         * The {@link SceneObject} within the eye pointee holder's bounding
         * box - the object that the ray intersected.
         *
         * @return {@link EyePointeeHolder#getOwnerObject()}
         */
        public SceneObject getHitObject() {
            return sceneObject;
        }

        /**
         * The hit location, as an [x, y, z] array.
         *
         * @return A copy of the {@link EyePointeeHolder#getHit()} result:
         * changing the result will not change the
         * {@link PickedObject picked object's} hit data.
         */
        public float[] getHitLocation() {
            return Arrays.copyOf(hitLocation, hitLocation.length);
        }

        /**
         * The x coordinate of the hit location
         */
        public float getHitX() {
            return hitLocation[0];
        }

        /**
         * The x coordinate of the hit location
         */
        public float getHitY() {
            return hitLocation[1];
        }

        /**
         * The x coordinate of the hit location
         */
        public float getHitZ() {
            return hitLocation[2];
        }
    }

    static final ReentrantLock sFindObjectsLock = new ReentrantLock();
}

final class NativePicker {
    static native long[] pickScene(long scene, float ox, float oy, float oz, float dx, float dy, float dz);

    static native float pickSceneObject(long sceneObject, long camera);

    static native float[] pickSceneObjectv(long sceneObject, long camera);
}
