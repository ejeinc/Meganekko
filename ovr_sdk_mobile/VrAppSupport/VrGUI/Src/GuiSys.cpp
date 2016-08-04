/************************************************************************************

Filename    :   OvrGuiSys.cpp
Content     :   Manager for native GUIs.
Created     :   June 6, 2014
Authors     :   Jonathan E. Wright

Copyright   :   Copyright 2014 Oculus VR, LLC. All Rights reserved.


*************************************************************************************/

#include "GuiSys.h"

#include "Kernel/OVR_GlUtils.h"
#include "GlProgram.h"
#include "GlTexture.h"
#include "GlGeometry.h"
#include "VrCommon.h"
#include "App.h"
#include "VRMenuMgr.h"
#include "VRMenuComponent.h"
#include "SoundLimiter.h"
#include "VRMenuEventHandler.h"
#include "FolderBrowser.h"
#include "Input.h"
#include "DefaultComponent.h"
#include "VrApi.h"
#include "Android/JniUtils.h"
#include "Kernel/OVR_JSON.h"
#include "Kernel/OVR_Lexer.h"
#include "SystemActivities.h"

//#define OVR_USE_PERF_TIMER
#include "OVR_PerfTimer.h"

#if defined( OVR_OS_ANDROID )
#include "Input.h"
#endif

namespace OVR {

#define IMPL_CONSOLE_FUNC_BOOL( var_name ) \
	ovrLexer lex( parms );	\
	int v;					\
	lex.ParseInt( v, 1 );	\
	var_name = v != 0;		\
	LOG( #var_name "( '%s' ) = %i", parms, var_name )

//==============================================================
// OvrGuiSysLocal
class OvrGuiSysLocal : public OvrGuiSys
{
public:
							OvrGuiSysLocal();
	virtual					~OvrGuiSysLocal();

	virtual void			Init( App * app, OvrGuiSys::SoundEffectPlayer & soundEffectPlayer,
							        char const * fontName, OvrDebugLines * debugLines ) OVR_OVERRIDE;
	// Init with a custom font surface for larger-than-normal amounts of text.
	virtual void			Init( App * app_, OvrGuiSys::SoundEffectPlayer & soundEffectPlayer,
							        char const * fontName, BitmapFontSurface * fontSurface,
							        OvrDebugLines * debugLines ) OVR_OVERRIDE;

	virtual void			Shutdown() OVR_OVERRIDE;

	virtual void			Frame( ovrFrameInput const & vrFrame,
									Matrix4f const & viewMatrix ) OVR_OVERRIDE;

	virtual void 			AppendSurfaceList( Matrix4f const & centerViewMatrix, Array< ovrDrawSurface > * surfaceList ) const OVR_OVERRIDE;

	//----DEPRECATED - DrawEyeView
	virtual void			RenderEyeView( Matrix4f const & centerViewMatrix, Matrix4f const & viewMatrix, 
									Matrix4f const & projectionMatrix, ovrSurfaceRender & surfaceRender ) const OVR_OVERRIDE;
	//----DEPRECATED - DrawEyeView	

	virtual bool			OnKeyEvent( int const keyCode, 
									const int repeatCount, 
									KeyEventType const eventType ) OVR_OVERRIDE;

	virtual void			ResetMenuOrientations( Matrix4f const & viewMatrix ) OVR_OVERRIDE;

	virtual void			AddMenu( VRMenu * menu ) OVR_OVERRIDE;
	virtual VRMenu *		GetMenu( char const * menuName ) const OVR_OVERRIDE;
	virtual Array< String > GetAllMenuNames() const OVR_OVERRIDE;
	virtual void			DestroyMenu( VRMenu * menu ) OVR_OVERRIDE;
	
	virtual void			OpenMenu( char const * name ) OVR_OVERRIDE;
	
	virtual void			CloseMenu( char const * menuName, bool const closeInstantly ) OVR_OVERRIDE;
	virtual void			CloseMenu( VRMenu * menu, bool const closeInstantly ) OVR_OVERRIDE;

	virtual bool			IsMenuActive( char const * menuName ) const OVR_OVERRIDE;
	virtual bool			IsAnyMenuActive() const OVR_OVERRIDE;
	virtual bool			IsAnyMenuOpen() const OVR_OVERRIDE;
	
	virtual App *					GetApp() const OVR_OVERRIDE { return app; }
	virtual OvrVRMenuMgr &			GetVRMenuMgr() OVR_OVERRIDE { return *MenuMgr; }
	virtual OvrVRMenuMgr const &	GetVRMenuMgr() const OVR_OVERRIDE { return *MenuMgr; };
	virtual OvrGazeCursor &			GetGazeCursor() OVR_OVERRIDE { return *GazeCursor; }
	virtual BitmapFont &			GetDefaultFont() OVR_OVERRIDE { return *DefaultFont; }
	virtual BitmapFontSurface &		GetDefaultFontSurface() OVR_OVERRIDE { return *DefaultFontSurface; }
	virtual OvrDebugLines &			GetDebugLines() OVR_OVERRIDE { return *DebugLines; }
    virtual SoundEffectPlayer &		GetSoundEffectPlayer() OVR_OVERRIDE { return *SoundEffectPlayer; }
	virtual ovrTextureManager &		GetTextureManager() OVR_OVERRIDE { return *app->GetTextureManager(); } // app's texture manager should always initialize before guisys

private:
	App *					app;
	OvrVRMenuMgr *			MenuMgr;
	OvrGazeCursor *			GazeCursor;
	BitmapFont *			DefaultFont;
	BitmapFontSurface *		DefaultFontSurface;
	OvrDebugLines *			DebugLines;
    OvrGuiSys::SoundEffectPlayer *     SoundEffectPlayer;

	Array< VRMenu* >		Menus;
	Array< VRMenu* >		ActiveMenus;

	// These button down times prevent an up on the menu button from canceling the gaze cursor
	// timer if the back button is still held, and vice versa. This is a hack to deal with Android
	// 5.1.1 on Note 5 sending a menu button down/up when the back button is held for 0.5 seconds.
	double					BackButtonDownTime;
	double					MenuButtonDownTime;

	bool					IsInitialized;
	static bool				SkipFrame;
	static bool				SkipRender;
	static bool				SkipSubmit;
	static bool				SkipFont;
	static bool				SkipCursor;

private:
	int						FindMenuIndex( char const * menuName ) const;
	int						FindMenuIndex( VRMenu const * menu ) const;
	int						FindActiveMenuIndex( VRMenu const * menu ) const;
	int						FindActiveMenuIndex( char const * menuName ) const;
	virtual void			MakeActive( VRMenu * menu ) OVR_OVERRIDE;
	void					MakeInactive( VRMenu * menu );
	void					StartTimer( int const keyCode, float const durationSeconds, 
									float const timeBeforeShowingTimer );
	void					CancelTimer( int const keyCode, bool const force );

	Array< VRMenuComponent* > GetDefaultComponents();

	static void				GUISkipFrame( void * appPtr, char const * parms ) { IMPL_CONSOLE_FUNC_BOOL( SkipFrame ); }
	static void				GUISkipRender( void * appPtr, char const * parms ) { IMPL_CONSOLE_FUNC_BOOL( SkipRender ); }
	static void				GUISkipSubmit( void * appPtr, char const * parms ) { IMPL_CONSOLE_FUNC_BOOL( SkipSubmit ); }
	static void				GUISkipFont( void * appPtr, char const * parms ) { IMPL_CONSOLE_FUNC_BOOL( SkipFont ); }
	static void				GUISkipCursor( void * appPtr, char const * parms ) { IMPL_CONSOLE_FUNC_BOOL( SkipCursor ); }

};


Vector4f const OvrGuiSys::BUTTON_DEFAULT_TEXT_COLOR( 0.098f, 0.6f, 0.96f, 1.0f );
Vector4f const OvrGuiSys::BUTTON_HILIGHT_TEXT_COLOR( 1.0f );

//==============================
// OvrGuiSys::Create
OvrGuiSys * OvrGuiSys::Create()
{
	return new OvrGuiSysLocal;
}

//==============================
// OvrGuiSys::Destroy
void OvrGuiSys::Destroy( OvrGuiSys * & guiSys )
{
	if ( guiSys != NULL )
	{
		guiSys->Shutdown();
		delete guiSys;
		guiSys = NULL;
	}
}

bool OvrGuiSysLocal::SkipFrame = false;
bool OvrGuiSysLocal::SkipRender = false;
bool OvrGuiSysLocal::SkipSubmit = false;
bool OvrGuiSysLocal::SkipFont = false;
bool OvrGuiSysLocal::SkipCursor = false;

//==============================
// OvrGuiSysLocal::
OvrGuiSysLocal::OvrGuiSysLocal() 
	: app( NULL )
	, MenuMgr( NULL )
	, GazeCursor( NULL )
	, DefaultFont( NULL )
	, DefaultFontSurface( NULL )
	, DebugLines( NULL )
    , SoundEffectPlayer( NULL )
	, BackButtonDownTime( -1.0 )
	, MenuButtonDownTime( -1.0 )
	, IsInitialized( false )
{
}

//==============================
// OvrGuiSysLocal::
OvrGuiSysLocal::~OvrGuiSysLocal()
{
	OVR_ASSERT( IsInitialized == false ); // Shutdown should already have been called

}

//==============================
// OvrGuiSysLocal::Init
void OvrGuiSysLocal::Init( App * app_, OvrGuiSys::SoundEffectPlayer & soundEffectPlayer, char const * fontName, 
		BitmapFontSurface * fontSurface, OvrDebugLines * debugLines )
{
	LOG( "OvrGuiSysLocal::Init" );

	app = app_;
	SoundEffectPlayer = &soundEffectPlayer;
	DebugLines = debugLines;

	MenuMgr = OvrVRMenuMgr::Create( *this );
	MenuMgr->Init( *this );

	GazeCursor = OvrGazeCursor::Create( app->GetFileSys() );

	DefaultFont = BitmapFont::Create();

	OVR_ASSERT( fontSurface->IsInitialized() );	// if you pass a font surface in, you must initialized it before calling OvrGuiSysLocal::Init()
	DefaultFontSurface = fontSurface;

	// choose a package to load the font from.
	// select the System Activities package first
	LOG( "GuiSys::Init - fontName is '%s'", fontName );

	if ( OVR_strncmp( fontName, "apk:", 4 ) == 0 ) //if full apk path specified use that
	{
		if ( !DefaultFont->Load( app->GetFileSys(), fontName ) )
		{
			// we can't just do a fatal error here because the /lang/ host is supposed to be System Activities
			// one case of the font failing to load is because System Activities is missing entirely.
			// Instead, we
			app->ShowDependencyError();
		}
	}
	else
	{
		char fontUri[1024];
		OVR_sprintf( fontUri, sizeof( fontUri ), "apk://font/res/raw/%s", fontName );
		if ( !DefaultFont->Load( app->GetFileSys(), fontUri ) )
		{
			// we can't just do a fatal error here because the /lang/ host is supposed to be System Activities
			// one case of the font failing to load is because System Activities is missing entirely.
			// Instead, we
			app->ShowDependencyError();
		}
	}

	IsInitialized = true;

	app->RegisterConsoleFunction( "GUISkipFrame", OvrGuiSysLocal::GUISkipFrame );
	app->RegisterConsoleFunction( "GUISkipRender", OvrGuiSysLocal::GUISkipRender );
	app->RegisterConsoleFunction( "GUISkipSubmit", OvrGuiSysLocal::GUISkipSubmit );
	app->RegisterConsoleFunction( "GUISkipFont", OvrGuiSysLocal::GUISkipFont );
	app->RegisterConsoleFunction( "GUISkipCursor", OvrGuiSysLocal::GUISkipCursor );
}

//==============================
// OvrGuiSysLocal::Init
void OvrGuiSysLocal::Init( App * app_, OvrGuiSys::SoundEffectPlayer & soundEffectPlayer, char const * fontName, OvrDebugLines * debugLines )
{
	BitmapFontSurface * fontSurface = BitmapFontSurface::Create();
	fontSurface->Init( 8192 );
	Init( app_, soundEffectPlayer, fontName, fontSurface, debugLines );
}

//==============================
// OvrGuiSysLocal::Shutdown
void OvrGuiSysLocal::Shutdown()
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	IsInitialized = false;

	// pointers in this list will always be in Menus list, too, so just clear it
	ActiveMenus.Clear();

	// FIXME: we need to make sure we delete any child menus here -- it's not enough to just delete them
	// in the destructor of the parent, because they'll be left in the menu list since the destructor has
	// no way to call GuiSys->DestroyMenu() for them.
	for ( int i = 0; i < Menus.GetSizeI(); ++i )
	{
		VRMenu * menu = Menus[i];
		menu->Shutdown( *this );
		delete menu;
		Menus[i] = NULL;
	}
	Menus.Clear();

	BitmapFontSurface::Free( DefaultFontSurface );
	BitmapFont::Free( DefaultFont );
	OvrGazeCursor::Destroy( GazeCursor );
	OvrVRMenuMgr::Destroy( MenuMgr );
	DebugLines = NULL;
    SoundEffectPlayer = NULL;
	app = NULL;
}

//==============================
// OvrGuiSysLocal::RepositionMenus
// Reposition any open menus 
void OvrGuiSysLocal::ResetMenuOrientations( Matrix4f const & centerViewMatrix )
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	for ( int i = 0; i < Menus.GetSizeI(); ++i )
	{
		if ( VRMenu* menu = Menus.At( i ) )
		{
			LOG( "ResetMenuOrientation -> '%s'", menu->GetName() );
			menu->ResetMenuOrientation( centerViewMatrix );
		}
	}
}

//==============================
// OvrGuiSysLocal::AddMenu
void OvrGuiSysLocal::AddMenu( VRMenu * menu )
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	int menuIndex = FindMenuIndex( menu->GetName() );
	if ( menuIndex >= 0 )
	{
		WARN( "Duplicate menu name '%s'", menu->GetName() );
		OVR_ASSERT( menuIndex < 0 );
	}
	Menus.PushBack( menu );
}

//==============================
// OvrGuiSysLocal::GetMenu
VRMenu * OvrGuiSysLocal::GetMenu( char const * menuName ) const
{
	int menuIndex = FindMenuIndex( menuName );
	if ( menuIndex >= 0 )
	{
		return Menus[menuIndex];
	}
	return NULL;
}

//==============================
// OvrGuiSysLocal::GetAllMenuNames
Array< String > OvrGuiSysLocal::GetAllMenuNames() const
{
	Array< String > allMenuNames;
	for ( int i = 0; i < Menus.GetSizeI(); ++i )
	{
		allMenuNames.PushBack( String( Menus[ i ]->GetName() ) );
	}
	return allMenuNames;
}

//==============================
// OvrGuiSysLocal::DestroyMenu
void OvrGuiSysLocal::DestroyMenu( VRMenu * menu )
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	if ( menu == nullptr )
	{
		return;
	}

	MakeInactive( menu );

	menu->Shutdown( *this );
	delete menu;

	int idx = FindMenuIndex( menu );
	if ( idx >= 0 )
	{
		Menus.RemoveAt( idx );
	}
}

//==============================
// OvrGuiSysLocal::FindMenuIndex
int OvrGuiSysLocal::FindMenuIndex( char const * menuName ) const
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return -1;
	}

	for ( int i = 0; i < Menus.GetSizeI(); ++i )
	{
		if ( OVR_stricmp( Menus[i]->GetName(), menuName ) == 0 )
		{
			return i;
		}
	}
	return -1;
}

//==============================
// OvrGuiSysLocal::FindMenuIndex
int OvrGuiSysLocal::FindMenuIndex( VRMenu const * menu ) const
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return -1;
	}

	for ( int i = 0; i < Menus.GetSizeI(); ++i )
	{
		if ( Menus[i] == menu ) 
		{
			return i;
		}
	}
	return -1;
}

//==============================
// OvrGuiSysLocal::FindActiveMenuIndex
int OvrGuiSysLocal::FindActiveMenuIndex( VRMenu const * menu ) const
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return -1;
	}

	for ( int i = 0; i < ActiveMenus.GetSizeI(); ++i )
	{
		if ( ActiveMenus[i] == menu ) 
		{
			return i;
		}
	}
	return -1;
}

//==============================
// OvrGuiSysLocal::FindActiveMenuIndex
int OvrGuiSysLocal::FindActiveMenuIndex( char const * menuName ) const
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return -1;
	}

	for ( int i = 0; i < ActiveMenus.GetSizeI(); ++i )
	{
		if ( OVR_stricmp( ActiveMenus[i]->GetName(), menuName ) == 0 )
		{
			return i;
		}
	}
	return -1;
}

//==============================
// OvrGuiSysLocal::MakeActive
void OvrGuiSysLocal::MakeActive( VRMenu * menu )
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	int idx = FindActiveMenuIndex( menu );
	if ( idx < 0 )
	{
		ActiveMenus.PushBack( menu );
	}
}

//==============================
// OvrGuiSysLocal::MakeInactive
void OvrGuiSysLocal::MakeInactive( VRMenu * menu )
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	int idx = FindActiveMenuIndex( menu );
	if ( idx >= 0 )
	{
		ActiveMenus.RemoveAtUnordered( idx );
	}
}

//==============================
// OvrGuiSysLocal::OpenMenu
void OvrGuiSysLocal::OpenMenu( char const * menuName )
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	int menuIndex = FindMenuIndex( menuName );
	if ( menuIndex < 0 )
	{
		WARN( "No menu named '%s'", menuName );
		OVR_ASSERT( menuIndex >= 0 && menuIndex < Menus.GetSizeI() );
		return;
	}
	VRMenu * menu = Menus[menuIndex];
	OVR_ASSERT( menu != NULL );

	menu->Open( *this );
}

//==============================
// OvrGuiSysLocal::CloseMenu
void OvrGuiSysLocal::CloseMenu( VRMenu * menu, bool const closeInstantly )
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	OVR_ASSERT( menu != NULL );

	menu->Close( *this, closeInstantly );
}

//==============================
// OvrGuiSysLocal::CloseMenu
void OvrGuiSysLocal::CloseMenu( char const * menuName, bool const closeInstantly ) 
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	int menuIndex = FindMenuIndex( menuName );
	if ( menuIndex < 0 )
	{
		WARN( "No menu named '%s'", menuName );
		OVR_ASSERT( menuIndex >= 0 && menuIndex < Menus.GetSizeI() );
		return;
	}
	VRMenu * menu = Menus[menuIndex];
	CloseMenu( menu, closeInstantly );
}


//==============================
// OvrGuiSysLocal::IsMenuActive
bool OvrGuiSysLocal::IsMenuActive( char const * menuName ) const
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return false;
	}

	int idx = FindActiveMenuIndex( menuName );
	return idx >= 0;
}

//==============================
// OvrGuiSysLocal::IsAnyMenuOpen
bool OvrGuiSysLocal::IsAnyMenuActive() const 
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return false;
	}

	return ActiveMenus.GetSizeI() > 0;
}

//==============================
// OvrGuiSysLocal::IsAnyMenuOpen
bool OvrGuiSysLocal::IsAnyMenuOpen() const
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return false;
	}

	for ( int i = 0; i < ActiveMenus.GetSizeI(); ++i )
	{
		if ( ActiveMenus[i]->IsOpenOrOpening() )
		{
			return true;
		}
	}
	return false;
}

//==============================
// OvrGuiSysLocal::Frame
void OvrGuiSysLocal::Frame( const ovrFrameInput & vrFrame, Matrix4f const & centerViewMatrix )
{
	OVR_PERF_TIMER( OvrGuiSys_Frame );

	if ( !IsInitialized || SkipFrame )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	// this is a hack used to determine if the app was paused. Without this, every app currently
	// would have to explicitly call GuiSys on enter / leave vr mode, which is just tedious and
	// easy to forget. All this does is ignore input events for 0.5 seconds after a frame that
	// took longer than 1/2 second
	if ( vrFrame.EnteredVrMode )
	{
		// we actually do want to use real time for this check and we don't have access to vrFrame in
		// OnKeyEvent where we need to check against this value.
		if ( GazeCursor != nullptr )
		{
			CancelTimer( 0, true );
		}
	}

	for ( int i = 0; i < vrFrame.AppEvents->NumEvents; ++i )
	{
		char const * jsonError;
		JSON * jsonObj = JSON::Parse( vrFrame.AppEvents->Events[i], &jsonError );
		JsonReader reader( jsonObj );
		OVR_ASSERT( jsonObj != NULL && reader.IsObject() );
		String command = reader.GetChildStringByName( "Command" );
		if ( OVR_stricmp( command.ToCStr(), SYSTEM_ACTIVITY_EVENT_REORIENT ) == 0 )
		{
			//LOG( "OvrGuiSysLocal::Frame - reorienting" );
			app->RecenterYaw( false );
			ResetMenuOrientations( app->GetLastViewMatrix() );
			// remove this event so the app doesn't handle it again
			SystemActivities_RemoveAppEvent( vrFrame.AppEvents, i );
			--i;
		}
		jsonObj->Release();
	}

	{
		OVR_PERF_TIMER( OvrGuiSys_Frame_Menus_Frame );
		// go backwards through the list so we can use unordered remove when a menu finishes closing
		for ( int i = ActiveMenus.GetSizeI() - 1; i >= 0; --i )
		{
			VRMenu * curMenu = ActiveMenus[i];
			OVR_ASSERT( curMenu != NULL );

			curMenu->Frame( *this, vrFrame, centerViewMatrix );

			if ( curMenu->GetCurMenuState() == VRMenu::MENUSTATE_CLOSED )
			{
				// remove from the active list
				ActiveMenus.RemoveAtUnordered( i );
				continue;
			}
		}
	}

	{
		OVR_PERF_TIMER( OvrGuiSys_GazeCursor_Frame );
		GazeCursor->Frame( centerViewMatrix, vrFrame.DeltaSeconds );	
	}

	{
		OVR_PERF_TIMER( OvrGuiSys_Frame_Font_Finish );
		DefaultFontSurface->Finish( centerViewMatrix );
	}

	{
		OVR_PERF_TIMER( OvrGuiSys_Frame_MenuMgr_Finish );
		MenuMgr->Finish( centerViewMatrix );
	}
}

//==============================
// OvrGuiSysLocal::AppendSurfaceList
void OvrGuiSysLocal::AppendSurfaceList( Matrix4f const & centerViewMatrix, Array< ovrDrawSurface > * surfaceList ) const
{
	if ( !IsInitialized || SkipRender )
	{
		OVR_ASSERT( IsInitialized );
		return;
	}

	if ( !SkipSubmit )
	{
		MenuMgr->AppendSurfaceList( centerViewMatrix, *surfaceList );
	}
	
	if ( !SkipFont )
	{
		DefaultFontSurface->AppendSurfaceList( *DefaultFont, *surfaceList );
	}

	if ( !SkipCursor )
	{
		GazeCursor->AppendSurfaceList( *surfaceList );
	}
}

//----DEPRECATED - DrawEyeView
//==============================
// OvrGuiSysLocal::RenderEyeView
void OvrGuiSysLocal::RenderEyeView( Matrix4f const & centerViewMatrix, Matrix4f const & viewMatrix, 
									Matrix4f const & projectionMatrix, ovrSurfaceRender & surfaceRender ) const
{
	OVR::Array< ovrDrawSurface > guiSurfaceList;
	AppendSurfaceList( centerViewMatrix, &guiSurfaceList );
	surfaceRender.RenderSurfaceList( guiSurfaceList, viewMatrix, projectionMatrix );
}
//----DEPRECATED - DrawEyeView

//==============================
// OvrGuiSysLocal::StartTimer
// This function prevent a down on the menu button from canceling the gaze cursor timer
// if the back button is still held, and vice versa. This can happen in Android 5.1.1 on
// the Note 5.
void OvrGuiSysLocal::StartTimer( int const keyCode, float const durationSeconds, 
		float const timeBeforeShowingTimer )
{
	double curTime = vrapi_GetTimeInSeconds();
	if ( ( MenuButtonDownTime < 0.0 || curTime - MenuButtonDownTime > MENU_BUTTON_LONG_PRESS_TIME_IN_SECONDS ) &&
		( BackButtonDownTime < 0.0 || curTime - BackButtonDownTime > BACK_BUTTON_LONG_PRESS_TIME_IN_SECONDS ) )
	{
		if ( keyCode == OVR_KEY_BACK || keyCode == 0 )
		{
			BackButtonDownTime = curTime;
		}
		else if ( keyCode == OVR_KEY_MENU || keyCode == 0 )
		{
			MenuButtonDownTime = curTime;
		}
		GazeCursor->StartTimer( durationSeconds, timeBeforeShowingTimer );
	}

}

//==============================
// OvrGuiSysLocal::CancelTimer
// This function prevent an up on the menu button from canceling the gaze cursor timer
// if the back button is still held, and vice versa. This is a hack to deal with Android
// 5.1.1 on Note 5 sending a menu button down/up when the back button is held for 0.5 seconds.
void OvrGuiSysLocal::CancelTimer( int const keyCode, bool const force )
{
	// reset the time for the passed key code
	if ( keyCode == OVR_KEY_BACK || keyCode == 0 )
	{
		BackButtonDownTime = -1.0;
	}
	if ( keyCode == OVR_KEY_MENU || keyCode == 0 )
	{
		MenuButtonDownTime = -1.0;
	}

	double curTime = vrapi_GetTimeInSeconds();
	if ( force || ( ( MenuButtonDownTime < 0.0 || curTime - MenuButtonDownTime > MENU_BUTTON_LONG_PRESS_TIME_IN_SECONDS ) &&
		( BackButtonDownTime < 0.0 || curTime - BackButtonDownTime > BACK_BUTTON_LONG_PRESS_TIME_IN_SECONDS ) ) )
	{
		//LOG( "Cancelling gaze timer: curTime = %f, MenuButtonDownTime = %f, BackButtonDownTime = %f",
		//		curTime, MenuButtonDownTime, BackButtonDownTime );
		GazeCursor->CancelTimer();
	}
}

//==============================
// OvrGuiSysLocal::OnKeyEvent
bool OvrGuiSysLocal::OnKeyEvent( int const keyCode, const int repeatCount, KeyEventType const eventType ) 
{
	if ( !IsInitialized )
	{
		OVR_ASSERT( IsInitialized );
		return false;
	}

	bool isSystemActivities = false;
#if defined( OVR_OS_ANDROID )
	if ( keyCode == OVR_KEY_BACK || keyCode == OVR_KEY_MENU )
	{
		isSystemActivities = ovr_IsCurrentActivity( app->GetJava()->Env, app->GetJava()->ActivityObject, PUI_CLASS_NAME );
	}
#endif
	// The back key is special because it has to handle short-press, long-press and double-tap.
	if ( keyCode == OVR_KEY_BACK )
	{
		// If this is not system activities.
		if ( !isSystemActivities )
		{
			// Update the gaze cursor timer.
			if ( eventType == KEY_EVENT_DOWN )
			{
				StartTimer( keyCode, BACK_BUTTON_LONG_PRESS_TIME_IN_SECONDS, BUTTON_DOUBLE_TAP_TIME_IN_SECONDS );
			} 
			else if ( eventType == KEY_EVENT_DOUBLE_TAP || eventType == KEY_EVENT_SHORT_PRESS )
			{
				CancelTimer( keyCode, false );
			}
			else if ( eventType == KEY_EVENT_LONG_PRESS )
			{
				CancelTimer( keyCode, false );
				GetApp()->StartSystemActivity( PUI_GLOBAL_MENU );
				return true;
			}
			else if ( eventType == KEY_EVENT_UP )
			{
				CancelTimer( keyCode, false );
			}
		}
	}
	else if ( keyCode == OVR_KEY_MENU )
	{
		// If this is not system activities.
		if ( !isSystemActivities )
		{
			// Update the gaze cursor timer.
			if ( eventType == KEY_EVENT_DOWN )
			{
				StartTimer( keyCode, MENU_BUTTON_LONG_PRESS_TIME_IN_SECONDS, BUTTON_DOUBLE_TAP_TIME_IN_SECONDS );
			} 
			else if ( eventType == KEY_EVENT_DOUBLE_TAP || eventType == KEY_EVENT_SHORT_PRESS || eventType == KEY_EVENT_LONG_PRESS )
			{
				CancelTimer( keyCode, false );
			}
			else if ( eventType == KEY_EVENT_UP )
			{
				CancelTimer( keyCode, false );
			}
		}
	}

	// menus ignore key repeats? I do not know why this is here any longer :(
	if ( repeatCount != 0 )
	{
		return false;
	}

	for ( int i = 0; i < ActiveMenus.GetSizeI(); ++i )
	{
		VRMenu * curMenu = ActiveMenus[i];
		OVR_ASSERT( curMenu != NULL );

		if ( keyCode == OVR_KEY_BACK ) 
		{
			LOG( "OvrGuiSysLocal back key event '%s' for menu '%s'", KeyEventNames[eventType], curMenu->GetName() );
		}

		if ( curMenu->OnKeyEvent( *this, keyCode, repeatCount, eventType ) )
		{
			LOG( "VRMenu '%s' consumed key event", curMenu->GetName() );
			return true;
		}
	}
	// we ignore other keys in the app menu for now
	return false;
}

bool OvrGuiSys::ovrDummySoundEffectPlayer::Has( const char* name ) const
{
	LOG( "ovrDummySoundEffectPlayer::Has( %s )", name );
	return false;
}

void OvrGuiSys::ovrDummySoundEffectPlayer::Play( const char* name )
{
	LOG( "ovrDummySoundEffectPlayer::Play( %s )", name );
}

void OvrGuiSys::ovrDummySoundEffectPlayer::LoadSoundAsset( const char* name )
{
	LOG( "ovrDummySoundEffectPlayer::LoadSoundAsset( %s )", name );
}

} // namespace OVR
