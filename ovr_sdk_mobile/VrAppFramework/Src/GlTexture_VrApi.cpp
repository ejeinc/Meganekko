/************************************************************************************

Filename    :   GlTexture_VrApi.cpp
Content     :   OpenGL texture loading.
Created     :   September 30, 2013
Authors     :   John Carmack

Copyright   :   Copyright 2014 Oculus VR, LLC. All Rights reserved.

*************************************************************************************/

#include "GlTexture.h"
#include "VrApi.h"

namespace OVR {

// These are just wrappers over the vrapi swap chain calls, and should probably be removed.

// They need to be in a seperate source file from the other GlTexture so using just the
// standard textures doesn't pull in a dependency on vrapi.
ovrTextureSwapChain * CreateTextureSwapChain( ovrTextureType type, ovrTextureFormat format, int width, int height, int levels, bool buffered )
{
	return vrapi_CreateTextureSwapChain( type, format, width, height, levels, buffered );
}

void DestroyTextureSwapChain( ovrTextureSwapChain * chain )
{
	vrapi_DestroyTextureSwapChain( chain );
}

int GetTextureSwapChainLength( ovrTextureSwapChain * chain )
{
	return vrapi_GetTextureSwapChainLength( chain );
}

unsigned int GetTextureSwapChainHandle( ovrTextureSwapChain * chain, int index )
{
	return vrapi_GetTextureSwapChainHandle( chain, index );
}

}	// namespace OVR
