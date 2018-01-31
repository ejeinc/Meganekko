/* 
 * Copyright 2015 eje inc.
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
 * Copied from VrAppFramework/Include/Input.h
 */
object JoyButton {
    const val BUTTON_A = 1 shl 0
    const val BUTTON_B = 1 shl 1
    const val BUTTON_X = 1 shl 2
    const val BUTTON_Y = 1 shl 3
    const val BUTTON_START = 1 shl 4
    const val BUTTON_BACK = 1 shl 5
    const val BUTTON_SELECT = 1 shl 6
    const val BUTTON_MENU = 1 shl 7
    const val BUTTON_RIGHT_TRIGGER = 1 shl 8
    const val BUTTON_LEFT_TRIGGER = 1 shl 9
    const val BUTTON_DPAD_UP = 1 shl 10
    const val BUTTON_DPAD_DOWN = 1 shl 11
    const val BUTTON_DPAD_LEFT = 1 shl 12
    const val BUTTON_DPAD_RIGHT = 1 shl 13
    const val BUTTON_LSTICK_UP = 1 shl 14
    const val BUTTON_LSTICK_DOWN = 1 shl 15
    const val BUTTON_LSTICK_LEFT = 1 shl 16
    const val BUTTON_LSTICK_RIGHT = 1 shl 17
    const val BUTTON_RSTICK_UP = 1 shl 18
    const val BUTTON_RSTICK_DOWN = 1 shl 19
    const val BUTTON_RSTICK_LEFT = 1 shl 20
    const val BUTTON_RSTICK_RIGHT = 1 shl 21
    const val BUTTON_TOUCH = 1 shl 22
    const val BUTTON_SWIPE_UP = 1 shl 23
    const val BUTTON_SWIPE_DOWN = 1 shl 24
    const val BUTTON_SWIPE_FORWARD = 1 shl 25
    const val BUTTON_SWIPE_BACK = 1 shl 26
    const val BUTTON_TOUCH_WAS_SWIPE = 1 shl 27
    const val BUTTON_TOUCH_SINGLE = 1 shl 28
    const val BUTTON_TOUCH_DOUBLE = 1 shl 29
    const val BUTTON_TOUCH_LONGPRESS = 1 shl 30

    /**
     * @param buttonState Returned value from `FrameInput#getButtonPressed()`,
     * `FrameInput#getButtonReleased()` or
     * `FrameInput#getButtonState()`.
     * @param code        JoyButton.BUTTON_* constant value.
     * @return true if `buttonState` contains `code` in bit flags.
     */
    @JvmStatic
    fun contains(buttonState: Int, code: Int): Boolean {
        return buttonState and code > 0
    }
}
