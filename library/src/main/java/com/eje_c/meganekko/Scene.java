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

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * The scene graph
 */
public class Scene extends SceneObject {

    private boolean mInitialized;
    private MeganekkoApp mApp;

    private static native void setFrustumCulling(long scene, boolean flag);

    private static native void setOcclusionQuery(long scene, boolean flag);

    private static native boolean isLookingAt(long scene, long sceneObject);

    private static native void getLookingPoint(long scene, long sceneObject, boolean axisInWorld, float[] val);

    private static native void setViewMatrix(long scene, float[] m);

    private static native void setProjectionMatrix(long scene, float[] m);

    private static native void render(long scene, int eye);

    private static native void setViewPosition(long scene, float x, float y, float z);

    private static native void getViewPosition(long scene, float[] val);

    private static native void getViewOrientation(long scene, float[] val);

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
     * This is called just before this scene is first rendered.
     * Usually, you should keep reference to {@link SceneObject} by {@link #findObjectById(int)}
     * in this method.
     * You must call {@code super.initialize(app)} if you override this method.
     *
     * @param app
     */
    protected void initialize(MeganekkoApp app) {
        mApp = app;
    }

    void onResume(MeganekkoApp app) {

        // Call initialize first time only
        if (!mInitialized) {
            initialize(app);
            mInitialized = true;
        }

        onResume();
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

    public Vector3f getLookingPoint(SceneObject target, boolean axisInWorld) {
        synchronized (TEMP_VALUES_FOR_JNI) {
            getLookingPoint(getNative(), target.getNative(), axisInWorld, TEMP_VALUES_FOR_JNI);
            return new Vector3f(TEMP_VALUES_FOR_JNI[0], TEMP_VALUES_FOR_JNI[1], TEMP_VALUES_FOR_JNI[2]);
        }
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

    public Vector3f getViewPosition() {
        synchronized (TEMP_VALUES_FOR_JNI) {
            getViewPosition(getNative(), TEMP_VALUES_FOR_JNI);
            return new Vector3f(TEMP_VALUES_FOR_JNI[0], TEMP_VALUES_FOR_JNI[1], TEMP_VALUES_FOR_JNI[2]);
        }
    }

    public Quaternionf getViewOrientation() {
        synchronized (TEMP_VALUES_FOR_JNI) {
            getViewOrientation(getNative(), TEMP_VALUES_FOR_JNI);
            return new Quaternionf(TEMP_VALUES_FOR_JNI[0], TEMP_VALUES_FOR_JNI[1], TEMP_VALUES_FOR_JNI[2], TEMP_VALUES_FOR_JNI[3]);
        }
    }

    public MeganekkoApp getApp() {
        return mApp;
    }
}
