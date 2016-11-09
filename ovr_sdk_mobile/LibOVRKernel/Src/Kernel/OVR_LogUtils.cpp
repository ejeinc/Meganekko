/************************************************************************************

Filename    :   Log.cpp
Content     :   Macros and helpers for Android logging.
Created     :   4/15/2014
Authors     :   Jonathan E. Wright

Copyright   :   Copyright 2014 Oculus VR, LLC. All Rights reserved.

*************************************************************************************/

#include "OVR_LogUtils.h"

#if defined( ANDROID )
#include <unistd.h>			// for gettid()
#include <sys/syscall.h>	// for syscall()
#endif

#include <stdarg.h>
#include <string.h>
#include <assert.h>

#include "OVR_GlUtils.h"

// GPU Timer queries cause instability on current
// Adreno drivers. Disable by default, but allow
// enabling via the local prefs file.
// TODO: Test this on new Qualcomm driver under Lollipop.
static int AllowGpuTimerQueries = 0;	// 0 = off, 1 = glBeginQuery/glEndQuery, 2 = glQueryCounter

void SetAllowGpuTimerQueries( int enable )
{
	LOG( "SetAllowGpuTimerQueries( %d )", enable );
	AllowGpuTimerQueries = enable;
}

void SetThreadAffinityMask( int tid, int mask )
{
#if defined( OVR_OS_ANDROID )
	int syscallres = syscall( __NR_sched_setaffinity, tid, sizeof( mask ), &mask );
	if ( syscallres != 0 )
	{
		int err = errno;
		WARN( "Error in the syscall setaffinity: mask=0x%x err=0x%x", mask, err );
	}
#endif
}

int GetThreadAffinityMask( int tid )
{
#if defined( OVR_OS_ANDROID )
	if ( tid != 0 )
	{
		int mask;
		for ( size_t len = sizeof( mask ); len != 0; len >>= 1 )
		{
			int syscallres = syscall( __NR_sched_getaffinity, tid, len, &mask );
			if ( syscallres >= 0 )
			{
				return mask;
			}
			if ( errno == ENOSYS )
			{
				break;
			}
		}
	}
#endif
	return 0;
}

// Log with an explicit tag
void LogWithTag( const int prio, const char * tag, const char * fmt, ... )
{
#if defined( OVR_OS_ANDROID )
	va_list ap;
	va_start( ap, fmt );
	__android_log_vprint( prio, tag, fmt, ap );
	va_end( ap );
#elif defined( OVR_OS_WIN32 )
	va_list args;
	va_start( args, fmt );

	char buffer[4096];
	vsnprintf_s( buffer, 4096, _TRUNCATE, fmt, args );
	va_end( args );

	OutputDebugString( buffer );
#else
#error "Not implemented"
#endif
}

void LogWithFileTag( const int prio, const char * fileTag, const char * fmt, ... )
{
#if defined( OVR_OS_ANDROID )
	va_list ap, ap2;

	// fileTag will be something like "jni/App.cpp", which we
	// want to strip down to just "App"
	char strippedTag[128];

	// scan backwards from the end to the first slash
	const int len = strlen( fileTag );
	int	slash;
	for ( slash = len - 1; slash > 0 && fileTag[slash] != '/'; slash-- )
	{
	}
	if ( fileTag[slash] == '/' )
	{
		slash++;
	}
	// copy forward until a dot or 0
	size_t i;
	for ( i = 0; i < sizeof( strippedTag ) - 1; i++ )
	{
		const char c = fileTag[slash+i];
		if ( c == '.' || c == 0 )
		{
			break;
		}
		strippedTag[i] = c;
	}
	strippedTag[i] = 0;

	va_start( ap, fmt );

	// Calculate the length of the log message... if its too long __android_log_vprint() will clip it!
	va_copy( ap2, ap );
	const int loglen = vsnprintf( NULL, 0, fmt, ap2 );
	va_end( ap2 );

	if ( loglen < 512 )
	{
		// For short messages just use android's default formatting path (which has a fixed size buffer on the stack).
		__android_log_vprint( prio, strippedTag, fmt, ap );
	}
	else
	{
		// For long messages allocate off the heap to avoid blowing the stack...
		char *formattedMsg = ( char * )malloc( loglen + 1 );
		vsnprintf( formattedMsg, ( size_t ) ( loglen + 1 ), fmt, ap2 );
		__android_log_write( prio, strippedTag, formattedMsg );
		free( formattedMsg );
	}

	va_end( ap );
#elif defined( OVR_OS_WIN32 )
	va_list args;
	va_start( args, fmt );

	char buffer[4096];
	vsnprintf_s( buffer, 4096, _TRUNCATE, fmt, args );
	va_end( args );

	OutputDebugString( buffer );
	OutputDebugString( "\n" );
#else
#error "Not implemented"
#endif
}

template< int NumTimers, int NumFrames >
LogGpuTime<NumTimers,NumFrames>::LogGpuTime() :
	UseTimerQuery( false ),
	UseQueryCounter( false ),
	TimerQuery(),
	BeginTimestamp(),
	DisjointOccurred(),
	TimeResultIndex(),
	TimeResultMilliseconds(),
	LastIndex( -1 )
{
}

template< int NumTimers, int NumFrames >
LogGpuTime<NumTimers,NumFrames>::~LogGpuTime()
{
#if defined( OVR_OS_ANDROID )
	for ( int i = 0; i < NumTimers; i++ )
	{
		if ( TimerQuery[i] != 0 )
		{
			glDeleteQueriesEXT_( 1, &TimerQuery[i] );
		}
	}
#endif
}

template< int NumTimers, int NumFrames >
bool LogGpuTime<NumTimers,NumFrames>::IsEnabled()
{
#if defined( OVR_OS_ANDROID )
	return UseTimerQuery && extensionsOpenGL.EXT_disjoint_timer_query;
#else
	return false;
#endif
}

template< int NumTimers, int NumFrames >
void LogGpuTime<NumTimers,NumFrames>::Begin( int index )
{
	// don't enable by default on Mali because this issues a glFinish() to work around a driver bug
	UseTimerQuery = ( AllowGpuTimerQueries != 0 );
	// use glQueryCounterEXT on Mali to time GPU rendering to a non-default FBO
	UseQueryCounter = ( AllowGpuTimerQueries == 2 );

#if defined( OVR_OS_ANDROID )
	if ( UseTimerQuery && extensionsOpenGL.EXT_disjoint_timer_query )
	{
		assert( index >= 0 && index < NumTimers );
		assert( LastIndex == -1 );
		LastIndex = index;

		if ( TimerQuery[index] )
		{
			for ( GLint available = 0; available == 0; )
			{
				glGetQueryObjectivEXT_( TimerQuery[index], GL_QUERY_RESULT_AVAILABLE, &available );
			}

			glGetIntegerv( GL_GPU_DISJOINT_EXT, &DisjointOccurred[index] );

			GLuint64 elapsedGpuTime = 0;
			glGetQueryObjectui64vEXT_( TimerQuery[index], GL_QUERY_RESULT_EXT, &elapsedGpuTime );

			TimeResultMilliseconds[index][TimeResultIndex[index]] = ( elapsedGpuTime - (GLuint64)BeginTimestamp[index] ) * 0.000001;
			TimeResultIndex[index] = ( TimeResultIndex[index] + 1 ) % NumFrames;
		}
		else
		{
			glGenQueriesEXT_( 1, &TimerQuery[index] );
		}
		if ( !UseQueryCounter )
		{
			BeginTimestamp[index] = 0;
			glBeginQueryEXT_( GL_TIME_ELAPSED_EXT, TimerQuery[index] );
		}
		else
		{
			glGetInteger64v_( GL_TIMESTAMP_EXT, &BeginTimestamp[index] );
		}
	}
#endif
}

template< int NumTimers, int NumFrames >
void LogGpuTime<NumTimers,NumFrames>::End( int index )
{
#if defined( OVR_OS_ANDROID )
	if ( UseTimerQuery && extensionsOpenGL.EXT_disjoint_timer_query )
	{
		assert( index == LastIndex );
		LastIndex = -1;

		if ( !UseQueryCounter )
		{
			glEndQueryEXT_( GL_TIME_ELAPSED_EXT );
		}
		else
		{
			glQueryCounterEXT_( TimerQuery[index], GL_TIMESTAMP_EXT );
			// Mali workaround: check for availability once to make sure all the pending flushes are resolved
			GLint available = 0;
			glGetQueryObjectivEXT_( TimerQuery[index], GL_QUERY_RESULT_AVAILABLE, &available );
			// Mali workaround: need glFinish() when timing rendering to non-default FBO
			//glFinish();
		}
	}
#endif
}

template< int NumTimers, int NumFrames >
void LogGpuTime<NumTimers,NumFrames>::PrintTime( int index, const char * label ) const
{
#if defined( OVR_OS_ANDROID )
	if ( UseTimerQuery && extensionsOpenGL.EXT_disjoint_timer_query )
	{
//		double averageTime = 0.0;
//		for ( int i = 0; i < NumFrames; i++ )
//		{
//			averageTime += TimeResultMilliseconds[index][i];
//		}
//		averageTime *= ( 1.0 / NumFrames );
//		LOG( "%s %i: %3.1f %s", label, index, averageTime, DisjointOccurred[index] ? "DISJOINT" : "" );
	}
#endif
}

template< int NumTimers, int NumFrames >
double LogGpuTime<NumTimers,NumFrames>::GetTime( int index ) const
{
	double averageTime = 0;
	for ( int i = 0; i < NumFrames; i++ )
	{
		averageTime += TimeResultMilliseconds[index][i];
	}
	averageTime *= ( 1.0 / NumFrames );
	return averageTime;
}

template< int NumTimers, int NumFrames >
double LogGpuTime<NumTimers,NumFrames>::GetTotalTime() const
{
	double totalTime = 0;
	for ( int j = 0; j < NumTimers; j++ )
	{
		for ( int i = 0; i < NumFrames; i++ )
		{
			totalTime += TimeResultMilliseconds[j][i];
		}
	}
	totalTime *= ( 1.0 / NumFrames );
	return totalTime;
}

template class LogGpuTime<2,10>;
template class LogGpuTime<8,10>;
