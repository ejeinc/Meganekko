/************************************************************************************

Filename    :   DebugConsole.h
Content     :   Handles the Debug Console
Created     :   Oct 1, 2015
Authors     :   Eric Duhon

Copyright   :   Copyright 2015 Oculus VR, Inc. All Rights reserved.

************************************************************************************/

//TODO: Future Work
// 1) Possibly Merge with Console.h/cpp
// 2) Move to a system activity
// 3) Better word breaks, currently text will get cut in the middle of a word.
// 4) add blinking cursor (or even just having a cursor would be an improvment)
// 5) let left and right arrow keys move cursor, so you can edit a command without having to retype the whole thing
// 6) auto complete with commands registered with Console.h?
// 7) log filters? if we only want to see warnings
// 8) Wire it into logs, and racket.
// 9) console isn't stealing input fully. as your typing things will happen in cinema. This issue would just go away if console is a system activity.

#ifndef OVR_DEBUG_CONSOLE_H
#define OVR_DEBUG_CONSOLE_H

#include "App.h"

namespace OVR
{
	namespace DebugConsole
	{
		void Init( VrAppInterface * appInterface );
		void Shutdown();
		bool OnKeyEvent( const int keyCode, const int repeatCount, const OVR::KeyEventType eventType );
		void BeginFrame();
		void EndFrame();
		void Frame( const ovrFrameInput & vrFrame );
		void DrawEyeView( const int eye, const float fovDegreesX, const float fovDegreesY,
						  ovrFrameParms & frameParms, ovrSurfaceRender & surfaceRender );

		//Will render in default console color, which may be easier to read than other colors
		void Log( const char * fmt, ... );
		void Log( const Vector3f color, const char * fmt, ... );
	}
}

#endif	// OVR_DEBUG_CONSOLE_H