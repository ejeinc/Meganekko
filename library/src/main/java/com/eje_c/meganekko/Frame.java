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
 * Represents frame information such as delta time, absolute time or button state.
 */
public interface Frame {

    /**
     * Predicted absolute time in seconds this frame will be displayed.
     * To make accurate journal playback possible, applications should
     * use this time instead of geting system time directly.
     *
     * @return Predicted absolute time
     */
    double getPredictedDisplayTimeInSeconds();

    /**
     * The amount of time in seconds that has passed since the last frame,
     * usable for movement scaling.
     * This will be clamped to no more than 0.1 seconds to prevent
     * excessive movement after pauses for loading or initialization.
     *
     * @return The amount of time in seconds that has passed since the last frame
     */
    float getDeltaSeconds();

    /**
     * Incremented once for every frame.
     *
     * @return Frame number
     */
    int getFrameNumber();

    /**
     * Ranges from 0.0 - 1.0 during a swipe action.
     * Applications can provide visual feedback that a swipe
     * is being recognized.
     *
     * @return Swipe fraction.
     */
    float getSwipeFraction();

    /**
     * Bits are set for the buttons that are currently pressed down.
     *
     * @return Button state
     */
    int getButtonState();

    /**
     * Pressed button from the last VrFrame.
     *
     * @return Pressed buttons from the last VrFrame.
     */
    int getButtonPressed();

    /**
     * Released button from the last VrFrame.
     *
     * @return Released button from the last VrFrame.
     */
    int getButtonReleased();
}
