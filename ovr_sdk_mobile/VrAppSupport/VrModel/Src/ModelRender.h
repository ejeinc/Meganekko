/************************************************************************************

Filename    :   ModelRender.h
Content     :   Optimized OpenGL rendering path
Created     :   August 9, 2013
Authors     :   John Carmack

Copyright   :   Copyright 2014 Oculus VR, LLC. All Rights reserved.

************************************************************************************/
#ifndef OVR_ModelRender_h
#define OVR_ModelRender_h

#include "Kernel/OVR_Math.h"
#include "Kernel/OVR_Array.h"
#include "Kernel/OVR_String.h"
#include "Kernel/OVR_GlUtils.h"

#include "SurfaceRender.h"

namespace OVR
{

// This data is constant after model load, and can be referenced by
// multiple ModelState instances.
struct ModelDef
{
							ModelDef() {}

	OVR::Array<ovrSurfaceDef>	surfaces;
};

struct ModelState
{
						ModelState() : modelDef( NULL ) {}
						ModelState( const ModelDef & modelDef_ ) :
							modelDef( &modelDef_ ),
							DontRenderForClientUid( 0 ) { modelMatrix.Identity(); }

	const ModelDef *	modelDef;

	Matrix4f			modelMatrix;
	Array< Matrix4f >	Joints;

	long long			DontRenderForClientUid;	// skip rendering the model if the current scene's client uid matches this
};

// The model surfaces are culled and added to the sorted surface list.
// Application specific surfaces from the emit list are also added to the sorted surface list.
// The surface list is sorted such that opaque surfaces come first, sorted front-to-back,
// and transparent surfaces come last, sorted back-to-front.
void BuildModelSurfaceList(	Array<ovrDrawSurface> & surfaceList,
							const long long suppressModelsWithClientId,
							const Array<ModelState *> & emitModels,
							const Array<ovrDrawSurface> & emitSurfaces,
							const Matrix4f & viewMatrix,
							const Matrix4f & projectionMatrix );

} // namespace OVR

#endif	// OVR_ModelRender_h
