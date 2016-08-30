package org.meganekkovr;

/**
 * Manually coped from VrAppFramework/Input.h enum ovrKeyCode.
 */
public class KeyCode {
    public static final int OVR_KEY_NONE = 0; // nothing

    public static final int OVR_KEY_LCONTROL = 1;
    public static final int OVR_KEY_RCONTROL = 2;
    public static final int OVR_KEY_LSHIFT = 3;
    public static final int OVR_KEY_RSHIFT = 4;
    public static final int OVR_KEY_LALT = 5;
    public static final int OVR_KEY_RALT = 6;
    public static final int OVR_KEY_MENU = 7;

    public static final int OVR_KEY_UP = 9;
    public static final int OVR_KEY_DOWN = 10;
    public static final int OVR_KEY_LEFT = 11;
    public static final int OVR_KEY_RIGHT = 12;

    public static final int OVR_KEY_F1 = 14;
    public static final int OVR_KEY_F2 = 15;
    public static final int OVR_KEY_F3 = 16;
    public static final int OVR_KEY_F4 = 17;
    public static final int OVR_KEY_F5 = 18;
    public static final int OVR_KEY_F6 = 19;
    public static final int OVR_KEY_F7 = 20;
    public static final int OVR_KEY_F8 = 21;
    public static final int OVR_KEY_F9 = 22;
    public static final int OVR_KEY_F10 = 23;
    public static final int OVR_KEY_F11 = 24;
    public static final int OVR_KEY_F12 = 25;

    public static final int OVR_KEY_RETURN = 27; // return (not on numeric keypad)
    public static final int OVR_KEY_SPACE = 28;  // space bar
    public static final int OVR_KEY_INSERT = 29;
    public static final int OVR_KEY_DELETE = 30;
    public static final int OVR_KEY_HOME = 31;
    public static final int OVR_KEY_END = 32;
    public static final int OVR_KEY_PAGEUP = 33;
    public static final int OVR_KEY_PAGEDOWN = 34;
    public static final int OVR_KEY_SCROLL_LOCK = 35;
    public static final int OVR_KEY_PAUSE = 36;
    public static final int OVR_KEY_PRINT_SCREEN = 37;
    public static final int OVR_KEY_NUM_LOCK = 38;
    public static final int OVR_KEY_CAPSLOCK = 39;
    public static final int OVR_KEY_ESCAPE = 40;
    public static final int OVR_KEY_BACK = OVR_KEY_ESCAPE; // escape and back are synonomous
    public static final int OVR_KEY_SYS_REQ = 41;
    public static final int OVR_KEY_BREAK = 42;

    public static final int OVR_KEY_KP_DIVIDE = 44;   // / (forward slash) on numeric keypad
    public static final int OVR_KEY_KP_MULTIPLY = 45; // * on numeric keypad
    public static final int OVR_KEY_KP_ADD = 46;      // + on numeric keypad
    public static final int OVR_KEY_KP_SUBTRACT = 47; // - on numeric keypad
    public static final int OVR_KEY_KP_ENTER = 48;    // enter on numeric keypad
    public static final int OVR_KEY_KP_DECIMAL = 49;  // delete on numeric keypad
    public static final int OVR_KEY_KP_0 = 50;
    public static final int OVR_KEY_KP_1 = 51;
    public static final int OVR_KEY_KP_2 = 52;
    public static final int OVR_KEY_KP_3 = 53;
    public static final int OVR_KEY_KP_4 = 54;
    public static final int OVR_KEY_KP_5 = 55;
    public static final int OVR_KEY_KP_6 = 56;
    public static final int OVR_KEY_KP_7 = 57;
    public static final int OVR_KEY_KP_8 = 58;
    public static final int OVR_KEY_KP_9 = 59;

    public static final int OVR_KEY_TAB = 61;
    public static final int OVR_KEY_COMMA = 62;         // ,
    public static final int OVR_KEY_PERIOD = 63;        // .
    public static final int OVR_KEY_LESS = 64;          // <
    public static final int OVR_KEY_GREATER = 65;       // >
    public static final int OVR_KEY_FORWARD_SLASH = 66; // /
    public static final int OVR_KEY_BACK_SLASH = 67;    /* \ */
    public static final int OVR_KEY_QUESTION_MARK = 68; // ?
    public static final int OVR_KEY_SEMICOLON = 69;     // ;
    public static final int OVR_KEY_COLON = 70;         // :
    public static final int OVR_KEY_APOSTROPHE = 71;    // '
    public static final int OVR_KEY_QUOTE = 72;         // "
    public static final int OVR_KEY_OPEN_BRACKET = 73;  // [
    public static final int OVR_KEY_CLOSE_BRACKET = 74; // ]
    public static final int OVR_KEY_CLOSE_BRACE = 75;   // {
    public static final int OVR_KEY_OPEN_BRACE = 76;    // }
    public static final int OVR_KEY_BAR = 77;           // |
    public static final int OVR_KEY_TILDE = 78;         // ~
    public static final int OVR_KEY_GRAVE = 79;         // `

    public static final int OVR_KEY_1 = 81;
    public static final int OVR_KEY_2 = 82;
    public static final int OVR_KEY_3 = 83;
    public static final int OVR_KEY_4 = 84;
    public static final int OVR_KEY_5 = 85;
    public static final int OVR_KEY_6 = 86;
    public static final int OVR_KEY_7 = 87;
    public static final int OVR_KEY_8 = 88;
    public static final int OVR_KEY_9 = 89;
    public static final int OVR_KEY_0 = 90;
    public static final int OVR_KEY_EXCLAMATION = 91;  // !
    public static final int OVR_KEY_AT = 92;           // @
    public static final int OVR_KEY_POUND = 93;        // #
    public static final int OVR_KEY_DOLLAR = 94;       // $
    public static final int OVR_KEY_PERCENT = 95;      // %
    public static final int OVR_KEY_CARET = 96;        // ^
    public static final int OVR_KEY_AMPERSAND = 97;    // &
    public static final int OVR_KEY_ASTERISK = 98;     // *
    public static final int OVR_KEY_OPEN_PAREN = 99;   // (
    public static final int OVR_KEY_CLOSE_PAREN = 100; // )
    public static final int OVR_KEY_MINUS = 101;       // -
    public static final int OVR_KEY_UNDERSCORE = 102;  // _
    public static final int OVR_KEY_PLUS = 103;        // +
    public static final int OVR_KEY_EQUALS = 104;      // =
    public static final int OVR_KEY_BACKSPACE = 105;   //

    public static final int OVR_KEY_A = 107;
    public static final int OVR_KEY_B = 108;
    public static final int OVR_KEY_C = 109;
    public static final int OVR_KEY_D = 110;
    public static final int OVR_KEY_E = 111;
    public static final int OVR_KEY_F = 112;
    public static final int OVR_KEY_G = 113;
    public static final int OVR_KEY_H = 114;
    public static final int OVR_KEY_I = 115;
    public static final int OVR_KEY_J = 116;
    public static final int OVR_KEY_K = 117;
    public static final int OVR_KEY_L = 118;
    public static final int OVR_KEY_M = 119;
    public static final int OVR_KEY_N = 120;
    public static final int OVR_KEY_O = 121;
    public static final int OVR_KEY_P = 122;
    public static final int OVR_KEY_Q = 123;
    public static final int OVR_KEY_R = 124;
    public static final int OVR_KEY_S = 125;
    public static final int OVR_KEY_T = 126;
    public static final int OVR_KEY_U = 127;
    public static final int OVR_KEY_V = 128;
    public static final int OVR_KEY_W = 129;
    public static final int OVR_KEY_X = 130;
    public static final int OVR_KEY_Y = 131;
    public static final int OVR_KEY_Z = 132;

    public static final int OVR_KEY_VOLUME_MUTE = 134;
    public static final int OVR_KEY_VOLUME_UP = 135;
    public static final int OVR_KEY_VOLUME_DOWN = 136;
    public static final int OVR_KEY_MEDIA_NEXT_TRACK = 137;
    public static final int OVR_KEY_MEDIA_PREV_TRACK = 138;
    public static final int OVR_KEY_MEDIA_STOP = 139;
    public static final int OVR_KEY_MEDIA_PLAY_PAUSE = 140;
    public static final int OVR_KEY_LAUNCH_APP1 = 141;
    public static final int OVR_KEY_LAUNCH_APP2 = 142;

    public static final int OVR_KEY_BUTTON_A = 144;
    public static final int OVR_KEY_BUTTON_B = 145;
    public static final int OVR_KEY_BUTTON_C = 146;
    public static final int OVR_KEY_BUTTON_X = 147;
    public static final int OVR_KEY_BUTTON_Y = 148;
    public static final int OVR_KEY_BUTTON_Z = 149;
    public static final int OVR_KEY_BUTTON_START = 150;
    public static final int OVR_KEY_BUTTON_SELECT = 151;
    public static final int OVR_KEY_BUTTON_MENU = 152;
    public static final int OVR_KEY_LEFT_TRIGGER = 153;
    public static final int OVR_KEY_BUTTON_L1 = OVR_KEY_LEFT_TRIGGER;    // FIXME: this is a poor name, but we're maintaining it for ease of conversion
    public static final int OVR_KEY_RIGHT_TRIGGER = 154;
    public static final int OVR_KEY_BUTTON_R1 = OVR_KEY_RIGHT_TRIGGER;    // FIXME: this is a poor name, but we're maintaining it for eash of conversion
    public static final int OVR_KEY_DPAD_UP = 155;
    public static final int OVR_KEY_DPAD_DOWN = 156;
    public static final int OVR_KEY_DPAD_LEFT = 157;
    public static final int OVR_KEY_DPAD_RIGHT = 158;
    public static final int OVR_KEY_LSTICK_UP = 159;
    public static final int OVR_KEY_LSTICK_DOWN = 160;
    public static final int OVR_KEY_LSTICK_LEFT = 161;
    public static final int OVR_KEY_LSTICK_RIGHT = 162;
    public static final int OVR_KEY_RSTICK_UP = 163;
    public static final int OVR_KEY_RSTICK_DOWN = 164;
    public static final int OVR_KEY_RSTICK_LEFT = 165;
    public static final int OVR_KEY_RSTICK_RIGHT = 166;

    public static final int OVR_KEY_BUTTON_LEFT_SHOULDER = 167;                // the button above the left trigger on MOGA / XBox / PS joypads
    public static final int OVR_KEY_BUTTON_L2 = OVR_KEY_BUTTON_LEFT_SHOULDER;
    public static final int OVR_KEY_BUTTON_RIGHT_SHOULDER = 168;               // the button above ther right trigger on MOGA / XBox / PS joypads
    public static final int OVR_KEY_BUTTON_R2 = OVR_KEY_BUTTON_RIGHT_SHOULDER;
    public static final int OVR_KEY_BUTTON_LEFT_THUMB = 169;                   // click of the left thumbstick
    public static final int OVR_KEY_BUTTON_THUMBL = OVR_KEY_BUTTON_LEFT_THUMB;
    public static final int OVR_KEY_BUTTON_RIGHT_THUMB = 170;                  // click of the left thumbstick
    public static final int OVR_KEY_BUTTON_THUMBR = OVR_KEY_BUTTON_RIGHT_THUMB;

    public static final int OVR_KEY_MAX = 172;
}
