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
package org.meganekkovr

/**
 * Native ovrFrameInput wrapper.
 */
class FrameInput internal constructor(
        /**
         * Get native `ovrFrameInput*` value.
         *
         * @return Native ovrFrameInput* value.
         */
        val nativePointer: Long) {

    /**
     * Predicted absolute time in seconds this frame will be displayed.
     * To make accurate journal playback possible, applications should
     * use this time instead of geting system time directly.
     *
     * @return Predicted absolute time
     */
    val predictedDisplayTimeInSeconds: Double
        get() = getPredictedDisplayTimeInSeconds(nativePointer)

    /**
     * The amount of time in seconds that has passed since the last frame,
     * usable for movement scaling.
     * This will be clamped to no more than 0.1 seconds to prevent
     * excessive movement after pauses for loading or initialization.
     *
     * @return The amount of time in seconds that has passed since the last frame
     */
    val deltaSeconds: Float
        get() = getDeltaSeconds(nativePointer)

    /**
     * Incremented once for every frame.
     *
     * @return Frame number
     */
    val frameNumber: Int
        get() = getFrameNumber(nativePointer)

    /**
     * Ranges from 0.0 - 1.0 during a swipe action.
     * Applications can provide visual feedback that a swipe
     * is being recognized.
     *
     * @return Swipe fraction.
     */
    val swipeFraction: Float
        get() = getSwipeFraction(nativePointer)

    /**
     * Bits are set for the buttons that are currently pressed down.
     *
     * @return Button state
     */
    val buttonState: Int
        get() = getButtonState(nativePointer)

    /**
     * Pressed button from the last VrFrame.
     *
     * @return Pressed buttons from the last VrFrame.
     */
    val buttonPressed: Int
        get() = getButtonPressed(nativePointer)

    /**
     * Released button from the last VrFrame.
     *
     * @return Released button from the last VrFrame.
     */
    val buttonReleased: Int
        get() = getButtonReleased(nativePointer)

    private external fun getPredictedDisplayTimeInSeconds(vrFramePtr: Long): Double

    private external fun getDeltaSeconds(vrFramePtr: Long): Float

    private external fun getFrameNumber(vrFramePtr: Long): Int

    private external fun getSwipeFraction(vrFramePtr: Long): Float

    private external fun getButtonState(vrFramePtr: Long): Int

    private external fun getButtonPressed(vrFramePtr: Long): Int

    private external fun getButtonReleased(vrFramePtr: Long): Int
}
