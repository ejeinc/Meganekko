/************************************************************************************

Filename    :   DebugConsole.h
Content     :   Handles the Debug Console
Created     :   Oct 1, 2015
Authors     :   Eric Duhon

Copyright   :   Copyright 2015 Oculus VR, Inc. All Rights reserved.

************************************************************************************/

#include "DebugConsole.h"
#include "Kernel/OVR_LogUtils.h"
#include "Kernel/OVR_String.h"
#include "Kernel/OVR_Deque.h"
#include "BitmapFont.h"
#include "PackageFiles.h"
#include "EyeBuffers.h"
#include "Console.h"
#include "VrApi.h"
#include "Kernel/OVR_Lexer.h"
#include "Kernel/OVR_String_Utils.h"
#include "VrApi_LocalPrefs.h"

#define LOCAL_PREF_VRAPI_DEBUG_CONSOLE "dev_debugConsole" // "0" = off, "1" = on

namespace OVR
{

class DebugConsoleSingleton
{
public:
	DebugConsoleSingleton( const DebugConsoleSingleton & ) = delete;
	DebugConsoleSingleton & operator=( const DebugConsoleSingleton & ) = delete;

	static DebugConsoleSingleton & Instance();
	void	Init( VrAppInterface * appInterface );
	void	Shutdown();
	void	Frame( const ovrFrameInput & vrFrame );
	void	BeginFrame();
	void	EndFrame();
	void	DrawEyeView( const int eye, const float fovDegreesX, const float fovDegreesY,
						 ovrFrameParms & frameParms, ovrSurfaceRender & surfaceRender );
	void	Log( const char * fmt, va_list args );
	void	Log( const Vector3f& color, const char * fmt, va_list args );
	bool	OnKeyEvent( const int keyCode, const int repeatCount, const OVR::KeyEventType eventType );

private:
	struct LogEntry
	{
		LogEntry() {}
		LogEntry( Vector3f color, const char* text ) : Color( color, 1.0f ), Text( text ) {}

		Vector4f Color;
		String Text;
	};
	DebugConsoleSingleton() = default;
	~DebugConsoleSingleton() = default;

	void	SetConsoleTextColor( const char * colorText );
	void	SetConsoleTextSize( const char * sizeText );
	void	SetConsoleTextPosition( const char * positionText );
	void	SetConsoleWidth( const char * widthText );
	void	GetConsoleTextSettings( const char * );
	void	PrintConsoleTestText( const char * );

	static const int MaxLogEntries = 1024;
	static const int MaxLinesToDisplay = 128;
	static const int MaxRecentCommands = 16;
	static const int MaxCharsPerLine = 128; // text has to be unreadably small, or the console fairly wide to go over this.
	static const int MinLinesToDisplay = 8; // For scrolling, keep at least this many lines on screen if there are this many.
	static const char * Prompt;

	CircularBuffer<LogEntry>	LogEntries;
	CircularBuffer<String>		RecentCommands;
	int							RecentCommandIndex { 0 };
	char						Command[MaxCharsPerLine];
	BitmapFont *				Font { nullptr };
	BitmapFontSurface *			FontSurface { nullptr };
	ovrEyeBuffers *				EyeBuffers { nullptr };
	bool						DisplayConsole { false };
	bool						ShiftDown[2]; //left, right
	VrAppInterface *			AppInterface;
	int							NumCharsPerLine { MaxCharsPerLine };
	int							ScrollPosition { 0 };

	Vector3f					TextDefaultColor { 0.0f, 1.0f, 0.0f }; //green has highest legibility on pentile
	float						TextSize { 0.04f };
	Vector3f					TextPosition { -0.08f, -0.07f, -0.35f };
	float						ConsoleWidth { 0.085f };

	ovrTracking					CurrentTracking;
	ovrMatrix4f					CenterEyeTransform;
};

const char * DebugConsoleSingleton::Prompt = ">";

DebugConsoleSingleton & DebugConsoleSingleton::Instance()
{
	static DebugConsoleSingleton console;
	return console;
}

void DebugConsoleSingleton::Init( VrAppInterface * appInterface )
{
	if ( OVR_strcmp( ovr_GetLocalPreferenceValueForKey( LOCAL_PREF_VRAPI_DEBUG_CONSOLE, "0" ), "1" ) == 0 )
	{
		ShiftDown[0] = ShiftDown[1] = false;
		AppInterface = appInterface;
		Font = BitmapFont::Create();
		if ( !Font )
		{
			FAIL( "Failed to create a bitmap font" );
		}
		// Must be a monospace font.
		if ( !Font->Load( appInterface->app->GetFileSys(), "apk:///res/raw/debug_console.fnt" ) )
		{
			BitmapFont::Free( Font );
			Font = nullptr;
			WARN( "Could not load DebugConsole.fnt" );
			return;
		}
		FontSurface = BitmapFontSurface::Create();
		if ( !FontSurface )
		{
			FAIL( "Failed to create a bitmap font surface" );
		}
		static const int vertsPerChar = 4;
		FontSurface->Init( MaxLinesToDisplay * MaxCharsPerLine * vertsPerChar );

		ovrEyeBufferParms eyeBufferParams;
		eyeBufferParams.depthFormat = DEPTH_0;
		eyeBufferParams.colorFormat = COLOR_5551; //don't really need high quality color for a text console
		eyeBufferParams.multisamples = 1; //we only render sdf text, so no need for additional antialiasing
		// TODO: Could use a function in vrapi that returns something like highest useful resolution, for now these numbers work on currently supported devices
		eyeBufferParams.resolutionWidth = 1536;
		eyeBufferParams.resolutionHeight = 1536;

		EyeBuffers = new ovrEyeBuffers { };
		EyeBuffers->Initialize( eyeBufferParams, false );

		RegisterConsoleFunction( "SetConsoleTextColor",		[] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().SetConsoleTextColor( cmd );	} );
		RegisterConsoleFunction( "SetConsoleTextSize",		[] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().SetConsoleTextSize( cmd ); } );
		RegisterConsoleFunction( "SetConsoleTextPosition",  [] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().SetConsoleTextPosition( cmd ); } );
		RegisterConsoleFunction( "SetConsoleWidth",			[] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().SetConsoleWidth( cmd );	} );

		RegisterConsoleFunction( "GetConsoleSettings",		[] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().GetConsoleTextSettings( cmd ); } );
		RegisterConsoleFunction( "PrintConsoleTestText",	[] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().PrintConsoleTestText( cmd ); } );
		
		RegisterConsoleFunction( "List", [] ( void*, const char * )
		{
			DebugConsole::Log( "Registered Console Commands:" );
			GetConsoleCmds( [] ( const char * cmd )
			{
				DebugConsole::Log( cmd );
			} );
		} );

		float charWidth, charHeight, charAdvancex, charAdvancey;
		Font->GetGlyphMetrics( 'a', charWidth, charHeight, charAdvancex, charAdvancey ); //assume monospace
		NumCharsPerLine = ( int )Alg::Clamp<int>( ( int )( ConsoleWidth / ( charAdvancex * TextSize ) ), 1, MaxCharsPerLine );

		StringUtils::Copy( Command, Prompt );
	}
}

void DebugConsoleSingleton::Shutdown()
{
	BitmapFontSurface::Free( FontSurface );
	BitmapFont::Free( Font );
	delete EyeBuffers;

	FontSurface = nullptr;
	Font = nullptr;
	EyeBuffers = nullptr;
}

void DebugConsoleSingleton::BeginFrame()
{
	if ( !DisplayConsole || !FontSurface )
	{
		return;
	}

	EyeBuffers->BeginFrame();
}

void DebugConsoleSingleton::EndFrame()
{
	if ( !DisplayConsole || !FontSurface )
	{
		return;
	}

	EyeBuffers->EndFrame();
}

void DebugConsoleSingleton::Frame( const ovrFrameInput & vrFrame )
{
	if ( !DisplayConsole || !FontSurface )
	{
		return;
	}
	
	fontParms_t fontParams;
	Vector3f logTextPosition = TextPosition;
	int linesPrinted = 0; // have to include command lines in this count, we only have enough vertices for MaxLinesToDisplay
	// print out current command, we wont crawl commands up , instead if they go past the line they will crawl down until they are commited (press enter) and join the log.
	{
		Vector3f commandTextPosition = TextPosition;
		int currentPos = 0;
		int commandLength = static_cast<int>( OVR_strlen( Command ) );
		while ( currentPos < commandLength && linesPrinted < MaxLinesToDisplay )
		{
			int endPos = Alg::Min<int>( currentPos + NumCharsPerLine, commandLength );
			OVR_ASSERT( endPos < (int)sizeof( Command ) );
			char temp = Command[endPos]; //save making a copy of the string
			Command[endPos] = '\0';
			auto positionStep = FontSurface->DrawText3D( *Font, fontParams, commandTextPosition, Vector3f( 0.0f, 0.0f, 1.0f ),
															Vector3f( 0.0f, 1.0f, 0.0f ), TextSize, Vector4f( TextDefaultColor, 1.0f ), &Command[currentPos] );
			commandTextPosition += positionStep;
			if ( currentPos == 0 )
			{
				logTextPosition -= positionStep;
			}
			Command[endPos] = temp;
			currentPos = endPos;
			++linesPrinted;
		}
	}

	for ( int i = ScrollPosition; i < Alg::Min<int>(ScrollPosition + ( MaxLinesToDisplay - linesPrinted ), static_cast<int>( LogEntries.GetSize() ) ); ++i )
	{
		logTextPosition -= FontSurface->DrawText3D( *Font, fontParams, logTextPosition, Vector3f( 0.0f, 0.0f, 1.0f ),
														Vector3f( 0.0f, 1.0f, 0.0f ), TextSize, LogEntries.PeekFront( i ).Color, LogEntries.PeekFront( i ).Text.ToCStr() );
	}

	const ovrMatrix4f input = Matrix4f::Identity();
	CenterEyeTransform = vrapi_GetCenterEyeTransform( &AppInterface->app->GetHeadModelParms(), &vrFrame.Tracking, &input );
	CenterEyeTransform = ovrMatrix4f_Inverse( &CenterEyeTransform );

	FontSurface->Finish( CenterEyeTransform );
}

void DebugConsoleSingleton::DrawEyeView( const int eye, const float fovDegreesX, const float fovDegreesY,
										 ovrFrameParms & frameParms,  ovrSurfaceRender & surfaceRender )
{
	if ( !DisplayConsole || FontSurface == nullptr )
	{
		return;
	}

	const Matrix4f viewMatrix = vrapi_GetEyeViewMatrix( &AppInterface->app->GetHeadModelParms(), &CenterEyeTransform, eye );
	const Matrix4f projectionMatrix = ovrMatrix4f_CreateProjectionFov( fovDegreesX, fovDegreesY, 0.0f, 0.0f, VRAPI_ZNEAR, 0.0f );

	EyeBuffers->BeginRenderingEye( eye );
	glClearColor( 0, 0, 0, 1 );
	glClear( GL_COLOR_BUFFER_BIT );

	/// FIXME: this probably should output its surfaces in a frame result like everything else so we're not calling 
	/// RenderSurfaceList again in this special case.
	OVR::Array< ovrDrawSurface > surfaces;
	FontSurface->AppendSurfaceList( *Font, surfaces );
	surfaceRender.RenderSurfaceList( surfaces, viewMatrix, projectionMatrix );

	EyeBuffers->EndRenderingEye( eye );

	frameParms.LayerCount = 1;

	ovrFrameLayer & layer = frameParms.Layers[VRAPI_FRAME_LAYER_TYPE_WORLD];
	layer.SrcBlend = VRAPI_FRAME_LAYER_BLEND_ONE;
	layer.DstBlend = VRAPI_FRAME_LAYER_BLEND_ZERO;
	layer.Flags = 0;
	const auto consoleSwapChain = EyeBuffers->GetCurrentFrameTextureSwapChains();
	layer.Textures[eye].ColorTextureSwapChain = consoleSwapChain.ColorTextureSwapChain[eye];
	layer.Textures[eye].TextureSwapChainIndex = consoleSwapChain.TextureSwapChainIndex;
}

void DebugConsoleSingleton::Log( const char * fmt, va_list args )
{
	Log( TextDefaultColor, fmt, args );
}

void DebugConsoleSingleton::Log( const Vector3f& color, const char * fmt, va_list args )
{
	if ( !FontSurface )
	{
		return;
	}

	char buffer[4096];
	StringUtils::VSPrintf( buffer, fmt, args );

	int startLine = 0;
	for ( int pos = 0; buffer[pos] != '\0'; ++pos )
	{
		if ( buffer[pos] == '\n' || (pos - startLine) >= NumCharsPerLine )
		{
			if ( pos == startLine ) // for a line with no text go ahead and add a space, need something in the string for BitmapFont to move the line
			{
				LogEntries.PushFront( LogEntry( color, " " ) );
			}
			else
			{
				char temp = buffer[pos]; //avoid a copy
				buffer[pos] = '\0';
				LogEntries.PushFront( LogEntry( color, buffer + startLine ) );
				buffer[pos] = temp;
			}
			startLine = pos;
			if ( buffer[pos] == '\n' ) //skip newline, don't want it in our text
			{
				++pos;
				++startLine;
			}
		}
	}
	LogEntries.PushFront( LogEntry( color, buffer + startLine ) );

	while ( LogEntries.GetSize() > MaxLogEntries )
	{
		LogEntries.PopBack();
	}
}

void DebugConsoleSingleton::SetConsoleTextColor( const char * colorText )
{
	//Very likely to have text errors, so use lexer
	ovrLexer lexer { colorText };
	Vector3f newColor;
	ovrLexer::ovrResult parseResult = lexer.ParseFloat( newColor.x, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetDefaultTextColor failed, ill formed arguments %s", colorText );
		return;
	}
	parseResult = lexer.ParseFloat( newColor.y, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetDefaultTextColor failed, ill formed arguments %s", colorText );
		return;
	}
	parseResult = lexer.ParseFloat( newColor.z, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetDefaultTextColor failed, ill formed arguments %s", colorText );
		return;
	}
	newColor = Vector3f::Max( newColor, { 0, 0, 0 } );
	newColor = Vector3f::Min( newColor, { 1, 1, 1 } );
	TextDefaultColor = newColor;
}

void DebugConsoleSingleton::SetConsoleTextPosition( const char * positionText )
{
	//Very likely to have text errors, so use lexer
	ovrLexer lexer { positionText };
	Vector3f newPosition;
	ovrLexer::ovrResult parseResult = lexer.ParseFloat( newPosition.x, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetConsoleTextPosition failed, ill formed arguments %s", positionText );
		return;
	}
	parseResult = lexer.ParseFloat( newPosition.y, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetConsoleTextPosition failed, ill formed arguments %s", positionText );
		return;
	}
	parseResult = lexer.ParseFloat( newPosition.z, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetConsoleTextPosition failed, ill formed arguments %s", positionText );
		return;
	}
	TextPosition = newPosition;
}

void DebugConsoleSingleton::SetConsoleTextSize( const char * sizeText )
{
	//Very likely to have text errors, so use lexer
	ovrLexer lexer { sizeText };
	float newSize;
	ovrLexer::ovrResult parseResult = lexer.ParseFloat( newSize, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetConsoleTextSize failed, ill formed arguments %s", sizeText );
		return;
	}
	TextSize = Alg::Clamp( newSize, 0.01f, 10.0f );
	//This will change the layout, so for ease just wipe out the log, changing the text size isn't going to happen often (only when setting up the console)
	LogEntries.Clear();
}

void DebugConsoleSingleton::SetConsoleWidth( const char * widthText )
{
	ovrLexer lexer { widthText };
	float newWidth;
	ovrLexer::ovrResult parseResult = lexer.ParseFloat( newWidth, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN( "SetConsoleTextSize failed, ill formed arguments %s", widthText );
		return;
	}
	ConsoleWidth = Alg::Clamp( newWidth, 0.0f, 10.0f );
	//This will change the layout, so for ease just wipe out the log, changing the console width isn't going to happen often (only when setting up the console)
	LogEntries.Clear();
}

void DebugConsoleSingleton::GetConsoleTextSettings( const char * )
{
	DebugConsole::Log( "Text Color: %0.2f %0.2f %0.2f", TextDefaultColor.x, TextDefaultColor.y, TextDefaultColor.z );
	DebugConsole::Log( "Text Size: %0.3f", TextSize );
	DebugConsole::Log( "Text Position: %0.2f %0.2f %0.2f", TextPosition.x, TextPosition.y, TextPosition.z );
	DebugConsole::Log( "Console Width: %0.2f", ConsoleWidth );
}

bool DebugConsoleSingleton::OnKeyEvent( const int keyCode, const int repeatCount, const OVR::KeyEventType eventType )
{
	if ( !FontSurface )
	{
		return false;
	}

	//TODO: This is not a 100% reliable way to handle shift state. Could use a getasynckeystate type api, as it is now we could
	//miss a shift upkey event and get a stuck shift key. or maybe when recieving key events it passes along a flag with shift states
	//at the time of the event.
	if ( keyCode == OVR_KEY_LSHIFT )
	{
		ShiftDown[0] = eventType == KEY_EVENT_DOWN;
		return false;
	}
	if ( keyCode == OVR_KEY_RSHIFT )
	{
		ShiftDown[1] = eventType == KEY_EVENT_DOWN;
		return false;
	}
	if ( eventType == KEY_EVENT_DOWN && keyCode == OVR_KEY_GRAVE )
	{
		DisplayConsole = !DisplayConsole;
		return true;
	}

	if ( !DisplayConsole )
	{
		return false;
	}

	if ( eventType == KEY_EVENT_DOWN )
	{
		size_t commandLength = OVR_strlen( Command );
		if ( keyCode == OVR_KEY_BACKSPACE && commandLength > 1 )
		{
			Command[commandLength-1] = '\0';
			return true;
		}
		else if ( keyCode == OVR_KEY_RETURN && commandLength > 1 )
		{
			// +1's to skip the prompt char
			DebugConsole::Log( Command + 1 );
			SendConsoleCmd( AppInterface->app, Command + 1 );
			RecentCommands.PushBack( String( Command + 1 ) );
			if ( RecentCommands.GetSize() > MaxRecentCommands )
			{
				RecentCommands.PopFront();
			}
			RecentCommandIndex = static_cast<int>( RecentCommands.GetSize() ) - 1;
			StringUtils::Copy( Command, Prompt );
			return true;
		}
		else if ( keyCode == OVR_KEY_DPAD_UP && !RecentCommands.IsEmpty() )
		{
			StringUtils::Copy( Command, Prompt );
			OVR_strcat( Command, sizeof( Command ), RecentCommands.PeekFront( RecentCommandIndex ).ToCStr() );
			RecentCommandIndex = Alg::Max( RecentCommandIndex - 1, 0 );
			return true;
		}
		else if ( keyCode == OVR_KEY_DPAD_DOWN && !RecentCommands.IsEmpty() )
		{
			RecentCommandIndex = Alg::Min<int>( RecentCommandIndex + 1, static_cast<int>( RecentCommands.GetSize() ) - 1 );
			StringUtils::Copy( Command, Prompt );
			OVR_strcat( Command, sizeof( Command ), RecentCommands.PeekFront( RecentCommandIndex ).ToCStr() );
			return true;
		}
		else if ( keyCode == OVR_KEY_LEFT_TRIGGER )
		{
			ScrollPosition = Alg::Max( ScrollPosition - 1, 0 );
			return true;
		}
		else if ( keyCode == OVR_KEY_RIGHT_TRIGGER )
		{
			ScrollPosition = Alg::Min<int>( ScrollPosition + 1, static_cast<int>( LogEntries.GetSize() ) - MinLinesToDisplay );
			return true;
		}
		char ascii = GetAsciiForKeyCode( ( ovrKeyCode )keyCode, ShiftDown[0] || ShiftDown[1] );
		if ( ascii )
		{
			char appendString[2] = { ascii, '\0' };
			OVR_strcat( Command, sizeof( Command ), appendString );
			return true;
		}
	}

	return false;
}

void DebugConsoleSingleton::PrintConsoleTestText( const char * )
{
	static const char* testText =
		R"(    Android's standard OpenGL ES implementation triple buffers window surfaces.
    Triple buffering increases latency for increased smoothness, which is a debatable tradeoff for most applications, but is clearly bad for VR.An Android application that heavily loads the GPU and never forces a synchronization may have over 50 milliseconds of latency from the time eglSwapBuffers() is called to the time pixels start changing on the screen, even running at 60 FPS.
    Android should probably offer a strictly double buffered mode, and possibly a swap - tear option, but it is a sad truth that if you present a buffer to the system, there is a good chance it won't do what you want with it immediately. The best case is to have no chain of buffers at all -- a single buffer that you can render to while it is being scanned to the screen. To avoid tear lines, it is up to the application to draw only in areas of the window that aren't currently being scanned out.
    The mobile displays are internally scanned in portrait mode from top to bottom when the home button is on the bottom, or left to right when the device is in the headset.VrApi receives timestamped events at display vsync, and uses them to determine where the video raster is scanning at a given time.The current code waits until the right eye is being displayed to warp the left eye, then waits until the left eye is being displayed to warp the right eye.This gives a latency of only 8 milliseconds before the first pixel is changed on the screen.It takes 8 milliseconds to display scan half of the screen, so the latency will vary by that much across each eye.
)";

	DebugConsole::Log( Vector3f { 0.5f, 1.0f, 0.5f }, testText );
	DebugConsole::Log( Vector3f { 1.0f, 0.0f, 1.0f }, testText );
	DebugConsole::Log( Vector3f { 0.0f, 0.0f, 1.0f }, testText );
	DebugConsole::Log( Vector3f { 1.0f, 0.0f, 0.0f }, testText );
	DebugConsole::Log( Vector3f { 0.0f, 1.0f, 1.0f }, testText );
	DebugConsole::Log( Vector3f { 1.0f, 1.0f, 0.0f }, testText );
	DebugConsole::Log( Vector3f { 1.0f, 1.0f, 1.0f }, testText );
	DebugConsole::Log( testText ); //defaultcolor
}

/************************************************************************************
External Interface
************************************************************************************/

namespace DebugConsole
{
#ifdef OVR_OS_ANDROID
	void Init( VrAppInterface * appInterface )
	{
		DebugConsoleSingleton::Instance().Init( appInterface );
	}

	void Shutdown()
	{
		DebugConsoleSingleton::Instance().Shutdown();
	}

	bool OnKeyEvent( const int keyCode, const int repeatCount, const OVR::KeyEventType eventType )
	{
		return DebugConsoleSingleton::Instance().OnKeyEvent( keyCode, repeatCount, eventType );
	}

	void Frame( const ovrFrameInput & vrFrame )
	{
		DebugConsoleSingleton::Instance().Frame( vrFrame );
	}

	void BeginFrame()
	{
		DebugConsoleSingleton::Instance().BeginFrame();
	}

	void EndFrame()
	{
		DebugConsoleSingleton::Instance().EndFrame();
	}

	void DrawEyeView( const int eye, const float fovDegreesX, const float fovDegreesY,
					  ovrFrameParms & frameParms, ovrSurfaceRender & surfaceRender )
	{
		DebugConsoleSingleton::Instance().DrawEyeView( eye, fovDegreesX, fovDegreesY, frameParms, surfaceRender );
	}

	void Log( const char * fmt, ... )
	{
		va_list args;
		va_start( args, fmt );
		DebugConsoleSingleton::Instance().Log( fmt, args );
		va_end( args );
	}

	void Log( const Vector3f color, const char * fmt, ... )
	{
		va_list args;
		va_start( args, fmt );
		DebugConsoleSingleton::Instance().Log( color, fmt, args );
		va_end( args );
	}
#else //crash on PC, and no way to enable anyway (requires a pref), so disable
	void Init( VrAppInterface * )
	{
	}

	void Shutdown()
	{
	}

	bool OnKeyEvent( const int, const int,  const OVR::KeyEventType )
	{
		return false;
	}

	void Frame( const ovrFrameInput & )
	{
	}

	void BeginFrame()
	{
	}

	void EndFrame()
	{
	}

	void DrawEyeView( const int, const float, const float, ovrFrameParms &, ovrSurfaceRender & )
	{
	}

	void Log( const char *, ... )
	{
	}

	void Log( const Vector3f, const char *, ... )
	{
	}
#endif
}

}
