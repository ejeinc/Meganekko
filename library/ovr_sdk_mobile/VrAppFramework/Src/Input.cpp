/************************************************************************************

Filename    :   Input.h
Content     :   Data passed to VrAppInterface::Frame().
Created     :   June 26, 2015
Authors     :   Jonathan E. Wright

Copyright   :   Copyright 2014 Oculus VR, LLC. All Rights reserved.

*************************************************************************************/

#include "Input.h"

#include "Kernel/OVR_LogUtils.h"
#if defined( OVR_OS_ANDROID )
#include <android/keycodes.h>
#else
#define WIN32_LEAN_AND_MEAN
#include <Windows.h>
#endif

namespace OVR {

struct ovrOSKeyInfo_t
{
	int				OScode;			// OS-specific code
	ovrInputDevice	InputDevice;	// type of device this input must be associated with
};

// Note that OVR_KEY codes do not always have an equivalent OS key code (AKEYCODE on Android
// or Virtual Key on Windows). Usually this is because the key can only be sent by a combination of 
// another key + shift. This may not be true for all keyboards in all locales or across devices.
// For instance SysRq has a key code on Android, but no virtual key on Windows.

#if defined( OVR_OS_ANDROID )
// Android key codes
static ovrOSKeyInfo_t const OvrKeyToAndroidKeyMap[OVR_KEY_MAX] =
{
	{ 0,							OVR_INPUT_DEVICE_ANY },	// OVR_KEY_NONE -- used as a default if we recieve a Android key we don't have mapped

	{ AKEYCODE_CTRL_LEFT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_CTRL_RIGHT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_SHIFT_LEFT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_SHIFT_RIGHT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_ALT_LEFT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_ALT_RIGHT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_MENU,				OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_MAX },	// marker 1

	{ AKEYCODE_DPAD_UP,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_DPAD_DOWN,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_DPAD_LEFT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_DPAD_RIGHT,			OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_MAX },	// marker 2

	{ AKEYCODE_F1,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F2,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F3,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F4,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F5,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F6,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F7,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F8,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F9,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F10,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F11,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_F12,					OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_MAX },	// marker 3

	{ AKEYCODE_ENTER,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_SPACE,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_INSERT,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_FORWARD_DEL,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_MOVE_HOME,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_MOVE_END,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_PAGE_UP,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_PAGE_DOWN,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_SCROLL_LOCK,			OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// OVR_KEY_PAUSE - no equivalent
	{ -1,							OVR_INPUT_DEVICE_ANY },	// OVR_KEY_PRINT_SCREEN - no equivalent
	{ AKEYCODE_NUM_LOCK,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_CAPS_LOCK,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_BACK,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_SYSRQ,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_BREAK,				OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_MAX }, // marker 4

	{ AKEYCODE_NUMPAD_DIVIDE,		OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_MULTIPLY,		OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_SUBTRACT,		OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_SUBTRACT,		OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_ENTER,		OVR_INPUT_DEVICE_ANY },	// keypad enter
	{ AKEYCODE_NUMPAD_DOT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_0,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_1,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_2,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_3,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_4,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_5,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_6,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_7,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_8,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_NUMPAD_9,			OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_MAX },	// marker 5

	{ AKEYCODE_TAB,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_COMMA,				OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_PERIOD,				OVR_INPUT_DEVICE_ANY },
	{ 0,							OVR_INPUT_DEVICE_ANY },	// <
	{ 0,							OVR_INPUT_DEVICE_ANY },	// >
	{ AKEYCODE_SLASH,				OVR_INPUT_DEVICE_ANY },	// /
	{ AKEYCODE_BACKSLASH,			OVR_INPUT_DEVICE_ANY },	/* \ */
	{ 0,							OVR_INPUT_DEVICE_ANY },	// ? = slash + shift
	{ AKEYCODE_SEMICOLON,			OVR_INPUT_DEVICE_ANY },	// ;
	{ 0,							OVR_INPUT_DEVICE_ANY },	// : = semicolon + shift
	{ AKEYCODE_APOSTROPHE,			OVR_INPUT_DEVICE_ANY },	// '
	{ 0,							OVR_INPUT_DEVICE_ANY },	// " = ' + shift
	{ AKEYCODE_LEFT_BRACKET,		OVR_INPUT_DEVICE_ANY },	// [
	{ AKEYCODE_RIGHT_BRACKET,		OVR_INPUT_DEVICE_ANY },	// ]
	{ 0,							OVR_INPUT_DEVICE_ANY },	// { = shift + [
	{ 0,							OVR_INPUT_DEVICE_ANY },	// } = shift + ]
	{ 0,							OVR_INPUT_DEVICE_ANY },	/* | = shift + \ */
	{ 0,							OVR_INPUT_DEVICE_ANY },	// ~ = shift + `
	{ AKEYCODE_GRAVE,				OVR_INPUT_DEVICE_ANY },	// '
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 6

	{ AKEYCODE_1,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_2,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_3,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_4,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_5,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_6,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_7,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_8,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_9,					OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_0,					OVR_INPUT_DEVICE_ANY },

	{ 0,							OVR_INPUT_DEVICE_ANY },	// !
	{ 0,							OVR_INPUT_DEVICE_ANY },	// @
	{ 0,							OVR_INPUT_DEVICE_ANY },	// #
	{ 0,							OVR_INPUT_DEVICE_ANY },	// $
	{ 0,							OVR_INPUT_DEVICE_ANY },	// %
	{ 0,							OVR_INPUT_DEVICE_ANY },	// ^
	{ 0,							OVR_INPUT_DEVICE_ANY },	// &
	{ 0,							OVR_INPUT_DEVICE_ANY },	// *
	{ 0,							OVR_INPUT_DEVICE_ANY },	// (
	{ 0,							OVR_INPUT_DEVICE_ANY },	// )
	{ AKEYCODE_MINUS,				OVR_INPUT_DEVICE_ANY },	// -
	{ 0,							OVR_INPUT_DEVICE_ANY },	// _
	{ 0,							OVR_INPUT_DEVICE_ANY },	// +
	{ AKEYCODE_EQUALS,				OVR_INPUT_DEVICE_ANY },	// =
	{ AKEYCODE_DEL,					OVR_INPUT_DEVICE_ANY },	// backspace
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 7

	{ AKEYCODE_A,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_B,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_C,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_D,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_E,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_F,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_G,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_H,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_I,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_J,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_K,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_L,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_M,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_N,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_O,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_P,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_Q,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_R,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_S,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_T,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_U,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_V,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_W,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_X,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_Y,					OVR_INPUT_DEVICE_ANY },	
	{ AKEYCODE_Z,					OVR_INPUT_DEVICE_ANY },	
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 8

	{ AKEYCODE_VOLUME_MUTE,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_VOLUME_UP,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_VOLUME_DOWN,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_MEDIA_NEXT,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_MEDIA_PREVIOUS,		OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_MEDIA_STOP,			OVR_INPUT_DEVICE_ANY },
	{ AKEYCODE_MEDIA_PLAY_PAUSE,	OVR_INPUT_DEVICE_ANY },
	{ 0,							OVR_INPUT_DEVICE_ANY },	// launch app 1 - no equivalent
	{ 0,							OVR_INPUT_DEVICE_ANY },	// launch app 2 - no equivalent
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 9

	{ AKEYCODE_BUTTON_A,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_B,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_C,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_X,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_Y,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_Z,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_START,		OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_SELECT,		OVR_INPUT_DEVICE_JOYPAD },
//	{ AKEYCODE_MENU,				OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_L1,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_R1,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_DPAD_UP,				OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_DPAD_DOWN,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_DPAD_LEFT,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_DPAD_RIGHT,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_13,			OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_UP
	{ AKEYCODE_BUTTON_14,			OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_DOWN
	{ AKEYCODE_BUTTON_15,			OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_LEFT
	{ AKEYCODE_BUTTON_16,			OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_RIGHT
	{ AKEYCODE_LANGUAGE_SWITCH,		OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_UP
	{ AKEYCODE_MANNER_MODE,			OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_DOWN
	{ AKEYCODE_3D_MODE,				OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_LEFT
	{ AKEYCODE_CONTACTS,			OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_RIGHT
	{ AKEYCODE_BUTTON_L2,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_R2,			OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_THUMBL,		OVR_INPUT_DEVICE_JOYPAD },
	{ AKEYCODE_BUTTON_THUMBR,		OVR_INPUT_DEVICE_JOYPAD },
	{ -1,							OVR_INPUT_DEVICE_JOYPAD }	// marker 10
};
#else
// Windows virtual keys
static ovrOSKeyInfo_t const OvrKeyToVirtualKeyMap[OVR_KEY_MAX] =
{
	{ 0, OVR_INPUT_DEVICE_ANY },	// OVR_KEY_NONE -- used as a default if we recieve a VK_ key we don't have mapped

	{ VK_LCONTROL,					OVR_INPUT_DEVICE_ANY },
	{ VK_RCONTROL,					OVR_INPUT_DEVICE_ANY },
	{ VK_LSHIFT,					OVR_INPUT_DEVICE_ANY },
	{ VK_RSHIFT,					OVR_INPUT_DEVICE_ANY },
	{ VK_LMENU,						OVR_INPUT_DEVICE_ANY },
	{ VK_RMENU,						OVR_INPUT_DEVICE_ANY },
	{ VK_MENU,						OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 1

	{ VK_UP,						OVR_INPUT_DEVICE_ANY },
	{ VK_DOWN,						OVR_INPUT_DEVICE_ANY },
	{ VK_LEFT,						OVR_INPUT_DEVICE_ANY },
	{ VK_RIGHT,						OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 2

	{ VK_F1,						OVR_INPUT_DEVICE_ANY },
	{ VK_F2,						OVR_INPUT_DEVICE_ANY },
	{ VK_F3,						OVR_INPUT_DEVICE_ANY },
	{ VK_F4,						OVR_INPUT_DEVICE_ANY },
	{ VK_F5,						OVR_INPUT_DEVICE_ANY },
	{ VK_F6,						OVR_INPUT_DEVICE_ANY },
	{ VK_F7,						OVR_INPUT_DEVICE_ANY },
	{ VK_F8,						OVR_INPUT_DEVICE_ANY },
	{ VK_F9,						OVR_INPUT_DEVICE_ANY },
	{ VK_F10,						OVR_INPUT_DEVICE_ANY },
	{ VK_F11,						OVR_INPUT_DEVICE_ANY },
	{ VK_F12,						OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 3

	{ VK_RETURN,					OVR_INPUT_DEVICE_ANY },
	{ VK_SPACE,						OVR_INPUT_DEVICE_ANY },
	{ VK_INSERT,					OVR_INPUT_DEVICE_ANY },
	{ VK_DELETE,					OVR_INPUT_DEVICE_ANY },
	{ VK_HOME,						OVR_INPUT_DEVICE_ANY },
	{ VK_END,						OVR_INPUT_DEVICE_ANY },
	{ VK_PRIOR,						OVR_INPUT_DEVICE_ANY },
	{ VK_NEXT,						OVR_INPUT_DEVICE_ANY },
	{ VK_SCROLL,					OVR_INPUT_DEVICE_ANY },
	{ VK_PAUSE,						OVR_INPUT_DEVICE_ANY },
	{ VK_PRINT,						OVR_INPUT_DEVICE_ANY },
	{ VK_NUMLOCK,					OVR_INPUT_DEVICE_ANY },
	{ VK_CAPITAL,					OVR_INPUT_DEVICE_ANY },
	{ VK_ESCAPE,					OVR_INPUT_DEVICE_ANY },
	{ 0,							OVR_INPUT_DEVICE_ANY },	// no virtual key for SysRq
	{ VK_CANCEL,					OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY }, // marker 4

	{ VK_DIVIDE,					OVR_INPUT_DEVICE_ANY },
	{ VK_MULTIPLY,					OVR_INPUT_DEVICE_ANY },
	{ VK_ADD,						OVR_INPUT_DEVICE_ANY },
	{ VK_SUBTRACT,					OVR_INPUT_DEVICE_ANY },
	{ VK_ACCEPT,					OVR_INPUT_DEVICE_ANY },		// keypad enter
	//{ VK_RETURN, OVR_INPUT_DEVICE_ANY },	// Windows uses the same key code for normal and keypad enter, OVR_INPUT_DEVICE_ANY }, but sets a bit in the iparam for keypad enter
	{ VK_DECIMAL,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD0,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD1,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD2,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD3,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD4,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD5,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD6,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD7,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD8,					OVR_INPUT_DEVICE_ANY },
	{ VK_NUMPAD9,					OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 5

	{ VK_TAB,						OVR_INPUT_DEVICE_ANY },
	{ VK_OEM_COMMA,					OVR_INPUT_DEVICE_ANY },
	{ VK_OEM_PERIOD,				OVR_INPUT_DEVICE_ANY },
	{ VK_OEM_COMMA,					OVR_INPUT_DEVICE_ANY },	// <
	{ VK_OEM_PERIOD,				OVR_INPUT_DEVICE_ANY },	// >
	{ VK_OEM_2,						OVR_INPUT_DEVICE_ANY },	// /
	{ VK_OEM_5,						OVR_INPUT_DEVICE_ANY },	/* \ */
	{ VK_OEM_2,						OVR_INPUT_DEVICE_ANY },	// ?
	{ VK_OEM_1,						OVR_INPUT_DEVICE_ANY },	// ;
	{ VK_OEM_1,						OVR_INPUT_DEVICE_ANY },	// ;
	{ VK_OEM_7,						OVR_INPUT_DEVICE_ANY },	// '
	{ VK_OEM_7,						OVR_INPUT_DEVICE_ANY },	// "
	{ VK_OEM_4,						OVR_INPUT_DEVICE_ANY },	// [
	{ VK_OEM_6,						OVR_INPUT_DEVICE_ANY },	// ]
	{ VK_OEM_4,						OVR_INPUT_DEVICE_ANY },	// {
	{ VK_OEM_6,						OVR_INPUT_DEVICE_ANY },	// }
	{ VK_OEM_5,						OVR_INPUT_DEVICE_ANY },	// |
	{ VK_OEM_3,						OVR_INPUT_DEVICE_ANY },	// ~
	{ VK_OEM_3, 					OVR_INPUT_DEVICE_ANY },	// '
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 6

	{ 0x30,							OVR_INPUT_DEVICE_ANY },	// 1
	{ 0x31,							OVR_INPUT_DEVICE_ANY },	// 2 
	{ 0x32,							OVR_INPUT_DEVICE_ANY },	// 3
	{ 0x33,							OVR_INPUT_DEVICE_ANY },	// 4
	{ 0x34,							OVR_INPUT_DEVICE_ANY },	// 5
	{ 0x35,							OVR_INPUT_DEVICE_ANY },	// 6
	{ 0x36,							OVR_INPUT_DEVICE_ANY },	// 7
	{ 0x37,							OVR_INPUT_DEVICE_ANY },	// 8
	{ 0x38,							OVR_INPUT_DEVICE_ANY },	// 9
	{ 0x39,							OVR_INPUT_DEVICE_ANY },	// 0
	{ 0x30,							OVR_INPUT_DEVICE_ANY },	// !
	{ 0x31,							OVR_INPUT_DEVICE_ANY },	// @
	{ 0x32,							OVR_INPUT_DEVICE_ANY },	// #
	{ 0x33,							OVR_INPUT_DEVICE_ANY },	// $
	{ 0x34,							OVR_INPUT_DEVICE_ANY },	// %
	{ 0x35,							OVR_INPUT_DEVICE_ANY },	// ^
	{ 0x36,							OVR_INPUT_DEVICE_ANY },	// &
	{ 0x37,							OVR_INPUT_DEVICE_ANY },	// *
	{ 0x38,							OVR_INPUT_DEVICE_ANY },	// (
	{ 0x39,							OVR_INPUT_DEVICE_ANY },	// )
	{ VK_OEM_MINUS,					OVR_INPUT_DEVICE_ANY },	// -
	{ VK_OEM_MINUS,					OVR_INPUT_DEVICE_ANY },	// _
	{ VK_OEM_PLUS,					OVR_INPUT_DEVICE_ANY },	// +
	{ VK_OEM_PLUS,					OVR_INPUT_DEVICE_ANY },	// =
	{ VK_BACK,						OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 7

	{ 0x41,							OVR_INPUT_DEVICE_ANY },
	{ 0x42,							OVR_INPUT_DEVICE_ANY },
	{ 0x43,							OVR_INPUT_DEVICE_ANY },
	{ 0x44,							OVR_INPUT_DEVICE_ANY },
	{ 0x45,							OVR_INPUT_DEVICE_ANY },
	{ 0x46,							OVR_INPUT_DEVICE_ANY },
	{ 0x47,							OVR_INPUT_DEVICE_ANY },
	{ 0x48,							OVR_INPUT_DEVICE_ANY },
	{ 0x49,							OVR_INPUT_DEVICE_ANY },
	{ 0x4a,							OVR_INPUT_DEVICE_ANY },
	{ 0x4b,							OVR_INPUT_DEVICE_ANY },
	{ 0x4c,							OVR_INPUT_DEVICE_ANY },
	{ 0x4d,							OVR_INPUT_DEVICE_ANY },
	{ 0x4e,							OVR_INPUT_DEVICE_ANY },
	{ 0x4f,							OVR_INPUT_DEVICE_ANY },
	{ 0x50,							OVR_INPUT_DEVICE_ANY },
	{ 0x51,							OVR_INPUT_DEVICE_ANY },
	{ 0x52,							OVR_INPUT_DEVICE_ANY },
	{ 0x53,							OVR_INPUT_DEVICE_ANY },
	{ 0x54,							OVR_INPUT_DEVICE_ANY },
	{ 0x55,							OVR_INPUT_DEVICE_ANY },
	{ 0x56,							OVR_INPUT_DEVICE_ANY },
	{ 0x57,							OVR_INPUT_DEVICE_ANY },
	{ 0x58,							OVR_INPUT_DEVICE_ANY },
	{ 0x59,							OVR_INPUT_DEVICE_ANY },
	{ 0x5a,							OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 8

	{ VK_VOLUME_MUTE,				OVR_INPUT_DEVICE_ANY },
	{ VK_VOLUME_UP,					OVR_INPUT_DEVICE_ANY },
	{ VK_VOLUME_DOWN,				OVR_INPUT_DEVICE_ANY },
	{ VK_MEDIA_NEXT_TRACK,			OVR_INPUT_DEVICE_ANY },
	{ VK_MEDIA_PREV_TRACK,			OVR_INPUT_DEVICE_ANY },
	{ VK_MEDIA_STOP,				OVR_INPUT_DEVICE_ANY },
	{ VK_MEDIA_PLAY_PAUSE,			OVR_INPUT_DEVICE_ANY },
	{ VK_LAUNCH_APP1,				OVR_INPUT_DEVICE_ANY },
	{ VK_LAUNCH_APP2,				OVR_INPUT_DEVICE_ANY },
	{ -1,							OVR_INPUT_DEVICE_ANY },	// marker 9

	// currently, OVR_INPUT_DEVICE_ANY }, gamepad / joystick buttons do not have key defaults on Windows because
	// windows does not send them as virtual key codes to WndProc. These have to be grabbed
	// with direct input and mapped to keys or buttons.
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// button A
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// button B
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// button C
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// button X
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// button Y
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// button Z
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// start
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// select
//	{ OVR_KEY_LALT,					OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_BUTTON_MENU
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// left trigger
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// right trigger
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// dpad up
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// dpad down
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// dpad left
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// dpad right
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_UP
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_DOWN
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_LEFT
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_LSTICK_RIGHT
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_UP
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_DOWN
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_LEFT
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// OVR_KEY_RSTICK_RIGHT
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// left shoulder
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// right shoulder
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// left thumb
	{ 0,							OVR_INPUT_DEVICE_JOYPAD },	// right thumb

	{ -1,							OVR_INPUT_DEVICE_ANY }	// marker 10
};
#endif	// OVR_OS_ANDROID

char const * GetNameForKeyCode( ovrKeyCode const keyCode )
{
	static const char * ovrKeyNames[OVR_KEY_MAX] =
	{
		"OVR_KEY_NONE",

		"OVR_KEY_LCONTROL",
		"OVR_KEY_RCONTROL",
		"OVR_KEY_LSHIFT",
		"OVR_KEY_RSHIFT",
		"OVR_KEY_LALT",
		"OVR_KEY_RALT",
		"OVR_KEY_MENU",
		"OVR_KEY_MARKER_1",

		"OVR_KEY_UP",
		"OVR_KEY_DOWN",
		"OVR_KEY_LEFT",
		"OVR_KEY_RIGHT",
		"OVR_KEY_MARKER_2",

		"OVR_KEY_F1",
		"OVR_KEY_F2",
		"OVR_KEY_F3",
		"OVR_KEY_F4",
		"OVR_KEY_F5",
		"OVR_KEY_F6",
		"OVR_KEY_F7",
		"OVR_KEY_F8",
		"OVR_KEY_F9",
		"OVR_KEY_F10",
		"OVR_KEY_F11",
		"OVR_KEY_F12",
		"OVR_KEY_MARKER_3",

		"OVR_KEY_RETURN",
		"OVR_KEY_SPACE",
		"OVR_KEY_INSERT",
		"OVR_KEY_DELETE",
		"OVR_KEY_HOME",
		"OVR_KEY_END",
		"OVR_KEY_PAGEUP",
		"OVR_KEY_PAGEDOWN",
		"OVR_KEY_SCROLL_LOCK",
		"OVR_KEY_PAUSE",
		"OVR_KEY_PRINT_SCREEN",
		"OVR_KEY_NUM_LOCK",
		"OVR_KEY_CAPSLOCK",
		"OVR_KEY_BACK = OVR_KEY_ESCAPE",	// escape and back are synonomous
		"OVR_KEY_SYS_REQ",
		"OVR_KEY_BREAK",
		"OVR_KEY_MARKER_4",		// DO NOT USE: just a marker to catch mismatches in the key map

		"OVR_KEY_KP_DIVIDE",		// / (forward slash) on numeric keypad
		"OVR_KEY_KP_MULTIPLY",	// * on numeric keypad
		"OVR_KEY_KP_ADD",			// + on numeric keypad
		"OVR_KEY_KP_SUBTRACT",	// - on numeric keypad
		"OVR_KEY_KP_ENTER",		// enter on numeric keypad
		"OVR_KEY_KP_DECIMAL",		// delete on numeric keypad
		"OVR_KEY_KP_0",
		"OVR_KEY_KP_1",
		"OVR_KEY_KP_2",
		"OVR_KEY_KP_3",
		"OVR_KEY_KP_4",
		"OVR_KEY_KP_5",
		"OVR_KEY_KP_6",
		"OVR_KEY_KP_7",
		"OVR_KEY_KP_8",
		"OVR_KEY_KP_9",
		"OVR_KEY_MARKER_5",		// DO NOT USE: just a marker to catch mismatches in the key map

		"OVR_KEY_TAB",
		"OVR_KEY_COMMA",			// ",
		"OVR_KEY_PERIOD",			// .
		"OVR_KEY_LESS",			// <
		"OVR_KEY_GREATER",		// >
		"OVR_KEY_FORWARD_SLASH",	// /
		"OVR_KEY_BACK_SLASH",		/* \ */
		"OVR_KEY_QUESTION_MARK",	// ?
		"OVR_KEY_SEMICOLON",		// ;
		"OVR_KEY_COLON",			// :
		"OVR_KEY_APOSTROPHE",		// '
		"OVR_KEY_QUOTE",			// "
		"OVR_KEY_OPEN_BRACKET",	// [
		"OVR_KEY_CLOSE_BRACKET",	// ]
		"OVR_KEY_CLOSE_BRACE",	// {
		"OVR_KEY_OPEN_BRACE",		// }
		"OVR_KEY_BAR",			// |
		"OVR_KEY_TILDE",			// ~
		"OVR_KEY_GRAVE",			// `
		"OVR_KEY_MARKER_6",		// DO NOT USE: just a marker to catch mismatches in the key map

		"OVR_KEY_1",
		"OVR_KEY_2",
		"OVR_KEY_3",
		"OVR_KEY_4",
		"OVR_KEY_5",
		"OVR_KEY_6",
		"OVR_KEY_7",
		"OVR_KEY_8",
		"OVR_KEY_9",
		"OVR_KEY_0",
		"OVR_KEY_EXCLAMATION",	// !
		"OVR_KEY_AT",				// @
		"OVR_KEY_POUND",			// #
		"OVR_KEY_DOLLAR",			// $
		"OVR_KEY_PERCENT",		// %
		"OVR_KEY_CARET",			// ^
		"OVR_KEY_AMPERSAND",		// &
		"OVR_KEY_ASTERISK",		// *
		"OVR_KEY_OPEN_PAREN",		// (
		"OVR_KEY_CLOSE_PAREN",	// )
		"OVR_KEY_MINUS",			// -
		"OVR_KEY_UNDERSCORE",		// _
		"OVR_KEY_PLUS",			// +
		"OVR_KEY_EQUALS",			// =
		"OVR_KEY_BACKSPACE",		//
		"OVR_KEY_MARKER_7",		// DO NOT USE: just a marker to catch mismatches in the key map

		"OVR_KEY_A",
		"OVR_KEY_B",
		"OVR_KEY_C",
		"OVR_KEY_D",
		"OVR_KEY_E",
		"OVR_KEY_F",
		"OVR_KEY_G",
		"OVR_KEY_H",
		"OVR_KEY_I",
		"OVR_KEY_J",
		"OVR_KEY_K",
		"OVR_KEY_L",
		"OVR_KEY_M",
		"OVR_KEY_N",
		"OVR_KEY_O",
		"OVR_KEY_P",
		"OVR_KEY_Q",
		"OVR_KEY_R",
		"OVR_KEY_S",
		"OVR_KEY_T",
		"OVR_KEY_U",
		"OVR_KEY_V",
		"OVR_KEY_W",
		"OVR_KEY_X",
		"OVR_KEY_Y",
		"OVR_KEY_Z",
		"OVR_KEY_MARKER_8",		// DO NOT USE: just a marker to catch mismatches in the key map

		"OVR_KEY_VOLUME_MUTE",
		"OVR_KEY_VOLUME_UP",
		"OVR_KEY_VOLUME_DOWN",
		"OVR_KEY_MEDIA_NEXT_TRACK",
		"OVR_KEY_MEDIA_PREV_TRACK",
		"OVR_KEY_MEDIA_STOP",
		"OVR_KEY_MEDIA_PLAY_PAUSE",
		"OVR_KEY_LAUNCH_APP1",
		"OVR_KEY_LAUNCH_APP2",
		"OVR_KEY_MARKER_9",		// DO NOT USE: just a marker to catch mismatches in the key map

		"OVR_KEY_BUTTON_A",
		"OVR_KEY_BUTTON_B",
		"OVR_KEY_BUTTON_C",
		"OVR_KEY_BUTTON_X",
		"OVR_KEY_BUTTON_Y",
		"OVR_KEY_BUTTON_Z",
		"OVR_KEY_BUTTON_START",
		"OVR_KEY_BUTTON_SELECT",
//		"OVR_KEY_BUTTON_MENU",
		"OVR_KEY_LEFT_TRIGGER = OVR_KEY_BUTTON_L1",
		"OVR_KEY_RIGHT_TRIGGER = OVR_KEY_BUTTON_R1",
		"OVR_KEY_DPAD_UP",
		"OVR_KEY_DPAD_DOWN",
		"OVR_KEY_DPAD_LEFT",
		"OVR_KEY_DPAD_RIGHT",
		"OVR_KEY_LSTICK_UP",
		"OVR_KEY_LSTICK_DOWN",
		"OVR_KEY_LSTICK_LEFT",
		"OVR_KEY_LSTICK_RIGHT",
		"OVR_KEY_RSTICK_UP",
		"OVR_KEY_RSTICK_DOWN",
		"OVR_KEY_RSTICK_LEFT",
		"OVR_KEY_RSTICK_RIGHT",

		"OVR_KEY_BUTTON_LEFT_SHOULDER = OVR_KEY_BUTTON_L2",
		"OVR_KEY_BUTTON_RIGHT_SHOULDER = OVR_KEY_BUTTON_R2",
		"OVR_KEY_BUTTON_LEFT_THUMB = OVR_KEY_BUTTON_THUMBL",
		"OVR_KEY_BUTTON_RIGHT_THUMB = OVR_KEY_BUTTON_THUMBR",
		"OVR_KEY_MARKER_10"
	};
	return ovrKeyNames[keyCode];
}

char GetAsciiForKeyCode( ovrKeyCode const keyCode, bool const shiftDown )
{
	static char ovrAsciiChars[OVR_KEY_MAX][2] =
	{
		{ 0, 0 },

		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },

		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },

		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },

		{ 0, 0 },
		{ ' ', ' ' },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },

		{ '/', '/' },
		{ '*', '*' },
		{ '+', '+' },
		{ '-', '-' },
		{ 0, 0 },
		{ '.', '.' },
		{ '0', '0' },
		{ '1', '1' },
		{ '2', '2' },
		{ '3', '3' },
		{ '4', '4' },
		{ '5', '5' },
		{ '6', '6' },
		{ '7', '7' },
		{ '8', '8' },
		{ '9', '9' },
		{ 0, 0 },

		{ 0, 0 },
		{ ',', '<' },
		{ '.', '>' },
		{ '<', '<' },
		{ '>', '>' },
		{ '/', '?' },
		{ '\\', '|' },
		{ '?', '?' },
		{ ';', ':' },
		{ ':', ':' },
		{ '\'', '"' },
		{ '"', '"' },
		{ '[', '{' },
		{ ']', '}' },
		{ '{', '{' },
		{ '}', '}' },
		{ '|', '|' },
		{ '~', '~' },
		{ '`', '~' },
		{ 0, 0 },

		{ '1', '!' },
		{ '2', '@' },
		{ '3', '#' },
		{ '4', '$' },
		{ '5', '%' },
		{ '6', '^' },
		{ '7', '&' },
		{ '8', '*' },
		{ '9', '(' },
		{ '0', ')' },
		{ '!', '!' },
		{ '@', '@' },
		{ '#', '#' },
		{ '$', '$' },
		{ '%', '%' },
		{ '^', '^' },
		{ '&', '&' },
		{ '*', '*' },
		{ '(', '(' },
		{ ')', ')' },
		{ '-', '_' },
		{ '_', '_' },
		{ '+', '+' },
		{ '=', '+' },
		{ 0, 0 },
		{ 0, 0 },

		{ 'a', 'A' },
		{ 'b', 'B' },
		{ 'c', 'C' },
		{ 'd', 'D' },
		{ 'e', 'E' },
		{ 'f', 'F' },
		{ 'g', 'G' },
		{ 'h', 'H' },
		{ 'i', 'I' },
		{ 'j', 'J' },
		{ 'k', 'K' },
		{ 'l', 'L' },
		{ 'm', 'M' },
		{ 'n', 'N' },
		{ 'o', 'O' },
		{ 'p', 'P' },
		{ 'q', 'Q' },
		{ 'r', 'R' },
		{ 's', 'S' },
		{ 't', 'T' },
		{ 'u', 'U' },
		{ 'v', 'V' },
		{ 'w', 'W' },
		{ 'x', 'X' },
		{ 'y', 'Y' },
		{ 'z', 'Z' },
		{ 0, 0 },

		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },

		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
//		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },

		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 }
	};
	return ovrAsciiChars[keyCode][shiftDown ? 1 : 0];
}

struct ovrKeyInfo_t
{
	ovrKeyInfo_t()
	: KeyCode( OVR_KEY_NONE )
	, InputDevice( OVR_INPUT_DEVICE_ANY )
	{
	}

	ovrKeyCode		KeyCode;
	ovrInputDevice	InputDevice;
};

static void GenerateOSKeyToKeyCodeMap( const ovrOSKeyInfo_t * ovrKeyMap, 
		ovrKeyInfo_t * & osKeyMap, int & mapSize )
{
	// verify the ovrKeyMap array is not out of sync with the ovrKeyCode enums
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_1].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_2].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_3].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_4].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_5].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_6].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_7].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_8].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_9].OScode == -1 );
	OVR_ASSERT( ovrKeyMap[OVR_KEY_MARKER_10].OScode == -1 );

	// scan to find the maximum virtual key
	int maxVirtualKey = -1;
	for ( int i = 0; i < OVR_KEY_MAX; ++i )
	{
		if ( ovrKeyMap[i].OScode > maxVirtualKey )
		{
			maxVirtualKey = ovrKeyMap[i].OScode;
		}
	}

	mapSize = maxVirtualKey + 1;
	OVR_ASSERT( mapSize < 1024 );	// let us know if this gets out of hand
	osKeyMap = new ovrKeyInfo_t[mapSize];
	// start at 1 because 0 is an invalid OS key code and OVR_KEY_ codes that don't have scan 
	// code equivalent default to 0.
	for ( int i = 1; i < mapSize; ++i )
	{
		for ( int j = 0; j < OVR_KEY_MAX; ++j )
		{
			if ( ovrKeyMap[j].OScode == i )
			{
				OVR_ASSERT( osKeyMap[i].KeyCode == OVR_KEY_NONE );
				osKeyMap[i].KeyCode = static_cast< ovrKeyCode >( j );
				osKeyMap[i].InputDevice = ovrKeyMap[j].InputDevice;
				// exit the current loop so that we don't overwrite this entry if there's 
				// another OS key code that maps to the same OVR_KEY code
				break;  
			}
		}
	}
}

static ovrKeyInfo_t * OSKeyToKeyCodeMap = NULL;
static int OSKeyToKeyCodeMapSize = 0;

void InitInput()
{
#if defined( OVR_OS_ANDROID )
	GenerateOSKeyToKeyCodeMap( OvrKeyToAndroidKeyMap, OSKeyToKeyCodeMap, OSKeyToKeyCodeMapSize );
#else
	GenerateOSKeyToKeyCodeMap( OvrKeyToVirtualKeyMap, OSKeyToKeyCodeMap, OSKeyToKeyCodeMapSize );
#endif
}

ovrKeyCode OSKeyToKeyCode( int const osKey )
{
	OVR_ASSERT( OSKeyToKeyCodeMap != NULL );	// InitInput() must be called before this

	// Java will send joystick buttons with an extra flag OR'd in
	bool const fromJoypad = ( osKey & BUTTON_JOYPAD_FLAG ) != 0;
	OVR_UNUSED( fromJoypad );

	int const k = osKey & ( ~BUTTON_JOYPAD_FLAG );
	if ( k < 0 || k >= OSKeyToKeyCodeMapSize )
	{
		LOG( "OS key %i is outside of map range", k );
		OVR_ASSERT( k >= 0 && k < OSKeyToKeyCodeMapSize );
		return OVR_KEY_NONE;
	}
	ovrKeyInfo_t const & osKeyInfo = OSKeyToKeyCodeMap[k];
	ovrKeyCode keyCode = osKeyInfo.KeyCode;
	ovrInputDevice const device = osKeyInfo.InputDevice;

	// this emulates the original behavior where joypad buttons had an extra bit OR'd in or they 
	// would not be recognized
	/// FIXME: is this really necessary?
	if ( fromJoypad && device != OVR_INPUT_DEVICE_JOYPAD )
	{
		if ( keyCode == OVR_KEY_ESCAPE )
		{
			// HACK: Moga joysticks seem to return BACK / ESCAPE for the "select" button
			// Since we know this is from a joystick, we can remap it here to select.
			keyCode = OVR_KEY_BUTTON_SELECT;
		}
		else if ( keyCode == OVR_KEY_LEFT || keyCode == OVR_KEY_RIGHT || keyCode == OVR_KEY_UP || keyCode == OVR_KEY_DOWN )
		{
			// HACK: for 1.0.3 release, translate direction keys to dpad buttons
			keyCode = static_cast< ovrKeyCode >( keyCode + ( OVR_KEY_DPAD_UP - OVR_KEY_UP ) );
		}
		// we can get OVR_KEY_MENU from a joystick or possibly (in the future) the HMT	
		else if ( keyCode != OVR_KEY_MENU )
		{
			// this may be acceptable, but for now, assert and return no key
			OVR_ASSERT( fromJoypad && device == OVR_INPUT_DEVICE_JOYPAD );
			LOG( "OSKeyToKeyCode: got a button key from something other than a joypad!");
			return OVR_KEY_NONE;
		}
	}
	//LOG( "OSKeyToKeyCode got %s%s", GetNameForKeyCode( keyCode ), fromJoypad ? " from joypad." : "." );
	return keyCode;
}

} // namespace OVR