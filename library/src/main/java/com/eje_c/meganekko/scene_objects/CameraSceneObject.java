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

package com.eje_c.meganekko.scene_objects;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Material.ShaderType;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.VrFrame;

import java.io.IOException;

/**
 * A {@linkplain SceneObject scene object} that shows live video from one of the
 * device's cameras
 */
public class CameraSceneObject extends SceneObject {
    private final SurfaceTexture mSurfaceTexture;
    private boolean mPaused = false;

    /**
     * Create a {@linkplain SceneObject scene object} (with arbitrarily complex
     * geometry) that shows live video from one of the device's cameras
     *
     * @param vrContext current {@link VrContext}
     * @param mesh      an arbitrarily complex {@link Mesh} object - see
     *                  {@link VrContext#loadMesh(com.eje_c.meganekko.AndroidResource)}
     *                  and {@link VrContext#createQuad(float, float)}
     * @param camera    an Android {@link Camera}. <em>Note</em>: this constructor
     *                  calls {@link Camera#setPreviewTexture(SurfaceTexture)} so you
     *                  should be sure to call it before you call
     *                  {@link Camera#startPreview()}.
     */
    public CameraSceneObject(Mesh mesh, Camera camera) {
        super(mesh);

        Material material = new Material(ShaderType.OES.ID);
        mSurfaceTexture = material.getSurfaceTexture();

        getRenderData().setMaterial(material);

        try {
            camera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resumes camera preview
     * <p/>
     * <p/>
     * Note: {@link #pause()} and {@code resume()} only affect the polling that
     * links the Android {@link Camera} to this {@linkplain SceneObject
     * Meganekko scene object:} they have <em>no affect</em> on the underlying
     * {@link Camera} object.
     */
    public void resume() {
        mPaused = false;
    }

    /**
     * Pauses camera preview
     * <p/>
     * <p/>
     * Note: {@code pause()} and {@link #resume()} only affect the polling that
     * links the Android {@link Camera} to this {@linkplain SceneObject
     * Meganekko scene object:} they have <em>no affect</em> on the underlying
     * {@link Camera} object.
     */
    public void pause() {
        mPaused = true;
    }

    @Override
    public void onEvent(VrFrame vrFrame) {
        if (!mPaused) {
            mSurfaceTexture.updateTexImage();
        }
        super.onEvent(vrFrame);
    }
}
