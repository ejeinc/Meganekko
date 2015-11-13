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

/**
 * Holds the Cameras.
 */
public class Camera extends SceneObject {

    public Camera(VrContext vrContext) {
        super(vrContext);
    }

    @Override
    protected native long initNativeInstance();

    /**
     * The direction the camera rig is looking at. In other words, the direction
     * of the local -z axis.
     *
     * @return Array with 3 floats corresponding to a normalized direction
     * vector. ([0] : x, [1] : y, [2] : z)
     */
    public float[] getLookAt() {
        return NativeCamera.getLookAt(getNative());
    }
}

class NativeCamera {
    static native float[] getLookAt(long camera);
}
