/* 
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
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

import org.joml.Vector3f;

/**
 * The scene graph
 */
public class Scene extends SceneObject {

    private static native void setFrustumCulling(long scene, boolean flag);

    private static native void setOcclusionQuery(long scene, boolean flag);

    private static native boolean isLookingAt(long scene, long sceneObject);

    private static native void setViewMatrix(long scene, float[] m);

    private static native void setProjectionMatrix(long scene, float[] m);

    private static native void render(long scene, int eye);

    private static native void setViewPosition(long scene, float x, float y, float z);

    private static native float[] getViewPosition(long scene);

    private static native float[] getViewOrientation(long scene);

    @Override
    protected native long initNativeInstance();

    /**
     * Sets the frustum culling for the {@link Scene}.
     */
    public void setFrustumCulling(boolean flag) {
        setFrustumCulling(getNative(), flag);
    }

    /**
     * Sets the occlusion query for the {@link Scene}.
     */
    public void setOcclusionQuery(boolean flag) {
        setOcclusionQuery(getNative(), flag);
    }

    /**
     * Called when the scene becomes main scene with {@link MeganekkoApp#setScene(Scene)}.
     */
    public void onResume() {
    }

    /**
     * Called when the other scene becomes main scene with {@link MeganekkoApp#setScene(Scene)}.
     */
    public void onPause() {
    }

    public boolean isLookingAt(SceneObject target) {
        return isLookingAt(getNative(), target.getNative());
    }

    public void setViewMatrix(float[] viewM) {
        setViewMatrix(getNative(), viewM);
    }

    public void setProjectionMatrix(float[] projectionM) {
        setProjectionMatrix(getNative(), projectionM);
    }

    public void render(int eye) {
        render(getNative(), eye);
    }

    public void setViewPosition(float x, float y, float z) {
        setViewPosition(getNative(), x, y, z);
    }

    public void setViewPosition(Vector3f pos) {
        setViewPosition(getNative(), pos.x, pos.y, pos.z);
    }

    public float[] getViewPosition() {
        return getViewPosition(getNative());
    }

    public float[] getViewOrientation() {
        return getViewOrientation(getNative());
    }
}
