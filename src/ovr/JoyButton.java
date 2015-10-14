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

package ovr;

/**
 * Copied from VrAppFramework/Include/Input.h
 */
public class JoyButton {
    public static final int BUTTON_A = 1 << 0;
    public static final int BUTTON_B = 1 << 1;
    public static final int BUTTON_X = 1 << 2;
    public static final int BUTTON_Y = 1 << 3;
    public static final int BUTTON_START = 1 << 4;
    public static final int BUTTON_BACK = 1 << 5;
    public static final int BUTTON_SELECT = 1 << 6;
    public static final int BUTTON_MENU = 1 << 7;
    public static final int BUTTON_RIGHT_TRIGGER = 1 << 8;
    public static final int BUTTON_LEFT_TRIGGER = 1 << 9;
    public static final int BUTTON_DPAD_UP = 1 << 10;
    public static final int BUTTON_DPAD_DOWN = 1 << 11;
    public static final int BUTTON_DPAD_LEFT = 1 << 12;
    public static final int BUTTON_DPAD_RIGHT = 1 << 13;
    public static final int BUTTON_LSTICK_UP = 1 << 14;
    public static final int BUTTON_LSTICK_DOWN = 1 << 15;
    public static final int BUTTON_LSTICK_LEFT = 1 << 16;
    public static final int BUTTON_LSTICK_RIGHT = 1 << 17;
    public static final int BUTTON_RSTICK_UP = 1 << 18;
    public static final int BUTTON_RSTICK_DOWN = 1 << 19;
    public static final int BUTTON_RSTICK_LEFT = 1 << 20;
    public static final int BUTTON_RSTICK_RIGHT = 1 << 21;
    public static final int BUTTON_TOUCH = 1 << 22;
    public static final int BUTTON_SWIPE_UP = 1 << 23;
    public static final int BUTTON_SWIPE_DOWN = 1 << 24;
    public static final int BUTTON_SWIPE_FORWARD = 1 << 25;
    public static final int BUTTON_SWIPE_BACK = 1 << 26;
    public static final int BUTTON_TOUCH_WAS_SWIPE = 1 << 27;
    public static final int BUTTON_TOUCH_SINGLE = 1 << 28;
    public static final int BUTTON_TOUCH_DOUBLE = 1 << 29;
    public static final int BUTTON_TOUCH_LONGPRESS = 1 << 30;

    /**
     * 
     * @param buttonState
     *            Returned value from {@code VrFrame#getButtonPressed()},
     *            {@code VrFrame#getButtonReleased()} or
     *            {@code VrFrame#getButtonState()}.
     * @param code
     *            JoyButton.BUTTON_* constant value.
     * @return 
     */
    public static boolean contains(int buttonState, int code) {
        return (buttonState & code) > 0;
    }
}
