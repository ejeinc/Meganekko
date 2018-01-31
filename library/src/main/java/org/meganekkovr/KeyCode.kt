package org.meganekkovr

/**
 * Manually coped from VrAppFramework/Input.h enum ovrKeyCode.
 */
object KeyCode {
    const val OVR_KEY_NONE = 0 // nothing

    const val OVR_KEY_LCONTROL = 1
    const val OVR_KEY_RCONTROL = 2
    const val OVR_KEY_LSHIFT = 3
    const val OVR_KEY_RSHIFT = 4
    const val OVR_KEY_LALT = 5
    const val OVR_KEY_RALT = 6
    const val OVR_KEY_MENU = 7

    const val OVR_KEY_UP = 9
    const val OVR_KEY_DOWN = 10
    const val OVR_KEY_LEFT = 11
    const val OVR_KEY_RIGHT = 12

    const val OVR_KEY_F1 = 14
    const val OVR_KEY_F2 = 15
    const val OVR_KEY_F3 = 16
    const val OVR_KEY_F4 = 17
    const val OVR_KEY_F5 = 18
    const val OVR_KEY_F6 = 19
    const val OVR_KEY_F7 = 20
    const val OVR_KEY_F8 = 21
    const val OVR_KEY_F9 = 22
    const val OVR_KEY_F10 = 23
    const val OVR_KEY_F11 = 24
    const val OVR_KEY_F12 = 25

    const val OVR_KEY_RETURN = 27 // return (not on numeric keypad)
    const val OVR_KEY_SPACE = 28  // space bar
    const val OVR_KEY_INSERT = 29
    const val OVR_KEY_DELETE = 30
    const val OVR_KEY_HOME = 31
    const val OVR_KEY_END = 32
    const val OVR_KEY_PAGEUP = 33
    const val OVR_KEY_PAGEDOWN = 34
    const val OVR_KEY_SCROLL_LOCK = 35
    const val OVR_KEY_PAUSE = 36
    const val OVR_KEY_PRINT_SCREEN = 37
    const val OVR_KEY_NUM_LOCK = 38
    const val OVR_KEY_CAPSLOCK = 39
    const val OVR_KEY_ESCAPE = 40
    const val OVR_KEY_BACK = OVR_KEY_ESCAPE // escape and back are synonomous
    const val OVR_KEY_SYS_REQ = 41
    const val OVR_KEY_BREAK = 42

    const val OVR_KEY_KP_DIVIDE = 44   // / (forward slash) on numeric keypad
    const val OVR_KEY_KP_MULTIPLY = 45 // * on numeric keypad
    const val OVR_KEY_KP_ADD = 46      // + on numeric keypad
    const val OVR_KEY_KP_SUBTRACT = 47 // - on numeric keypad
    const val OVR_KEY_KP_ENTER = 48    // enter on numeric keypad
    const val OVR_KEY_KP_DECIMAL = 49  // delete on numeric keypad
    const val OVR_KEY_KP_0 = 50
    const val OVR_KEY_KP_1 = 51
    const val OVR_KEY_KP_2 = 52
    const val OVR_KEY_KP_3 = 53
    const val OVR_KEY_KP_4 = 54
    const val OVR_KEY_KP_5 = 55
    const val OVR_KEY_KP_6 = 56
    const val OVR_KEY_KP_7 = 57
    const val OVR_KEY_KP_8 = 58
    const val OVR_KEY_KP_9 = 59

    const val OVR_KEY_TAB = 61
    const val OVR_KEY_COMMA = 62         // ,
    const val OVR_KEY_PERIOD = 63        // .
    const val OVR_KEY_LESS = 64          // <
    const val OVR_KEY_GREATER = 65       // >
    const val OVR_KEY_FORWARD_SLASH = 66 // /
    const val OVR_KEY_BACK_SLASH = 67    /* \ */
    const val OVR_KEY_QUESTION_MARK = 68 // ?
    const val OVR_KEY_SEMICOLON = 69     // ;
    const val OVR_KEY_COLON = 70         // :
    const val OVR_KEY_APOSTROPHE = 71    // '
    const val OVR_KEY_QUOTE = 72         // "
    const val OVR_KEY_OPEN_BRACKET = 73  // [
    const val OVR_KEY_CLOSE_BRACKET = 74 // ]
    const val OVR_KEY_CLOSE_BRACE = 75   // {
    const val OVR_KEY_OPEN_BRACE = 76    // }
    const val OVR_KEY_BAR = 77           // |
    const val OVR_KEY_TILDE = 78         // ~
    const val OVR_KEY_GRAVE = 79         // `

    const val OVR_KEY_1 = 81
    const val OVR_KEY_2 = 82
    const val OVR_KEY_3 = 83
    const val OVR_KEY_4 = 84
    const val OVR_KEY_5 = 85
    const val OVR_KEY_6 = 86
    const val OVR_KEY_7 = 87
    const val OVR_KEY_8 = 88
    const val OVR_KEY_9 = 89
    const val OVR_KEY_0 = 90
    const val OVR_KEY_EXCLAMATION = 91  // !
    const val OVR_KEY_AT = 92           // @
    const val OVR_KEY_POUND = 93        // #
    const val OVR_KEY_DOLLAR = 94       // $
    const val OVR_KEY_PERCENT = 95      // %
    const val OVR_KEY_CARET = 96        // ^
    const val OVR_KEY_AMPERSAND = 97    // &
    const val OVR_KEY_ASTERISK = 98     // *
    const val OVR_KEY_OPEN_PAREN = 99   // (
    const val OVR_KEY_CLOSE_PAREN = 100 // )
    const val OVR_KEY_MINUS = 101       // -
    const val OVR_KEY_UNDERSCORE = 102  // _
    const val OVR_KEY_PLUS = 103        // +
    const val OVR_KEY_EQUALS = 104      // =
    const val OVR_KEY_BACKSPACE = 105   //

    const val OVR_KEY_A = 107
    const val OVR_KEY_B = 108
    const val OVR_KEY_C = 109
    const val OVR_KEY_D = 110
    const val OVR_KEY_E = 111
    const val OVR_KEY_F = 112
    const val OVR_KEY_G = 113
    const val OVR_KEY_H = 114
    const val OVR_KEY_I = 115
    const val OVR_KEY_J = 116
    const val OVR_KEY_K = 117
    const val OVR_KEY_L = 118
    const val OVR_KEY_M = 119
    const val OVR_KEY_N = 120
    const val OVR_KEY_O = 121
    const val OVR_KEY_P = 122
    const val OVR_KEY_Q = 123
    const val OVR_KEY_R = 124
    const val OVR_KEY_S = 125
    const val OVR_KEY_T = 126
    const val OVR_KEY_U = 127
    const val OVR_KEY_V = 128
    const val OVR_KEY_W = 129
    const val OVR_KEY_X = 130
    const val OVR_KEY_Y = 131
    const val OVR_KEY_Z = 132

    const val OVR_KEY_VOLUME_MUTE = 134
    const val OVR_KEY_VOLUME_UP = 135
    const val OVR_KEY_VOLUME_DOWN = 136
    const val OVR_KEY_MEDIA_NEXT_TRACK = 137
    const val OVR_KEY_MEDIA_PREV_TRACK = 138
    const val OVR_KEY_MEDIA_STOP = 139
    const val OVR_KEY_MEDIA_PLAY_PAUSE = 140
    const val OVR_KEY_LAUNCH_APP1 = 141
    const val OVR_KEY_LAUNCH_APP2 = 142

    const val OVR_KEY_BUTTON_A = 144
    const val OVR_KEY_BUTTON_B = 145
    const val OVR_KEY_BUTTON_C = 146
    const val OVR_KEY_BUTTON_X = 147
    const val OVR_KEY_BUTTON_Y = 148
    const val OVR_KEY_BUTTON_Z = 149
    const val OVR_KEY_BUTTON_START = 150
    const val OVR_KEY_BUTTON_SELECT = 151
    const val OVR_KEY_BUTTON_MENU = 152
    const val OVR_KEY_LEFT_TRIGGER = 153
    const val OVR_KEY_BUTTON_L1 = OVR_KEY_LEFT_TRIGGER    // FIXME: this is a poor name, but we're maintaining it for ease of conversion
    const val OVR_KEY_RIGHT_TRIGGER = 154
    const val OVR_KEY_BUTTON_R1 = OVR_KEY_RIGHT_TRIGGER    // FIXME: this is a poor name, but we're maintaining it for eash of conversion
    const val OVR_KEY_DPAD_UP = 155
    const val OVR_KEY_DPAD_DOWN = 156
    const val OVR_KEY_DPAD_LEFT = 157
    const val OVR_KEY_DPAD_RIGHT = 158
    const val OVR_KEY_LSTICK_UP = 159
    const val OVR_KEY_LSTICK_DOWN = 160
    const val OVR_KEY_LSTICK_LEFT = 161
    const val OVR_KEY_LSTICK_RIGHT = 162
    const val OVR_KEY_RSTICK_UP = 163
    const val OVR_KEY_RSTICK_DOWN = 164
    const val OVR_KEY_RSTICK_LEFT = 165
    const val OVR_KEY_RSTICK_RIGHT = 166

    const val OVR_KEY_BUTTON_LEFT_SHOULDER = 167                // the button above the left trigger on MOGA / XBox / PS joypads
    const val OVR_KEY_BUTTON_L2 = OVR_KEY_BUTTON_LEFT_SHOULDER
    const val OVR_KEY_BUTTON_RIGHT_SHOULDER = 168               // the button above ther right trigger on MOGA / XBox / PS joypads
    const val OVR_KEY_BUTTON_R2 = OVR_KEY_BUTTON_RIGHT_SHOULDER
    const val OVR_KEY_BUTTON_LEFT_THUMB = 169                   // click of the left thumbstick
    const val OVR_KEY_BUTTON_THUMBL = OVR_KEY_BUTTON_LEFT_THUMB
    const val OVR_KEY_BUTTON_RIGHT_THUMB = 170                  // click of the left thumbstick
    const val OVR_KEY_BUTTON_THUMBR = OVR_KEY_BUTTON_RIGHT_THUMB

    const val OVR_KEY_MAX = 172
}
