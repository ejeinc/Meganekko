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

#if defined( USE_LAYERIMAGE )
#include "LayerImage.h"
#endif

#ifdef OVR_OS_ANDROID
#define LOCAL_PREF_VRAPI_DEBUG_CONSOLE "dev_debugConsole" // "0" = off, "1" = on
#else
#define LOCAL_PREF_VRAPI_DEBUG_CONSOLE L"dev_debugConsole" // on if string exist on command line
#endif

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
	void	Frame( const ovrFrameInput & vrFrame, ovrFrameResult & frameResult );
	void	Log( const char * fmt, va_list args );
	void	Log( const Vector3f& color, const char * fmt, va_list args );
	bool	OnKeyEvent( const int keyCode, const int repeatCount, const OVR::KeyEventType eventType );

private:
	struct LogEntry
	{
		LogEntry() {}
		LogEntry( Vector3f color, const char * text ) : Color( color, 1.0f ), Text( text ) {}

		Vector4f Color;
		String Text;
	};
	DebugConsoleSingleton() = default;
	~DebugConsoleSingleton() = default;

	void	SetTextColor( const char * colorText );
	void	SetUseTWLayer ( const char * text );
	void	Echo ( const char * text );
	void	PrintTestText();

	static const int MaxLogEntries = 1024;
	static const int MaxLinesToDisplay = 88;
	static const int MaxRecentCommands = 16;
	static const int MaxCharsPerLine = 128;		// text has to be unreadably small, or the console fairly wide to go over this.
	static const int MinLinesToDisplay = 8;		// For scrolling, keep at least this many lines on screen if there are this many.
	static const char * Prompt;

	CircularBuffer<LogEntry>	LogEntries { MaxLogEntries };
	CircularBuffer<String>		RecentCommands;
	int							RecentCommandIndex { 0 };
	char						Command[MaxCharsPerLine];
	BitmapFont *				Font { nullptr };
	BitmapFontSurface *			FontSurface { nullptr };
	bool						DisplayConsole { false };
	bool						ShiftDown[2]; //left, right
	VrAppInterface *			AppInterface{nullptr};
	int							NumCharsPerLine { MaxCharsPerLine };
	int							ScrollPosition { 0 };

	Vector3f					TextDefaultColor { 0.0f, 1.0f, 0.0f }; // green has highest legibility on pentile
#if defined( OVR_OS_ANDROID )
	bool						UseTWLayer { true };
#else
	bool						UseTWLayer { false }; // doesn't work on pc currently
#endif
	float						TextSize { 130.0f };
	Vector3f					TextPosition { -192.0f, 310.0f, -1.0f }; // xy opposite of what you expect, portrait cylinder layer
	float						ConsoleWidth { 620.0f };

	int							LayerWidth { 2048 };
	int							LayerHeight { 640 };

	ovrSurfaceRender			SurfaceRender;
#if defined( USE_LAYERIMAGE )
	LayerImage *				UILayer { nullptr };
#endif
};

const char * DebugConsoleSingleton::Prompt = ">";

DebugConsoleSingleton & DebugConsoleSingleton::Instance()
{
	static DebugConsoleSingleton console;
	return console;
}

void DebugConsoleSingleton::Init( VrAppInterface * appInterface )
{
	bool useDebugConsole = false;
#ifdef OVR_OS_ANDROID
	useDebugConsole = OVR_strcmp( ovr_GetLocalPreferenceValueForKey( LOCAL_PREF_VRAPI_DEBUG_CONSOLE, "0" ), "1" ) == 0;
#else
	LPWSTR * commandLineArgs;
	int numArgs;

	commandLineArgs = CommandLineToArgvW( GetCommandLineW(), &numArgs );
	for ( int i = 0; i < numArgs; ++i )
	{
		useDebugConsole |= wcscmp( commandLineArgs[i], LOCAL_PREF_VRAPI_DEBUG_CONSOLE ) == 0;
	}
	LocalFree( commandLineArgs );

#endif
	if ( useDebugConsole )
	{
#if defined( USE_LAYERIMAGE )
		// just a debug console, so skip the slightly improved quality we would get from an 888srgb with cac and mips.
		UILayer = new LayerImage( LayerWidth, LayerHeight, VRAPI_TEXTURE_FORMAT_4444 );
		UILayer->SetChromaticCorrection( false ); // causes issues with text, no border pixels of color in the alpha parts
		UILayer->SetFilter ( LayerFilter::LF_LINEAR );
#endif
		
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
		//disable culling. there are some hacks on our projection matrix to flip our rendering around to align with the current portrait cylinder mode.
		//It works but leaves bindings backwards, so just disabled culling.
		FontSurface->Init( MaxLinesToDisplay * MaxCharsPerLine * vertsPerChar );
		FontSurface->SetCullEnabled( false );

		RegisterConsoleFunction( "DCTextColor",		[] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().SetTextColor( cmd );	} );
		RegisterConsoleFunction ( "Echo", [] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().Echo ( cmd );	} );
		RegisterConsoleFunction( "DCPrintTestText",	[] ( void *, const char * ) { DebugConsoleSingleton::Instance().PrintTestText(); } );
		RegisterConsoleFunction ( "DCUseTwLayer", [] ( void *, const char * cmd ) { DebugConsoleSingleton::Instance().SetUseTWLayer ( cmd );	} );
		
		RegisterConsoleFunction( "List", [] ( void *, const char * )
		{
			DebugConsole::Log( "Registered Console Commands:" );
			GetConsoleCmds( [] ( const char * cmd )
			{
				DebugConsole::Log( cmd );
			} );
		} );

		float charAdvancex = Font->CalcTextWidth ( "a" ); //assume monospace, so all chars are the same
		NumCharsPerLine = ( int )Alg::Clamp<int>( ( int )( ConsoleWidth / ( charAdvancex * TextSize ) ), 1, MaxCharsPerLine );

		StringUtils::Copy( Command, Prompt );

		SurfaceRender.Init();
	}
}

void DebugConsoleSingleton::Shutdown()
{
	SurfaceRender.Shutdown();

	BitmapFontSurface::Free( FontSurface );
	BitmapFont::Free( Font );
	FontSurface = nullptr;
	Font = nullptr;

#if defined( USE_LAYERIMAGE )
	delete UILayer;
	UILayer = nullptr;
#endif
}

void DebugConsoleSingleton::Frame( const ovrFrameInput & vrFrame, ovrFrameResult & frameResult )
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
															Vector3f( 1.0f, 0.0f, 0.0f ), TextSize, Vector4f( TextDefaultColor, 1.0f ), &Command[currentPos] );
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

	for ( int i = ScrollPosition; i < Alg::Min<int>( ScrollPosition + ( MaxLinesToDisplay - linesPrinted ), static_cast<int>( LogEntries.GetSize() ) ); ++i )
	{
		logTextPosition -= FontSurface->DrawText3D( *Font, fontParams, logTextPosition, Vector3f( 0.0f, 0.0f, 1.0f ),
														Vector3f( 1.0f, 0.0f, 0.0f ), TextSize, LogEntries.PeekFront( i ).Color, LogEntries.PeekFront( i ).Text.ToCStr() );
	}

	Matrix4f textProj = Matrix4f::Identity();
	//create an orthographic projection, hacked to deal with the mirrored texture with portrait layerimage
	float znear = VRAPI_ZNEAR;
	float zfar = 100;
	textProj.M[0][0] =  2.0f / LayerWidth;
	textProj.M[1][1] = -2.0f / LayerHeight;
	textProj.M[2][2] = -2.0f / ( zfar - znear );
	textProj.M[2][3] = -( zfar + znear ) / ( zfar - znear );

	FontSurface->Finish( Matrix4f::Identity() );

#if defined( USE_LAYERIMAGE )
	auto nextFbo = UILayer->NextFramebufferObject();
	glBindFramebuffer ( GL_DRAW_FRAMEBUFFER, nextFbo );
	glScissor ( 0, 0, LayerWidth, LayerHeight );
	glViewport ( 0, 0, LayerWidth, LayerHeight );
	glClearColor ( 0.0f, 0.0f, 0.0f, 0.0f );
	glClear ( GL_COLOR_BUFFER_BIT );

	OVR::Array< ovrDrawSurface > surfaces;
	FontSurface->AppendSurfaceList( *Font, surfaces );
	SurfaceRender.RenderSurfaceList( surfaces, Matrix4f::Identity(), textProj, 0 );

	glBindFramebuffer ( GL_DRAW_FRAMEBUFFER, 0 );
#endif

	auto centerView = vrapi_GetCenterEyeViewMatrix ( &AppInterface->app->GetHeadModelParms(), &vrFrame.Tracking, nullptr );
	frameResult.FrameMatrices.CenterView = centerView;
	frameResult.FrameMatrices.EyeView[0] = vrapi_GetEyeViewMatrix ( &AppInterface->app->GetHeadModelParms(), &centerView, 0 );
	frameResult.FrameMatrices.EyeView[1] = vrapi_GetEyeViewMatrix ( &AppInterface->app->GetHeadModelParms(), &centerView, 1 );

	//clear out anything else being rendered when debug console is up, we let that code go through the motions to catch any logs.
	frameResult.ClearColor = { 0.0f, 0.0f, 0.0f, 1.0f };
	frameResult.ClearColorBuffer = true;
	frameResult.ClearDepthBuffer = true;
	frameResult.Surfaces.Clear();

#if defined( USE_LAYERIMAGE )
	UILayer->SetModelMatrix( UILayer->PortraitCylinder( Vector3f( 0.0f, 0.0f, -1.0f ), Vector3f( 0.0f, 0.0f, 0.0f ) ), true );
	if ( UseTWLayer )
	{
		UILayer->PresentLayer( frameResult.FrameParms, frameResult.FrameMatrices.EyeView[0], frameResult.FrameMatrices.EyeView[1] );
	}
	else
	{
		frameResult.Surfaces.PushBack( UILayer->LayerAsSurface() );
	}
#endif
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
		if ( buffer[pos] == '\n' || ( pos - startLine ) >= NumCharsPerLine )
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

void DebugConsoleSingleton::SetTextColor( const char * colorText )
{
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

void DebugConsoleSingleton::SetUseTWLayer( const char * text )
{
	ovrLexer lexer { text };
	int useTWLayer;
	ovrLexer::ovrResult parseResult = lexer.ParseInt ( useTWLayer, 0 );
	if ( parseResult != ovrLexer::LEX_RESULT_OK )
	{
		WARN ( "SetUseTWLayer failed, ill formed arguments %s", text );
		return;
	}
	UseTWLayer = useTWLayer != 0;
}

void DebugConsoleSingleton::Echo ( const char * text )
{
	DebugConsole::Log ( text );
}

bool DebugConsoleSingleton::OnKeyEvent( const int keyCode, const int repeatCount, const OVR::KeyEventType eventType )
{
	if ( !FontSurface )
	{
		return false;
	}

	// TODO: This is not a 100% reliable way to handle shift state. Could use a getasynckeystate type api, as it is now we could
	// miss a shift upkey event and get a stuck shift key. or maybe when recieving key events it passes along a flag with shift states
	// at the time of the event.
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
	if ( eventType == KEY_EVENT_DOWN && ( keyCode == OVR_KEY_GRAVE || keyCode == OVR_KEY_TILDE ) )
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
		else if ( ( keyCode == OVR_KEY_DPAD_UP || keyCode == OVR_KEY_UP ) && !RecentCommands.IsEmpty() )
		{
			StringUtils::Copy( Command, Prompt );
			OVR_strcat( Command, sizeof( Command ), RecentCommands.PeekFront( RecentCommandIndex ).ToCStr() );
			RecentCommandIndex = Alg::Max( RecentCommandIndex - 1, 0 );
			return true;
		}
		else if ( ( keyCode == OVR_KEY_DPAD_DOWN || keyCode == OVR_KEY_DOWN ) && !RecentCommands.IsEmpty() )
		{
			RecentCommandIndex = Alg::Min<int>( RecentCommandIndex + 1, static_cast<int>( RecentCommands.GetSize() ) - 1 );
			StringUtils::Copy( Command, Prompt );
			OVR_strcat( Command, sizeof( Command ), RecentCommands.PeekFront( RecentCommandIndex ).ToCStr() );
			return true;
		}
		else if ( keyCode == OVR_KEY_LEFT_TRIGGER || keyCode == OVR_KEY_BUTTON_LEFT_SHOULDER )
		{
			ScrollPosition = Alg::Max( ScrollPosition - 1, 0 );
			return true;
		}
		else if ( keyCode == OVR_KEY_RIGHT_TRIGGER || keyCode == OVR_KEY_BUTTON_RIGHT_SHOULDER )
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

void DebugConsoleSingleton::PrintTestText( )
{
	static const char* testText =
		R"(    Android's standard OpenGL ES implementation triple buffers window surfaces.
    Triple buffering increases latency for increased smoothness, which is a debatable tradeoff for most applications, but is clearly bad for VR.An Android application that heavily loads the GPU and never forces a synchronization may have over 50 milliseconds of latency from the time eglSwapBuffers() is called to the time pixels start changing on the screen, even running at 60 FPS.
    Android should probably offer a strictly double buffered mode, and possibly a swap - tear option, but it is a sad truth that if you present a buffer to the system, there is a good chance it won't do what you want with it immediately. The best case is to have no chain of buffers at all -- a single buffer that you can render to while it is being scanned to the screen. To avoid tear lines, it is up to the application to draw only in areas of the window that aren't currently being scanned out.
    The mobile displays are internally scanned in portrait mode from top to bottom when the home button is on the bottom, or left to right when the device is in the headset.VrApi receives timestamped events at display vsync, and uses them to determine where the video raster is scanning at a given time.The current code waits until the right eye is being displayed to warp the left eye, then waits until the left eye is being displayed to warp the right eye.This gives a latency of only 8 milliseconds before the first pixel is changed on the screen.It takes 8 milliseconds to display scan half of the screen, so the latency will vary by that much across each eye.
)";
	
	DebugConsole::Log( Vector3f { 1.0f, 0.0f, 1.0f }, testText );
	DebugConsole::Log( Vector3f { 0.0f, 1.0f, 1.0f }, testText );
	DebugConsole::Log( Vector3f { 1.0f, 1.0f, 0.0f }, testText );
	DebugConsole::Log( Vector3f { 0.0f, 0.0f, 1.0f }, testText );
	DebugConsole::Log( Vector3f { 1.0f, 0.0f, 0.0f }, testText );
	DebugConsole::Log( Vector3f { 1.0f, 1.0f, 1.0f }, testText );
	DebugConsole::Log( testText ); //defaultcolor
}

/************************************************************************************
External Interface
************************************************************************************/

namespace DebugConsole
{
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

	void Frame( const ovrFrameInput & vrFrame, ovrFrameResult& frameResult )
	{
		DebugConsoleSingleton::Instance().Frame( vrFrame, frameResult );
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
}

}
