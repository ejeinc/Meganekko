/************************************************************************************

Filename    :   App.cpp
Content     :   Native counterpart to VrActivity and VrApp
Created     :   September 30, 2013
Authors     :   John Carmack

Copyright   :   Copyright 2014 Oculus VR, LLC. All Rights reserved.

*************************************************************************************/

#include "App.h"

#if defined( OVR_OS_ANDROID )
#include <jni.h>
#include <android/native_window_jni.h>	// for native window JNI
#include <unistd.h>						// gettid(), etc
#endif

#include <math.h>

#include "Kernel/OVR_System.h"
#include "Kernel/OVR_Math.h"
#include "Kernel/OVR_TypesafeNumber.h"
#include "Kernel/OVR_JSON.h"
#include "Android/JniUtils.h"

#include "stb_image.h"
#include "stb_image_write.h"

#include "VrApi.h"
#include "VrApi_Helpers.h"
#include "VrApi_Input.h"
#include "VrApi_LocalPrefs.h"
#include "VrApi_SystemUtils.h"


#include "GlSetup.h"
#include "GlTexture.h"
#include "VrCommon.h"
#include "AppLocal.h"
#include "DebugLines.h"
#include "PackageFiles.h"
#include "Console.h"
#include "OVR_Uri.h"
#include "OVR_FileSys.h"
#include "OVR_TextureManager.h"
#include "OVR_Input.h"
#include "DebugConsole.h"

#include "embedded/dependency_error_de.h"
#include "embedded/dependency_error_en.h"
#include "embedded/dependency_error_es.h"
#include "embedded/dependency_error_fr.h"
#include "embedded/dependency_error_it.h"
#include "embedded/dependency_error_ja.h"
#include "embedded/dependency_error_ko.h"

//#define OVR_USE_PERF_TIMER
#include "OVR_PerfTimer.h"

static double AppLocalConstructTime = -1.0;	// time when AppLocal was constructed

// some parameters from the intent can be empty strings, which cannot be represented as empty strings for sscanf
// so we encode them as EMPTY_INTENT_STR.
// Because the message queue handling uses sscanf() to parse the message, the JSON text is 
// always placed at the end of the message because it can contain spaces while the package
// name and URI cannot. The handler will use sscanf() to parse the first two strings, then
// assume the JSON text is everything immediately following the space after the URI string.
static const char * EMPTY_INTENT_STR = "<EMPTY>";
void ComposeIntentMessage( char const * packageName, char const * uri, char const * jsonText, 
		char * out, size_t outSize )
{
	OVR::OVR_sprintf( out, outSize, "intent %s %s %s",
			packageName == NULL || packageName[0] == '\0' ? EMPTY_INTENT_STR : packageName,
			uri == NULL || uri[0] == '\0' ? EMPTY_INTENT_STR : uri,
			jsonText == NULL || jsonText[0] == '\0' ? "" : jsonText );
}

// Initialize and shutdown the app framework version of LibOVR.
static struct InitShutdown
{
	InitShutdown()
	{
		OVR::System::Init( OVR::Log::ConfigureDefaultLog( OVR::LogMask_All ) );
	}
	~InitShutdown()
	{
		OVR::System::Destroy();
	}
} GlobalInitShutdown;

namespace OVR
{
//--- MV_INVALIDATE_WORKAROUND
static const char * clearVertexShaderSrc =
	"attribute vec4 Position;\n"
	"uniform lowp vec4 UniformColor;\n"
	"varying  lowp vec4 oColor;\n"
	"void main()\n"
	"{\n"
	"   gl_Position = Position;\n"
	"   oColor = UniformColor;\n"
	"}\n";

static const char * clearFragmentShaderSrc =
	"varying lowp vec4	oColor;\n"
	"void main()\n"
	"{\n"
	"	gl_FragColor = oColor;\n"
	"}\n";
//--- MV_INVALIDATE_WORKAROUND

//=======================================================================================
// Default handlers for VrAppInterface

VrAppInterface::VrAppInterface() :
	app( NULL ),
	ActivityClass( NULL )
{
}

VrAppInterface::~VrAppInterface()
{
	if ( ActivityClass != NULL )
	{
		// FIXME:
		//jni->DeleteGlobalRef( ActivityClass );
		//ActivityClass = NULL;
	}
}

void VrAppInterface::Configure( ovrSettings & settings )
{ 
	LOG( "VrAppInterface::Configure - default handler called" );
	OVR_UNUSED( settings );
}

void VrAppInterface::EnteredVrMode( const ovrIntentType intentType, const char * intentFromPackage, const char * intentJSON, const char * intentURI )
{
	LOG( "VrAppInterface::EnteredVrMode - default handler called" );
	OVR_UNUSED( intentType );
	OVR_UNUSED( intentFromPackage );
	OVR_UNUSED( intentJSON );
	OVR_UNUSED( intentURI );
}

void VrAppInterface::LeavingVrMode()
{
	LOG( "VrAppInterface::LeavingVrMode - default handler called" );
}

ovrFrameResult VrAppInterface::Frame( const ovrFrameInput & vrFrame )
{
	LOG( "VrAppInterface::Frame - default handler called" );
	OVR_UNUSED( vrFrame );
	return ovrFrameResult();
}

//==============================
// WaitForDebuggerToAttach
//
// wait on the debugger... once it is attached, change waitForDebugger to false
void WaitForDebuggerToAttach()
{
	static volatile bool waitForDebugger = true;
	while ( waitForDebugger )
	{
		// put your breakpoint on the sleep to wait
		Thread::MSleep( 100 );
	}
}

//=======================================================================================

extern void DebugMenuBounds( void * appPtr, const char * cmd );
extern void DebugMenuHierarchy( void * appPtr, const char * cmd );
extern void DebugMenuPoses( void * appPtr, const char * cmd );

App::~App()
{
	// avoids "undefined reference to 'vtable for OVR::App'" error
}

/*
 * AppLocal
 *
 * Called once at startup.
 *
 * ?still true?: exit() from here causes an immediate app re-launch,
 * move everything to first surface init?
 */
AppLocal::AppLocal( JNIEnv & jni_, jobject activityObject_, VrAppInterface & interface_ )
	: ExitOnDestroy( true )
	, IntentType( INTENT_LAUNCH )
	, pendingNativeWindow( NULL )
	, VrThreadSynced( false )
	, ReadyToExit( false )
	, Resumed( false )
	, appInterface( NULL )
	, MessageQueue( 100 )
	, nativeWindow( NULL )
	, FramebufferIsSrgb( false )
	, FramebufferIsProtected( false )
	, UseMultiview( false )
	, OvrMobile( NULL )
	, EyeBuffers( NULL )
	, VrActivityClass( NULL )
	, finishActivityMethodId( NULL )
	, IntentURI()
	, IntentJSON()
	, IntentFromPackage()
	, PackageName( "" )
	, SuggestedEyeFovDegreesX( 90.0f )
	, SuggestedEyeFovDegreesY( 90.0f )
	, InputEvents()
	, TheVrFrame()
	, EnteredVrModeFrame( 0 )
	, VrThread( &ThreadStarter, this, 256 * 1024 )
	, ExitCode( 0 )
	, RecenterYawFrameStart( 0 )
	, DebugLines( NULL )
	, StoragePaths( NULL )
	, LoadingIconTextureChain( 0 )
	, ErrorTextureSwapChain( NULL )
	, ErrorTextureSize( 0 )
	, ErrorMessageEndTime( -1.0 )
	, FileSys( nullptr )
	, TextureManager( nullptr )
{
	LOG( "----------------- AppLocal::AppLocal() -----------------");

	AppLocalConstructTime = vrapi_GetTimeInSeconds();

	// Set the VrAppInterface
	appInterface = &interface_;

	StoragePaths = new OvrStoragePaths( &jni_, activityObject_);

	//WaitForDebuggerToAttach();

#if defined( OVR_OS_ANDROID )
	jni_.GetJavaVM( &Java.Vm );
	Java.Env = NULL;	// set from the VrThread
	Java.ActivityObject = jni_.NewGlobalRef( activityObject_ );

	VrActivityClass = ovr_GetGlobalClassReference( &jni_, activityObject_, "com/oculus/vrappframework/VrActivity" );

	finishActivityMethodId = ovr_GetMethodID( &jni_, VrActivityClass, "finishActivity", "()V" );

	const jmethodID isHybridAppMethodId = ovr_GetStaticMethodID( &jni_, VrActivityClass, "isHybridApp", "(Landroid/app/Activity;)Z" );
	bool const isHybridApp = jni_.CallStaticBooleanMethod( VrActivityClass, isHybridAppMethodId, Java.ActivityObject );
#else
	Java.Vm = NULL;
	Java.Env = NULL;
	Java.ActivityObject = NULL;
	bool const isHybridApp = false;
#endif

	ExitOnDestroy = !isHybridApp;

	// Default display settings.
	VrSettings.ShowLoadingIcon = true;
	VrSettings.UseSrgbFramebuffer = false;
	VrSettings.UseProtectedFramebuffer = false;
	VrSettings.Use16BitFramebuffer = false;
	VrSettings.MinimumVsyncs = 1;
	VrSettings.RenderMode = RENDERMODE_STEREO;

	// Default ovrModeParms
	VrSettings.ModeParms = vrapi_DefaultModeParms( &Java );
	VrSettings.ModeParms.Flags |= VRAPI_MODE_FLAG_ALLOW_POWER_SAVE;
	// Must reset the FLAG_FULLSCREEN window flag when using a SurfaceView
	VrSettings.ModeParms.Flags |= VRAPI_MODE_FLAG_RESET_WINDOW_FULLSCREEN;

	// Default ovrPerformanceParms
	VrSettings.PerformanceParms = vrapi_DefaultPerformanceParms();
	VrSettings.PerformanceParms.CpuLevel = 2;
	VrSettings.PerformanceParms.GpuLevel = 2;

	// Default ovrEyeBufferParms (will be overwritten later based on hmdInfo)
	VrSettings.EyeBufferParms.resolutionWidth = 1024;
	VrSettings.EyeBufferParms.resolutionHeight = 1024;
	VrSettings.EyeBufferParms.multisamples = 4;
	VrSettings.EyeBufferParms.colorFormat = COLOR_8888;
	VrSettings.EyeBufferParms.depthFormat = DEPTH_24;

	// Default ovrHeadModelParms
	VrSettings.HeadModelParms = vrapi_DefaultHeadModelParms();

#if defined( OVR_OS_WIN32 )
	VrSettings.WindowParms.IconResourceId = 32517;	// TODO: Make a default appframework icon, currently defaults to IDI_WINLOGO
	VrSettings.WindowParms.Title = "VrApp";
#endif

#if defined( OVR_OS_ANDROID )
	// Get the path to the .apk and package name
	{
		char temp[1024];
		ovr_GetCurrentPackageName( &jni_, Java.ActivityObject, temp, sizeof( temp ) );
		PackageName = temp;

		ovr_GetPackageCodePath( &jni_, Java.ActivityObject, temp, sizeof( temp ) );

		String	outPath;
		const bool validCacheDir = StoragePaths->GetPathIfValidPermission(
				EST_INTERNAL_STORAGE, EFT_CACHE, "", permissionFlags_t( PERMISSION_WRITE ) | PERMISSION_READ, outPath );
		ovr_OpenApplicationPackage( temp, validCacheDir ? outPath.ToCStr() : NULL );
	}
#endif
}

AppLocal::~AppLocal()
{
	LOG( "---------- ~AppLocal() ----------" );

	delete StoragePaths;
}

void AppLocal::StartVrThread()
{
	LOG( "StartVrThread" );

	if ( VrThread.Start() == false )
	{
		FAIL( "VrThread.Start() failed" );
	}

	// Wait for the thread to be up and running.
	MessageQueue.SendPrintf( "sync " );
}

void AppLocal::StopVrThread()
{
	LOG( "StopVrThread" );

	MessageQueue.PostPrintf( "quit " );

	if ( VrThread.Join() == false )
	{
		WARN( "VrThread failed to terminate." );
	}
}

void * AppLocal::JoinVrThread()
{
	LOG( "JoinVrThread" );

	VrThread.Join();

	return VrThread.GetExitCode();
}

ovrMessageQueue & AppLocal::GetMessageQueue()
{
	return MessageQueue;
}

// Sends an intent to another application built with VrAppFramework. Command and Uri will be parsed
// and sent to that applications onNewIntent().
void AppLocal::SendIntent( const char * actionName, const char * toPackageName,
							const char * toClassName, const char * command, const char * uri )
{
	LOG( "AppLocal::SendIntent( '%s' '%s/%s' '%s' '%s' )", actionName, toPackageName, toClassName, 
			( command != NULL ) ? command : "<NULL>",
			( uri != NULL ) ? uri : "<NULL>" );

	// Push black images to the screen to eliminate any frames of lost head tracking.
	ovrFrameParms blackFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_BLACK_FINAL, vrapi_GetTimeInSeconds(), NULL );
	blackFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
	vrapi_SubmitFrame( OvrMobile, &blackFrameParms );

#if defined( OVR_OS_ANDROID )
	JavaString actionString( Java.Env, actionName );
	JavaString packageString( Java.Env, toPackageName );
	JavaString className( Java.Env, toClassName );
	JavaString commandString( Java.Env, command == NULL ? "" : command );
	JavaString uriString( Java.Env, uri == NULL ? "" : uri );

	jmethodID sendIntentFromNativeId = ovr_GetStaticMethodID( Java.Env, VrActivityClass, 
			"sendIntentFromNative", "(Landroid/app/Activity;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V" );
	if ( sendIntentFromNativeId != NULL )
	{
		Java.Env->CallStaticVoidMethod( VrActivityClass, sendIntentFromNativeId, Java.ActivityObject, 
				actionString.GetJString(), packageString.GetJString(), className.GetJString(), 
				commandString.GetJString(), uriString.GetJString() );
	}
#endif
}

void AppLocal::SendLaunchIntent( const char * toPackageName, const char * command, const char * uri,
								 const char * action )
{
	LOG( "AppLocal::SendLaunchIntent( '%s' '%s' '%s' '%s' )", toPackageName, 
			( command != NULL ) ? command : "<NULL>",
			( uri != NULL ) ? uri : "<NULL>",
			( action != NULL ) ? action : "<NULL>" );

	// Push black images to the screen to eliminate any frames of lost head tracking.
	ovrFrameParms blackFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_BLACK_FINAL, vrapi_GetTimeInSeconds(), NULL );
	blackFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
	vrapi_SubmitFrame( OvrMobile, &blackFrameParms );

	OVR_ASSERT( command != NULL );
#if defined( OVR_OS_ANDROID )
	JavaString packageString( Java.Env, toPackageName );
	JavaString commandString( Java.Env, command == NULL ? "" : command );
	JavaString uriString( Java.Env, uri == NULL ? "" : uri );
	JavaString actionString( Java.Env, action == NULL ? "" : action );

	jmethodID sendLaunchIntentId = ovr_GetStaticMethodID( Java.Env, VrActivityClass,
			"sendLaunchIntent", "(Landroid/app/Activity;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V" );
	if ( sendLaunchIntentId != NULL )
	{
		Java.Env->CallStaticVoidMethod( VrActivityClass, sendLaunchIntentId, Java.ActivityObject,
				packageString.GetJString(), commandString.GetJString(), uriString.GetJString(),
				actionString.GetJString() );
	}
#endif
}

struct embeddedImage_t
{
	char const *	ImageName;
	void *			ImageBuffer;
	size_t			ImageSize;
};

// for each error type, add an array of errorImage_t with an entry for each language
static const embeddedImage_t EmbeddedImages[] =
{
	{ "dependency_error_en.png",		dependencyErrorEnData,		dependencyErrorEnSize },
	{ "dependency_error_de.png",		dependencyErrorDeData,		dependencyErrorDeSize },
	{ "dependency_error_en-rGB.png",	dependencyErrorEnData,		dependencyErrorEnSize },
	{ "dependency_error_es.png",		dependencyErrorEsData,		dependencyErrorEsSize },
	{ "dependency_error_es-rES.png",	dependencyErrorEsData,		dependencyErrorEsSize },
	{ "dependency_error_fr.png",		dependencyErrorFrData,		dependencyErrorFrSize },
	{ "dependency_error_it.png",		dependencyErrorItData,		dependencyErrorItSize },
	{ "dependency_error_ja.png",		dependencyErrorJaData,		dependencyErrorJaSize },
	{ "dependency_error_ko.png",		dependencyErrorKoData,		dependencyErrorKoSize },
	{ NULL, NULL, 0 }
};

static embeddedImage_t const * FindErrorImage( embeddedImage_t const * list, char const * name )
{
	for ( int i = 0; list[i].ImageName != NULL; ++i )
	{
		if ( OVR::OVR_stricmp( list[i].ImageName, name ) == 0 )
		{
			LOG( "Found embedded image for '%s'", name );
			return &list[i];
		}
	}

	return NULL;
}

static bool FindEmbeddedImage( char const * imageName, void ** buffer, int * bufferSize )
{
	*buffer = NULL;
	*bufferSize = 0;

	embeddedImage_t const * image = FindErrorImage( EmbeddedImages, imageName );
	if ( image == NULL ) 
	{
		WARN( "No embedded image named '%s' was found!", imageName );
		return false;
	}

	*buffer = image->ImageBuffer;
	*bufferSize = static_cast<int>( image->ImageSize );
	return true;
}

#if defined( OVR_OS_ANDROID )
static const double SHOW_ERROR_MSG_SECONDS = 15.0;
#else
static const double SHOW_ERROR_MSG_SECONDS = 0.0;
#endif

void AppLocal::ShowDependencyError()
{
	LOG( "AppLocal::ShowDependencyError" );

#if defined( OVR_OS_ANDROID )
	// clear any pending exception to ensure no pending exception causes the error message to fail
	if ( Java.Env->ExceptionOccurred() )
	{
		Java.Env->ExceptionClear();
	}
#endif

	// Android specific
	OVR::String imageName = "dependency_error_";

	// call into Java directly here to get the language code
	char const * localeCode = "en";
#if defined( OVR_OS_ANDROID )
	JavaClass javaLocaleClass( Java.Env, ovr_GetLocalClassReference( Java.Env, Java.ActivityObject, "java/util/Locale" ) );
	jmethodID getDefaultId = ovr_GetStaticMethodID( Java.Env, javaLocaleClass.GetJClass(), "getDefault", "()Ljava/util/Locale;" );
	if ( getDefaultId != NULL )
	{
		JavaObject javaDefaultLocaleObject( Java.Env, Java.Env->CallStaticObjectMethod( javaLocaleClass.GetJClass(),getDefaultId ) );
		if ( javaDefaultLocaleObject.GetJObject() != NULL )
		{
			jmethodID getLocaleId = Java.Env->GetMethodID( javaLocaleClass.GetJClass(), "getLanguage", "()Ljava/lang/String;" );
			if ( getLocaleId != NULL )
			{
				JavaUTFChars javaLocale( Java.Env, (jstring)Java.Env->CallObjectMethod( javaDefaultLocaleObject.GetJObject(), getLocaleId ) );
				if ( javaLocale.GetJString() != NULL )
				{
					imageName += javaLocale.ToStr();
					localeCode = NULL;
				}
			}
		}
	}
#endif

	if ( localeCode != NULL )
	{
		imageName += localeCode;
	}
	imageName += ".png";

	void * imageBuffer = NULL;
	int imageSize = 0;
	if ( !FindEmbeddedImage( imageName.ToCStr(), &imageBuffer, &imageSize ) )
	{
		// try to default to English
		imageName = "dependency_error_en.png";
		if ( !FindEmbeddedImage( imageName.ToCStr(), &imageBuffer, &imageSize ) )
		{
			FAIL( "Failed to load error message texture!" );
		}
	}

	// Load the error texture from a buffer.
	{
		int width = 0;
		int height = 0;
		int comp = 0;
		stbi_uc * image = stbi_load_from_memory( (unsigned char *)imageBuffer, imageSize, &width, &height, &comp, 4 );

		OVR_ASSERT( image != NULL );
		if ( image != NULL )
		{
			OVR_ASSERT( width == height );

			// Only 1 mip level needed.
			ErrorTextureSwapChain = vrapi_CreateTextureSwapChain( VRAPI_TEXTURE_TYPE_2D, VRAPI_TEXTURE_FORMAT_8888, width, height, 1, false );
			ErrorTextureSize = width;

			glBindTexture( GL_TEXTURE_2D, vrapi_GetTextureSwapChainHandle( ErrorTextureSwapChain, 0 ) );
			glTexSubImage2D( GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, image );
			glBindTexture( GL_TEXTURE_2D, 0 );

			free( image );
		}
	}

	ErrorMessageEndTime = vrapi_GetTimeInSeconds() + SHOW_ERROR_MSG_SECONDS;
}

void AppLocal::ShowSystemUI( const ovrSystemUIType uiType )
{
	if ( vrapi_ShowSystemUI( &Java, uiType ) )
	{
		// Push black images to the screen to eliminate any frames of lost head tracking.
		ovrFrameParms blackFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_BLACK_FINAL, vrapi_GetTimeInSeconds(), NULL );
		blackFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
		vrapi_SubmitFrame( OvrMobile, &blackFrameParms );
		return;
	}
	if ( ErrorTextureSwapChain != NULL )
	{
		// already in an error state
		return;
	}

	LOG( "*************************************************************************" );
	LOG( "A fatal dependency error occured. Oculus SystemActivities failed to start.");
	LOG( "*************************************************************************" );
	vrapi_ReturnToHome( &Java );
}

void AppLocal::FinishActivity( const ovrAppFinishType type )
{
	// Push black images to the screen to eliminate any frames of lost head tracking.
	ovrFrameParms blackFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_BLACK_FINAL, vrapi_GetTimeInSeconds(), NULL );
	blackFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
	vrapi_SubmitFrame( OvrMobile, &blackFrameParms );

#if defined( OVR_OS_ANDROID )
	if ( type == FINISH_NORMAL )
	{
		const jmethodID mid = ovr_GetStaticMethodID( Java.Env, VrActivityClass,
				"finishOnUiThread", "(Landroid/app/Activity;)V" );

		if ( Java.Env->ExceptionOccurred() )
		{
			Java.Env->ExceptionClear();
			LOG( "Cleared JNI exception" );
		}
		LOG( "Calling activity.finishOnUiThread()" );
		Java.Env->CallStaticVoidMethod( VrActivityClass, mid, Java.ActivityObject );
		LOG( "Returned from activity.finishOnUiThread()" );
	}
	else if ( type == FINISH_AFFINITY )
	{
		const char * name = "finishAffinityOnUiThread";
		const jmethodID mid = ovr_GetStaticMethodID( Java.Env, VrActivityClass,
				name, "(Landroid/app/Activity;)V" );

		if ( Java.Env->ExceptionOccurred() )
		{
			Java.Env->ExceptionClear();
			LOG( "Cleared JNI exception" );
		}
		LOG( "Calling activity.finishAffinityOnUiThread()" );
		Java.Env->CallStaticVoidMethod( VrActivityClass, mid, Java.ActivityObject );
		LOG( "Returned from activity.finishAffinityOnUiThread()" );
	}
#endif
}

void AppLocal::FatalError( const ovrAppFatalError appError, const char * fileName, const unsigned int lineNumber,
						   const char * messageFormat, ... )
{
	char errorMessage[2048];
	va_list argPtr;
	va_start( argPtr, messageFormat );
	OVR::OVR_vsprintf( errorMessage, sizeof( errorMessage ), messageFormat, argPtr );
	va_end( argPtr );

	// map the app fatal error to the SA fatal error message
	// NOTE: These error message strings are used to determine which error image is shown.
	const char * appErrorToFatalTitle[FATAL_ERROR_MAX] = {
		"failOutOfMemory",
		"failOutOfStorage",
		"failUnknown"
	};
	// open SA with a fatal error message
	vrapi_ShowFatalError( &Java, appErrorToFatalTitle[appError], errorMessage, fileName, lineNumber );
	vrapi_Shutdown();
	exit( 0 );
}

void AppLocal::ReadFileFromApplicationPackage( const char * nameInZip, int &length, void * & buffer )
{
	ovr_ReadFileFromApplicationPackage( nameInZip, length, buffer );
}

void AppLocal::InitGlObjects()
{
	if ( EyeBuffers != NULL )
	{
		return;
	}

#if defined( OVR_OS_ANDROID )
	// Create a new context and pbuffer surface
	if ( VrSettings.Use16BitFramebuffer )
	{
		glSetup = GL_Setup( EGL_NO_CONTEXT, GL_ES_VERSION,	// no share context,
				5,6,5 /* rgb */, 0 /* depth */, 0 /* samples */,
				EGL_CONTEXT_PRIORITY_MEDIUM_IMG );
	}
	else
	{
		glSetup = GL_Setup( EGL_NO_CONTEXT, GL_ES_VERSION,	// no share context,
				8,8,8 /* rgb */, 0 /* depth */, 0 /* samples */,
				EGL_CONTEXT_PRIORITY_MEDIUM_IMG );
	}
#else
	const int displayPixelsWide = vrapi_GetSystemPropertyInt( &Java, VRAPI_SYS_PROP_DISPLAY_PIXELS_WIDE );
	const int displayPixelsHigh = vrapi_GetSystemPropertyInt( &Java, VRAPI_SYS_PROP_DISPLAY_PIXELS_HIGH );
	glSetup = GL_Setup( displayPixelsWide / 2, displayPixelsHigh / 2, false,
						VrSettings.WindowParms.Title.ToCStr(), VrSettings.WindowParms.IconResourceId,
						this );
#endif

	// Let glUtils look up extensions
	GL_InitExtensions();

	// Determine if multiview rendering is requested (and available) before initializing
	// our GL objects.
	UseMultiview = ( VrSettings.RenderMode == RENDERMODE_MULTIVIEW ) && extensionsOpenGL.OVR_multiview2 && GetSystemProperty( VRAPI_SYS_PROP_MULTIVIEW_AVAILABLE );
	LOG( "Use Multiview: %s", UseMultiview ? "true" : "false" );

	GlProgram::SetUseMultiview( UseMultiview );

	//--- MV_INVALIDATE_WORKAROUND
	static ovrProgramParm screenClearParms[] = 
	{
		{ "UniformColor",	ovrProgramParmType::FLOAT_VECTOR4 },
	};
	ScreenClearSurf.geo = BuildTesselatedQuad( 1, 1 );
	ScreenClearSurf.graphicsCommand.Program = GlProgram::Build( clearVertexShaderSrc, clearFragmentShaderSrc,
															screenClearParms, sizeof( screenClearParms ) / sizeof( ovrProgramParm )  );
	ScreenClearSurf.graphicsCommand.UniformData[0].Data = &ScreenClearColor;
	ScreenClearSurf.graphicsCommand.GpuState.depthEnable = false;
	ScreenClearSurf.graphicsCommand.GpuState.depthMaskEnable = false;
	ScreenClearSurf.graphicsCommand.GpuState.cullEnable = false;
	//--- MV_INVALIDATE_WORKAROUND	

	TextureManager = ovrTextureManager::Create();

	SurfaceRender.Init();

	EyeBuffers = new ovrEyeBuffers;

	DebugLines = OvrDebugLines::Create();
	DebugLines->Init();

	void * imageBuffer;
	int	imageSize;
	ovr_ReadFileFromApplicationPackage( "res/raw/loading_indicator.png", imageSize, imageBuffer );
	if ( imageBuffer != NULL )
	{
		int width = 0;
		int height = 0;
		int comp = 0;
		stbi_uc * image = stbi_load_from_memory( (unsigned char *)imageBuffer, imageSize, &width, &height, &comp, 4 );

		OVR_ASSERT( image != NULL );
		if ( image != NULL )
		{
			OVR_ASSERT( width == height );

			// Only 1 mip level needed.
			LoadingIconTextureChain = vrapi_CreateTextureSwapChain( VRAPI_TEXTURE_TYPE_2D, VRAPI_TEXTURE_FORMAT_8888, width, height, 1, false );

			glBindTexture( GL_TEXTURE_2D, vrapi_GetTextureSwapChainHandle( LoadingIconTextureChain, 0 ) );
			glTexSubImage2D( GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, image );
			glBindTexture( GL_TEXTURE_2D, 0 );

			free( image );
		}
	}

	DebugConsole::Init( appInterface );
}

void AppLocal::ShutdownGlObjects()
{
	DebugConsole::Shutdown();

	DebugLines->Shutdown();
	OvrDebugLines::Free( DebugLines );

	delete EyeBuffers;
	EyeBuffers = NULL;

	if ( LoadingIconTextureChain != NULL )
	{
		vrapi_DestroyTextureSwapChain( LoadingIconTextureChain );
		LoadingIconTextureChain = NULL;
	}
	if ( ErrorTextureSwapChain != NULL )
	{
		vrapi_DestroyTextureSwapChain( ErrorTextureSwapChain );
		ErrorTextureSwapChain = NULL;
	}

	//--- MV_INVALIDATE_WORKAROUND
	GlProgram::Free( ScreenClearSurf.graphicsCommand.Program );
	ScreenClearSurf.geo.Free();
	//--- MV_INVALIDATE_WORKAROUND

	SurfaceRender.Shutdown();

	GL_Shutdown( glSetup );
}

Vector3f ViewOrigin( const Matrix4f & view )
{
	return Vector3f( view.M[0][3], view.M[1][3], view.M[2][3] );
}

Vector3f ViewForward( const Matrix4f & view )
{
	return Vector3f( -view.M[0][2], -view.M[1][2], -view.M[2][2] );
}

Vector3f ViewUp( const Matrix4f & view )
{
	return Vector3f( view.M[0][1], view.M[1][1], view.M[2][1] );
}

Vector3f ViewRight( const Matrix4f & view )
{
	return Vector3f( view.M[0][0], view.M[1][0], view.M[2][0] );
}

void AppLocal::EnterVrMode()
{
	LOG( "AppLocal::EnterVrMode()" );

	// Initialize the eye buffers.
	EyeBuffers->Initialize( VrSettings.EyeBufferParms, UseMultiview );

#if defined( OVR_OS_ANDROID )
	VrSettings.ModeParms.Flags |= VRAPI_MODE_FLAG_NATIVE_WINDOW;
	VrSettings.ModeParms.Display = (size_t)glSetup.display;
	VrSettings.ModeParms.WindowSurface = (size_t)nativeWindow;
	VrSettings.ModeParms.ShareContext = (size_t)glSetup.context;
#endif

	EnteredVrModeFrame = TheVrFrame.Get().FrameNumber;

	// Enter VR mode.
	OvrMobile = vrapi_EnterVrMode( &VrSettings.ModeParms );

#if defined( OVR_OS_WIN32 )
	// TODO: these are just mirroring the values in VrSettings as some parts of the app query these.
	// afaik nothing else is being done to support these for PC.
	FramebufferIsSrgb = VrSettings.UseSrgbFramebuffer;
	FramebufferIsProtected = VrSettings.UseProtectedFramebuffer;
#else
	FramebufferIsSrgb = GetSystemStatus( VRAPI_SYS_STATUS_FRONT_BUFFER_SRGB ) != 0;
	FramebufferIsProtected = GetSystemStatus( VRAPI_SYS_STATUS_FRONT_BUFFER_PROTECTED ) != 0;
#endif

	LOG( "FramebufferIsSrgb: %s", FramebufferIsSrgb ? "true" : "false" );
	LOG( "FramebufferIsProtected: %s", FramebufferIsProtected ? "true" : "false" );

	// Optionally enable GPU timings for the eye buffers
	SetAllowGpuTimerQueries( atoi( ovr_GetLocalPreferenceValueForKey( LOCAL_PREF_VRAPI_GPU_TIMINGS, "0" ) ) );

	// Now that we are in VR mode, release the UI thread before doing a potentially long load.
	MessageQueue.NotifyMessageProcessed();

	// Update network state that doesn't need to be updated every frame.
	TheVrFrame.UpdateNetworkState( Java.Env, VrActivityClass, Java.ActivityObject );

	// Let the client app initialize only once by calling EnteredVrMode with INTENT_LAUNCH.
	// This is called after entering VR mode to be able to show a time warp loading icon.
	if ( IntentType != INTENT_OLD )
	{
		if ( VrSettings.ShowLoadingIcon )
		{
			ovrFrameParms loadingIconFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_LOADING_ICON_FLUSH, vrapi_GetTimeInSeconds(), LoadingIconTextureChain );
			loadingIconFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
			vrapi_SubmitFrame( OvrMobile, &loadingIconFrameParms );
		}
	}

	if ( IntentType != INTENT_LAUNCH )
	{
		// If this is a resume after EnteredVrMode( INTENT_LAUNCH ), then automatically reorient.
		//LOG( "EnterVrMode - Reorienting" );
		SetRecenterYawFrameStart( TheVrFrame.Get().FrameNumber + 1 );
	}

	// Notify the application that we are in VR mode.
	LOG( "VrAppInterface::EnteredVrMode()" );
	LOG( "intentType: %d", IntentType );
	LOG( "intentFromPackage: %s", IntentFromPackage.ToCStr() );
	LOG( "intentJSON: %s", IntentJSON.ToCStr() );
	LOG( "intentURI: %s", IntentURI.ToCStr() );
	appInterface->EnteredVrMode( IntentType, IntentFromPackage.ToCStr(), IntentJSON.ToCStr(), IntentURI.ToCStr() );

	IntentType = INTENT_OLD;

	if ( AppLocalConstructTime > 0.0 )
	{
		LOG( "Time to finish OneTimeInit = %f", vrapi_GetTimeInSeconds() - AppLocalConstructTime );
		AppLocalConstructTime = -1.0;
	}
}

void AppLocal::LeaveVrMode()
{
	LOG( "AppLocal::LeaveVrMode()" );

	// Notify the application that we are about to leave VR mode.
	appInterface->LeavingVrMode();

	// Push black images to the screen so that we don't see last front buffer image without any head tracking
	ovrFrameParms blackFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_BLACK_FINAL, vrapi_GetTimeInSeconds(), NULL );
	// when exiting VR mode, don't show a frame of a volume layer because the app may have disabled the layer
	blackFrameParms.Flags |= VRAPI_FRAME_FLAG_INHIBIT_VOLUME_LAYER;	
	blackFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
	vrapi_SubmitFrame( OvrMobile, &blackFrameParms );

	vrapi_LeaveVrMode( OvrMobile );
	OvrMobile = NULL;
}

void AppLocal::Configure()
{
	LOG( "AppLocal::Configure()" );

	VrSettings.ShowLoadingIcon = true;
	VrSettings.UseSrgbFramebuffer = false;
	VrSettings.UseProtectedFramebuffer = false;
	VrSettings.Use16BitFramebuffer = false;
	VrSettings.RenderMode = RENDERMODE_STEREO;

	VrSettings.EyeBufferParms.resolutionWidth = vrapi_GetSystemPropertyInt( &Java, VRAPI_SYS_PROP_SUGGESTED_EYE_TEXTURE_WIDTH );
	VrSettings.EyeBufferParms.resolutionHeight = vrapi_GetSystemPropertyInt( &Java, VRAPI_SYS_PROP_SUGGESTED_EYE_TEXTURE_HEIGHT );
	VrSettings.EyeBufferParms.multisamples = vrapi_GetSystemPropertyInt( &Java, VRAPI_SYS_PROP_MAX_FULLSPEED_FRAMEBUFFER_SAMPLES );
	VrSettings.EyeBufferParms.colorFormat = COLOR_8888;
	VrSettings.EyeBufferParms.depthFormat = DEPTH_24;

	// Allow the app to override any settings.
	appInterface->Configure( VrSettings );

	// Make sure the app didn't mess up the Java pointers.
	VrSettings.ModeParms.Java = Java;

	// Allow localparms to override some settings.
	if ( atoi( ovr_GetLocalPreferenceValueForKey( "app_force16BitFramebuffer", "0" ) ) )
	{
		VrSettings.Use16BitFramebuffer = true;
	}

	// FIXME: have apps set these flags directly.

	if ( VrSettings.UseProtectedFramebuffer )
	{
		VrSettings.ModeParms.Flags |= VRAPI_MODE_FLAG_FRONT_BUFFER_PROTECTED;
	}
	else
	{
		VrSettings.ModeParms.Flags &= ~VRAPI_MODE_FLAG_FRONT_BUFFER_PROTECTED;
	}
	if ( VrSettings.Use16BitFramebuffer )
	{
		VrSettings.ModeParms.Flags |= VRAPI_MODE_FLAG_FRONT_BUFFER_565;
	}
	else
	{
		VrSettings.ModeParms.Flags &= ~VRAPI_MODE_FLAG_FRONT_BUFFER_565;
	}
	if ( VrSettings.UseSrgbFramebuffer )
	{
		VrSettings.ModeParms.Flags |= VRAPI_MODE_FLAG_FRONT_BUFFER_SRGB;
	}
	else
	{
		VrSettings.ModeParms.Flags &= ~VRAPI_MODE_FLAG_FRONT_BUFFER_SRGB;
	}

	InitGlObjects();
}

/*
 * Command
 *
 * Process commands sent over the message queue for the VR thread.
 *
 */
void AppLocal::Command( const char * msg )
{
	// Always include the space in MatchesHead to prevent problems
	// with commands that have matching prefixes.

	if ( MatchesHead( "sync ", msg ) )
	{
		LOG( "%p msg: VrThreadSynced", this );
		VrThreadSynced = true;
		return;
	}

	if ( MatchesHead( "surfaceCreated ", msg ) )
	{
		LOG( "%p msg: surfaceCreated", this );
		nativeWindow = pendingNativeWindow;
		HandleVrModeChanges();
		return;
	}

	if ( MatchesHead( "surfaceDestroyed ", msg ) )
	{
		LOG( "%p msg: surfaceDestroyed", this );
		nativeWindow = NULL;
		HandleVrModeChanges();
		return;
	}

	if ( MatchesHead( "resume ", msg ) )
	{
		LOG( "%p msg: resume", this );
		Resumed = true;
		HandleVrModeChanges();
		return;
	}

	if ( MatchesHead( "pause ", msg ) )
	{
		LOG( "%p msg: pause", this );
		Resumed = false;
		HandleVrModeChanges();
		return;
	}

	if ( MatchesHead( "joy ", msg ) )
	{
		sscanf( msg, "joy %f %f %f %f",
				&InputEvents.JoySticks[0][0],
				&InputEvents.JoySticks[0][1],
				&InputEvents.JoySticks[1][0],
				&InputEvents.JoySticks[1][1] );
		return;
	}

	if ( MatchesHead( "touch ", msg ) )
	{
		sscanf( msg, "touch %i %f %f",
				&InputEvents.TouchAction,
				&InputEvents.TouchPosition[0],
				&InputEvents.TouchPosition[1] );
		return;
	}

	if ( MatchesHead( "key ", msg ) )
	{
		int keyCode, down, repeatCount;
		sscanf( msg, "key %i %i %i", &keyCode, &down, &repeatCount );
		if ( InputEvents.NumKeyEvents < MAX_INPUT_KEY_EVENTS )
		{
			//LOG( "Adding key event: keyCode = %i, down = %s, repeat = %i", keyCode, down ? "true" : "false", repeatCount );
			InputEvents.KeyEvents[InputEvents.NumKeyEvents].KeyCode = static_cast< ovrKeyCode >( keyCode & ~BUTTON_JOYPAD_FLAG );
			InputEvents.KeyEvents[InputEvents.NumKeyEvents].RepeatCount = repeatCount;
			InputEvents.KeyEvents[InputEvents.NumKeyEvents].Down = ( down != 0 );
			InputEvents.KeyEvents[InputEvents.NumKeyEvents].IsJoypadButton = ( keyCode & BUTTON_JOYPAD_FLAG ) != 0;
			InputEvents.NumKeyEvents++;
		}
		return;	
	}

	if ( MatchesHead( "intent ", msg ) )
	{
		LOG( "%p msg: intent", this );

		// define the buffer sizes with macros so we can ensure that the sscanf sizes are also updated
		// if the actual buffer sizes are changed.
#define FROM_SIZE 511
#define URI_SIZE 1023

		char fromPackageName[FROM_SIZE + 1];
		char uri[URI_SIZE + 1];
		// since the package name and URI cannot contain spaces, but JSON can,
		// the JSON string is at the end and will come after the third space.
		sscanf( msg, "intent %" STRINGIZE_VALUE( FROM_SIZE ) "s %" STRINGIZE_VALUE( URI_SIZE ) "s", fromPackageName, uri );
		char const * jsonStart = NULL;
		size_t msgLen = OVR_strlen( msg );
		int spaceCount = 0;
		for ( size_t i = 0; i < msgLen; ++i ) {
			if ( msg[i] == ' ' ) {
				spaceCount++;
				if ( spaceCount == 3 ) {
					jsonStart = &msg[i+1];
					break;
				}
			}
		}

		if ( OVR_strcmp( fromPackageName, EMPTY_INTENT_STR ) == 0 )
		{
			fromPackageName[0] = '\0';
		}
		if ( OVR_strcmp( uri, EMPTY_INTENT_STR ) == 0 )
		{
			uri[0] = '\0';
		}

		// Save off the intent.
		IntentFromPackage = fromPackageName;
		IntentJSON = jsonStart;
		IntentURI = uri;
		// This is only a new intent if the launch intent has already been handled.
		if ( IntentType == INTENT_OLD )
		{
			IntentType = INTENT_NEW;
		}

		return;
	}

	if ( MatchesHead( "quit ", msg ) )
	{
		// "quit" is called fron onDestroy and onPause should have been called already
		OVR_ASSERT( OvrMobile == NULL );
		ReadyToExit = true;
		LOG( "VrThreadSynced=%d ReadyToExit=%d", VrThreadSynced, ReadyToExit );
		return;
	}
}

void AppLocal::FrameworkInputProcessing( const VrInput & input )
{
	// Process key events.
	for ( int i = 0; i < input.NumKeyEvents; i++ )
	{
		const int keyCode = input.KeyEvents[i].KeyCode;
		const int repeatCount = input.KeyEvents[i].RepeatCount;
		const KeyEventType eventType = input.KeyEvents[i].EventType;

		// Give debug console a chance next.
		if ( DebugConsole::OnKeyEvent( keyCode, repeatCount, eventType ) )
		{
			continue;
		}
	}

	// Process button presses.
	bool const rightTrigger = ( input.buttonState & BUTTON_RIGHT_TRIGGER ) != 0;
	bool const leftTrigger = ( input.buttonState & BUTTON_LEFT_TRIGGER ) != 0;

	if ( leftTrigger && rightTrigger && ( input.buttonPressed & BUTTON_START ) != 0 )
	{
		time_t rawTime;
		time( &rawTime );
		struct tm * timeInfo = localtime( &rawTime );
		char timeStr[128];
		strftime( timeStr, sizeof( timeStr ), "%H:%M:%S", timeInfo );
		LOG_WITH_TAG( "QAEvent", "%s (%.3f) - QA event occurred", timeStr, vrapi_GetTimeInSeconds() );
	}
}

#if defined( OVR_OS_WIN32 )
static void GetInputEvents( ovrInputEvents & inputEvents )
{
}
#endif

/*
 * VrThreadFunction
 *
 * Continuously renders frames when active, checking for commands
 * from the main thread between frames.
 */
void * AppLocal::VrThreadFunction()
{
	// Set the name that will show up in systrace
	VrThread.SetThreadName( "OVR::VrThread" );

	// Initialize the VR thread
	{
		LOG( "AppLocal::VrThreadFunction - init" );

		// The Java VM needs to be attached on each thread that will use
		// it.  We need it to call UpdateTexture on surfaceTextures, which
		// must be done on the thread with the openGL context that created
		// the associated texture object current.
		ovr_AttachCurrentThread( Java.Vm, &Java.Env, NULL );

		// this must come after ovr_AttachCurrentThread so that Java is valid.
		FileSys = ovrFileSys::Create( *GetJava() );
		
		VrSettings.ModeParms.Java = Java;

#if defined( OVR_OS_ANDROID )
		VrSettings.PerformanceParms.MainThreadTid = gettid();
#else
		VrSettings.PerformanceParms.MainThreadTid = 0;
#endif
		VrSettings.PerformanceParms.RenderThreadTid = 0;

#if defined( OVR_OS_ANDROID )
		// Set up another thread for making longer-running java calls
		// to avoid hitches.
		Ttj.Init( *Java.Vm, *this );
#endif

		TheVrFrame.Init( &Java );

		InitInput();

		SuggestedEyeFovDegreesX = vrapi_GetSystemPropertyFloat( &Java, VRAPI_SYS_PROP_SUGGESTED_EYE_FOV_DEGREES_X );
		SuggestedEyeFovDegreesY = vrapi_GetSystemPropertyFloat( &Java, VRAPI_SYS_PROP_SUGGESTED_EYE_FOV_DEGREES_Y );

		// Init the adb 'console' and register console functions
		InitConsole( Java );
		RegisterConsoleFunction( "print", OVR::DebugPrint );
	}

	while( !( VrThreadSynced && ReadyToExit ) )
	{
		//SPAM( "FRAME START" );
		OVR_PERF_TIMER( VrThreadFunction_Loop );

		// Process incoming messages until the queue is empty.
		for ( ; ; )
		{
			const char * msg = MessageQueue.GetNextMessage();
			if ( msg == NULL )
			{
				break;
			}
			Command( msg );
			free( (void *)msg );
		}

		// Wait for messages until we are in VR mode.
		if ( OvrMobile == NULL )
		{
			// Don't wait if the exit conditions are satisfied.
			if ( !( VrThreadSynced && ReadyToExit ) )
			{
				MessageQueue.SleepUntilMessage();
			}
			continue;
		}

		// if there is an error condition, warp swap and nothing else
		if ( ErrorTextureSwapChain != NULL )
		{
			if ( vrapi_GetTimeInSeconds() >= ErrorMessageEndTime )
			{
				// Push black images to the screen to eliminate any frames of lost head tracking.
				ovrFrameParms blackFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_BLACK_FINAL, vrapi_GetTimeInSeconds(), NULL );
				blackFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
				vrapi_SubmitFrame( OvrMobile, &blackFrameParms );

#if defined( OVR_OS_ANDROID )
				vrapi_ReturnToHome( &Java );
#else
				// FIXME: What is the proper mechanism for returning to Home on PC?
				FAIL( "AppLocal::VrThreadFunction - Error Condition, exiting" );
#endif
			} 
			else 
			{
				// TODO: Should we update the FrameIndex while displaying the error message? On PC, not updating results
				// in the following error: ovrError_InvalidParameter: ovr_SubmitFrame called with unrecognized frameIndex
				ovrFrameParms warpSwapMessageParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_MESSAGE, vrapi_GetTimeInSeconds(), ErrorTextureSwapChain );
				warpSwapMessageParms.FrameIndex = TheVrFrame.Get().FrameNumber;
				warpSwapMessageParms.Layers[1].SpinSpeed = 0.0f;												// rotation in radians
				warpSwapMessageParms.Layers[1].SpinScale = 1024.0f / static_cast<float>( ErrorTextureSize );	// message size factor
				vrapi_SubmitFrame( OvrMobile, &warpSwapMessageParms );
			}
			continue;
		}

#if defined( OVR_OS_WIN32 )
		// NOTE: Currently we're only disabling input when the app does not
		// have focus. We need to review what pc lifecycle should look like.
		if ( GetSystemStatus( VRAPI_SYS_STATUS_HAS_FOCUS ) != VRAPI_FALSE )
		{
			// Handle PC specific controller input.
			GetInputEvents( InputEvents );
		}
#endif

		{
			OVR_PERF_TIMER( VrThreadFunction_Loop_AdvanceVrFrame );
			// Update ovrFrameInput.
			TheVrFrame.AdvanceVrFrame( InputEvents, OvrMobile, *GetJava(), VrSettings.HeadModelParms, EnteredVrModeFrame );
			InputEvents.NumKeyEvents = 0;
		}

		// Resend any debug lines that have expired.
		GetDebugLines().BeginFrame( TheVrFrame.Get().FrameNumber );

		// Process input.
		{
			OVR_PERF_TIMER( VrThreadFunction_Loop_FrameworkInputProcessing );
			FrameworkInputProcessing( TheVrFrame.Get().Input );
		}

		// Process Window Events.
		if ( GL_ProcessEvents( ExitCode ) )
		{
			break;
		}

		// Main loop logic and draw/update code common to both eyes.
		ovrFrameInput input = TheVrFrame.Get();

		// FIXME: move this to VrFrameBuilder::AdvanceVrFrame
		{
			const float fovIncrease = ( ( VrSettings.MinimumVsyncs > 1 ) || input.DeviceStatus.PowerLevelStateThrottled ) ? 10.0f : 0.0f;
			input.FovX = SuggestedEyeFovDegreesX + fovIncrease;
			input.FovY = SuggestedEyeFovDegreesY + fovIncrease;

			const Matrix4f projectionMatrix = ovrMatrix4f_CreateProjectionFov( input.FovX, input.FovY, 0.0f, 0.0f, VRAPI_ZNEAR, 0.0f );
			input.TexCoordsFromTanAngles = ovrMatrix4f_TanAngleMatrixFromProjection( (ovrMatrix4f*)( &projectionMatrix ) );

			const bool renderMonoMode = ( VrSettings.RenderMode & RENDERMODE_TYPE_MONO_MASK ) != 0;
			const ovrFrameTextureSwapChains eyes = EyeBuffers->GetCurrentFrameTextureSwapChains();
			for ( int eye = 0; eye < 2; eye++ )
			{
				input.ColorTextureSwapChain[eye] = eyes.ColorTextureSwapChain[renderMonoMode ? 0 : eye];
				input.DepthTextureSwapChain[eye] = eyes.DepthTextureSwapChain[renderMonoMode ? 0 : eye];
			}
			input.TextureSwapChainIndex = eyes.TextureSwapChainIndex;
		}

		ovrFrameResult res = appInterface->Frame( input );
		this->LastViewMatrix = res.FrameMatrices.CenterView;

		// Add any system-level debug surfaces to the FrameResult surface list.
		{
			GetDebugLines().AppendSurfaceList( res.Surfaces );
		}

		{
			OVR_PERF_TIMER( VrThreadFunction_Loop_DebugConsole_Frame );
			// Update debug console, trivial cost if console isn't up.
			DebugConsole::Frame( input, res );
		}

		// stop the loop perf timer now or it will show the wait on Timewarp in vrapi_SubmitFrame
		OVR_PERF_TIMER_STOP( VrThreadFunction_Loop );

		OVR_PERF_TIMER( VrThreadFunction_DrawEyeViews );

		// Draw the eye views.
		DrawEyeViews( res );


		//SPAM( "FRAME END" );
	}

	// Shutdown the VR thread
	{
		LOG( "AppLocal::VrThreadFunction - shutdown" );

#if !defined( OVR_OS_ANDROID )
		LeaveVrMode();
#endif

		// Shut down the message queue so it cannot overflow.
		MessageQueue.Shutdown();

		delete appInterface;
		appInterface = NULL;

		ovrTextureManager::Destroy( TextureManager );

		ShutdownGlObjects();

		ShutdownConsole( Java );

		ShutdownInput();

		ovrFileSys::Destroy( FileSys );

		// Detach from the Java VM before exiting.
		ovr_DetachCurrentThread( Java.Vm );
		VrSettings.ModeParms.Java.Env = NULL;
		Java.Env = NULL;

		LOG( "AppLocal::VrThreadFunction - exit" );
	}

	return &ExitCode;
}

// Shim to call a C++ object from an OVR::Thread::Start.
threadReturn_t AppLocal::ThreadStarter( Thread *, void * parm )
{
	threadReturn_t result = ((AppLocal *)parm)->VrThreadFunction();
	return result;
}

OvrDebugLines & AppLocal::GetDebugLines() 
{ 
    return *DebugLines; 
}

const OvrStoragePaths & AppLocal::GetStoragePaths()
{
	return *StoragePaths;
}

int AppLocal::GetSystemProperty( const ovrSystemProperty propType )
{
	return vrapi_GetSystemPropertyInt( &Java, propType );
}

int AppLocal::GetSystemStatus( const ovrSystemStatus statusType )
{
	return vrapi_GetSystemStatusInt( &Java, statusType );
}

const VrDeviceStatus & AppLocal::GetDeviceStatus() const
{
	return TheVrFrame.Get().DeviceStatus;
}

const ovrEyeBufferParms & AppLocal::GetEyeBufferParms() const
{
	return VrSettings.EyeBufferParms;
}

void AppLocal::SetEyeBufferParms( const ovrEyeBufferParms & parms )
{
	VrSettings.EyeBufferParms = parms;
}

const ovrHeadModelParms & AppLocal::GetHeadModelParms() const
{
	return VrSettings.HeadModelParms;
}

void AppLocal::SetHeadModelParms( const ovrHeadModelParms & parms ) 
{
	VrSettings.HeadModelParms = parms;
}

const ovrPerformanceParms & AppLocal::GetPerformanceParms() const
{
	return VrSettings.PerformanceParms;
}

int AppLocal::GetCpuLevel() const
{
	return VrSettings.PerformanceParms.CpuLevel;
}

void AppLocal::SetCpuLevel( const int cpuLevel )
{
	VrSettings.PerformanceParms.CpuLevel = cpuLevel;
}

int AppLocal::GetGpuLevel() const
{
	return VrSettings.PerformanceParms.GpuLevel;
}

void AppLocal::SetGpuLevel( const int gpuLevel )
{
	VrSettings.PerformanceParms.GpuLevel = gpuLevel;
}

int AppLocal::GetMinimumVsyncs() const
{
	return VrSettings.MinimumVsyncs;
}

void AppLocal::SetMinimumVsyncs( const int minimumVsyncs )
{
	VrSettings.MinimumVsyncs = minimumVsyncs;
}

bool AppLocal::GetFramebufferIsSrgb() const
{
	return FramebufferIsSrgb;
}

bool AppLocal::GetFramebufferIsProtected() const
{
	return FramebufferIsProtected;
}

Matrix4f const & AppLocal::GetLastViewMatrix() const
{
	return LastViewMatrix; 
}

void AppLocal::SetLastViewMatrix( Matrix4f const & m )
{
	LastViewMatrix = m; 
}

void AppLocal::RecenterLastViewMatrix()
{
	// Change LastViewMatrix to mirror what is done to the tracking orientation by vrapi_RecenterPose.
	// Get the current yaw rotation and cancel it out. This is necessary so that subsystems that
	// rely on LastViewMatrix do not end up using the orientation from before the recenter if they
	// are called before the beginning of the next frame.
	float yaw;
	float pitch;
	float roll;
	LastViewMatrix.ToEulerAngles< Axis_Y, Axis_X, Axis_Z, Rotate_CCW, Handed_R >( &yaw, &pitch, &roll );

	// undo the yaw
	Matrix4f unrotYawMatrix( Quatf( Axis_Y, -yaw ) );
	LastViewMatrix = LastViewMatrix * unrotYawMatrix;
}

const ovrJava * AppLocal::GetJava() const
{
	return &Java;
}

jclass & AppLocal::GetVrActivityClass()
{
	return VrActivityClass;
}

VrAppInterface * AppLocal::GetAppInterface() 
{
	return appInterface;
}

ovrMobile * AppLocal::GetOvrMobile()
{
	return OvrMobile;
}

const char * AppLocal::GetPackageName() const
{
	return PackageName.ToCStr();
}

bool AppLocal::GetInstalledPackagePath( char const * packageName, char * outPackagePath, size_t const outMaxSize ) const 
{
#if defined( OVR_OS_ANDROID )
	// Find the system activities apk so that we can load font data from it.
	jmethodID getInstalledPackagePathId = ovr_GetStaticMethodID( Java.Env, VrActivityClass, "getInstalledPackagePath", "(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;" );
	if ( getInstalledPackagePathId != NULL )
	{
		JavaString packageNameObj( Java.Env, packageName );
		JavaUTFChars utfPath( Java.
			Env, static_cast< jstring >( Java.Env->CallStaticObjectMethod( VrActivityClass, 
				getInstalledPackagePathId, Java.ActivityObject, packageNameObj.GetJString() ) ) );
		if ( !Java.Env->ExceptionOccurred() )
		{
			char const * pathStr = utfPath.ToStr();
			bool result = outMaxSize >= OVR_strlen( pathStr );	// return false if the buffer is too small
			OVR_sprintf( outPackagePath, outMaxSize, "%s", pathStr );
			return result;
		}
		WARN( "Exception occurred when calling getInstalledPackagePathId" );
		Java.Env->ExceptionClear();
	}
#endif
	return false;
}

void AppLocal::RecenterYaw( const bool showBlack )
{
	LOG( "AppLocal::RecenterYaw" );
	if ( showBlack )
	{
		ovrFrameParms blackFrameParms = vrapi_DefaultFrameParms( &Java, VRAPI_FRAME_INIT_BLACK_FLUSH, vrapi_GetTimeInSeconds(), NULL );
		blackFrameParms.FrameIndex = TheVrFrame.Get().FrameNumber;
		vrapi_SubmitFrame( OvrMobile, &blackFrameParms );
	}
	vrapi_RecenterPose( OvrMobile );

	RecenterLastViewMatrix();
}

void AppLocal::SetRecenterYawFrameStart( const long long frameNumber )
{
	//LOG( "SetRecenterYawFrameStart( %lld )", frameNumber );
	RecenterYawFrameStart = frameNumber;
}

long long AppLocal::GetRecenterYawFrameStart() const
{
	return RecenterYawFrameStart;
}

ovrFileSys & AppLocal::GetFileSys()
{
	return *FileSys;
}

ovrTextureManager * AppLocal::GetTextureManager()
{
	return TextureManager;
}

void AppLocal::RegisterConsoleFunction( char const * name, consoleFn_t function )
{
	OVR::RegisterConsoleFunction( name, function );
}

}	// namespace OVR
