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

/**
 * The scene graph
 */
public class Scene extends SceneObject {

    private Camera mMainCamera;

    /**
     * Constructs a scene with a camera rig holding left & right cameras in it.
     */
    public Scene() {
        Camera camera = new Camera();
        addChildObject(camera);
        setMainCamera(camera);
    }

    private static native void setFrustumCulling(long scene, boolean flag);

    private static native void setOcclusionQuery(long scene, boolean flag);

    private static native void setMainCamera(long scene, long camera);

    @Override
    protected native long initNativeInstance();

    /**
     * @return The {@link Camera camera rig} used for rendering the scene on the screen.
     */
    public Camera getMainCamera() {
        return mMainCamera;
    }

    /**
     * Set the {@link Camera camera} used for rendering the scene on the screen.
     *
     * @param camera The {@link Camera camera} to render with.
     */
    public void setMainCamera(Camera camera) {
        mMainCamera = camera;
        setMainCamera(getNative(), camera.getNative());
    }

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
     * Called when the scene becomes main scene with {@link Meganekko#setScene(Scene)}.
     */
    public void onResume() {
    }

    /**
     * Called when the other scene becomes main scene with {@link Meganekko#setScene(Scene)}.
     */
    public void onPause() {
    }
}
