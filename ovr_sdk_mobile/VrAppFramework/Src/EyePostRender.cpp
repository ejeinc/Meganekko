/************************************************************************************

Filename    :   EyePostRender.cpp
Content     :   Render on top of an eye render, portable between native and Unity.
Created     :   May 23, 2014
Authors     :   John Carmack

Copyright   :   Copyright 2014 Oculus VR, LLC. All Rights reserved.

*************************************************************************************/

#include "EyePostRender.h"

#include "Kernel/OVR_Alg.h"
#include "Kernel/OVR_Math.h"
#include "Kernel/OVR_Array.h"
#include "Kernel/OVR_GlUtils.h"
#include "Kernel/OVR_LogUtils.h"

namespace OVR
{

void EyePostRender::Init()
{
	LOG( "EyePostRender::Init()" );

	// grid of lines for drawing to eye buffer
	CalibrationLines = BuildCalibrationLines( 24, false );

	// ----TODO_DRAWEYEVIEW - use TransformVertex() once switched over to new path.
	UntexturedMvpProgram = BuildProgram(
		"uniform mat4 Mvpm;\n"
		"attribute vec4 Position;\n"
		"uniform mediump vec4 UniformColor;\n"
		"varying  lowp vec4 oColor;\n"
		"void main()\n"
		"{\n"
			"   gl_Position = Mvpm * Position;\n"
			"   oColor = UniformColor;\n"
		"}\n"
	,
		"varying lowp vec4	oColor;\n"
		"void main()\n"
		"{\n"
		"	gl_FragColor = oColor;\n"
		"}\n"
	);
}

void EyePostRender::Shutdown()
{
	LOG( "EyePostRender::Shutdown()" );
	CalibrationLines.Free();
	DeleteProgram( UntexturedMvpProgram );
}

void EyePostRender::DrawEyeCalibrationLines( const float bufferFovDegrees, const int eye )
{
	// ----TODO_DRAWEYEVIEW - add to framework surfaces list.

	// Optionally draw thick calibration lines into the texture,
	// which will be overlayed by the thinner pre-distorted lines
	// later -- they should match very closely!
	const Matrix4f projectionMatrix =
	//Matrix4f::Identity();
	 Matrix4f::PerspectiveRH( DegreeToRad( bufferFovDegrees ), 1.0f, 0.01f, 2000.0f );

	const GlProgram & prog = UntexturedMvpProgram;
	glUseProgram( prog.Program );
	glLineWidth( 3.0f );
	glUniform4f( prog.uColor, 0, static_cast<float>( 1 - eye ), static_cast<float>( eye ), 1.0f );
	// ----TODO_DRAWEYEVIEW - deprecating support for uMvp.
	glUniformMatrix4fv( prog.uMvp, 1, GL_TRUE, projectionMatrix.M[0] );

	glBindVertexArray( CalibrationLines.vertexArrayObject );
	glDrawElements( GL_LINES, CalibrationLines.indexCount, GL_UNSIGNED_SHORT, NULL );
	glBindVertexArray( 0 );
}

void EyePostRender::FillEdge( int fbWidth, int fbHeight )
{
	FillEdgeColor( fbWidth, fbHeight, 0.0f, 0.0f, 0.0f, 1.0f );
}

void EyePostRender::FillEdgeColor( int fbWidth, int fbHeight, float r, float g, float b, float a )
{
	// We need destination alpha to be solid 1 at the edges to prevent overlay
	// plane rendering from bleeding past the rendered view border, but we do
	// not want to fade to that, which would cause overlays to fade out differently
	// than scene geometry.

	// We could skip this for the cube map overlays when used for panoramic photo viewing
	// with no foreground geometry to get a little more fov effect, but if there
	// is a swipe view or anything else being rendered, the border needs to
	// be respected.

	// Note that this single pixel border won't be sufficient if mipmaps are made for
	// the eye buffers.

	// Probably should do this with GL_LINES instead of scissor changing.
	glClearColor( r, g, b, a );
	glEnable( GL_SCISSOR_TEST );

	glScissor( 0, 0, fbWidth, 1 );
	glClear( GL_COLOR_BUFFER_BIT );

	glScissor( 0, fbHeight-1, fbWidth, 1 );
	glClear( GL_COLOR_BUFFER_BIT );

	glScissor( 0, 0, 1, fbHeight );
	glClear( GL_COLOR_BUFFER_BIT );

	glScissor( fbWidth-1, 0, 1, fbHeight );
	glClear( GL_COLOR_BUFFER_BIT );

	glScissor( 0, 0, fbWidth, fbHeight );
	glDisable( GL_SCISSOR_TEST );
}

}	// namespace OVR
