/* Copyright 2015 eje inc.
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
 * Native VrFrame wrapper.
 */
public class VrFrame {

    private long nativePtr;

    VrFrame(long nativePtr) {
        this.nativePtr = nativePtr;
    }

    void setNativePtr(long nativePtr) {
        this.nativePtr = nativePtr;
    }

    public double getPredictedDisplayTimeInSeconds() {
        return getPredictedDisplayTimeInSeconds(nativePtr);
    }

    public float getDeltaSeconds() {
        return getDeltaSeconds(nativePtr);
    }

    public int getFrameNumber() {
        return getFrameNumber(nativePtr);
    }

    public float getSwipeFraction() {
        return getSwipeFraction(nativePtr);
    }

    public int getButtonState() {
        return getButtonState(nativePtr);
    }

    public int getButtonPressed() {
        return getButtonPressed(nativePtr);
    }

    public int getButtonReleased() {
        return getButtonReleased(nativePtr);
    }

    private static native double getPredictedDisplayTimeInSeconds(long vrFramePtr);

    private static native float getDeltaSeconds(long vrFramePtr);

    private static native int getFrameNumber(long vrFramePtr);

    private static native float getSwipeFraction(long vrFramePtr);

    private static native int getButtonState(long vrFramePtr);

    private static native int getButtonPressed(long vrFramePtr);

    private static native int getButtonReleased(long vrFramePtr);
}
