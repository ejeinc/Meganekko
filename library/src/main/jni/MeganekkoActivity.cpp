/*
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "includes.h"
#include "MeganekkoActivity.h"
#include "Scene.h"
#include "SceneObject.h"

namespace mgn
{
MeganekkoActivity::MeganekkoActivity() :
      GuiSys( OvrGuiSys::Create() ),
      Locale( nullptr ),
      HmdMounted(false)
{
}

MeganekkoActivity::~MeganekkoActivity()
{
    // Call MeganekkoActivity.oneTimeShutDown()
    app->GetJava()->Env->CallVoidMethod(app->GetJava()->ActivityObject, GetMethodID("oneTimeShutDown", "()V"));

    delete SoundEffectPlayer;
    delete SoundEffectContext;
    OvrGuiSys::Destroy( GuiSys );
}

void MeganekkoActivity::Configure(ovrSettings & settings)
{
    settings.RenderMode = RENDERMODE_MULTIVIEW;
}

void MeganekkoActivity::EnteredVrMode( const ovrIntentType intentType, const char * intentFromPackage, const char * intentJSON, const char * intentURI )
{

    // Init operation at first time
    if (intentType == INTENT_LAUNCH) {

        const ovrJava * java = app->GetJava();
        SoundEffectContext = new ovrSoundEffectContext( *java->Env, java->ActivityObject );
        SoundEffectContext->Initialize();
        SoundEffectPlayer = new OvrGuiSys::ovrDummySoundEffectPlayer();

        Locale = ovrLocale::Create( *java->Env, java->ActivityObject, "default" );

        String fontName;
        GetLocale().GetString( "@string/font_name", "efigs.fnt", fontName );
        GuiSys->Init( this->app, *SoundEffectPlayer, fontName.ToCStr(), &app->GetDebugLines() );

        // cache method IDs
        enteredVrModeMethodId   = GetMethodID("enteredVrMode", "()V");
        leavingVrModeMethodId   = GetMethodID("leavingVrMode", "()V");
        onHmdMountedMethodId    = GetMethodID("onHmdMounted", "()V");
        onHmdUnmountedMethodId  = GetMethodID("onHmdUnmounted", "()V");
        frameMethodId           = GetMethodID("frame", "(J)V");
        onKeyShortPressMethodId = GetMethodID("onKeyShortPress", "(II)Z");
        onKeyDoubleTapMethodId  = GetMethodID("onKeyDoubleTap", "(II)Z");
        onKeyLongPressMethodId  = GetMethodID("onKeyLongPress", "(II)Z");
        onKeyDownMethodId       = GetMethodID("onKeyDown", "(II)Z");
        onKeyUpMethodId         = GetMethodID("onKeyUp", "(II)Z");
        onKeyMaxMethodId        = GetMethodID("onKeyMax", "(II)Z");
        getNativeSceneMethodId  = GetMethodID("getNativeScene", "()J");

        // Call MeganekkoActivity.oneTimeInit()
        app->GetJava()->Env->CallVoidMethod(app->GetJava()->ActivityObject, GetMethodID("oneTimeInit", "()V"));
    }

    // Call MeganekkoActivity.enteredVrMode()
    app->GetJava()->Env->CallVoidMethod(app->GetJava()->ActivityObject, enteredVrModeMethodId);
}

void MeganekkoActivity::LeavingVrMode()
{
    // Call MeganekkoActivity.leavingVrMode()
    app->GetJava()->Env->CallVoidMethod(app->GetJava()->ActivityObject, leavingVrModeMethodId);
}

ovrFrameResult MeganekkoActivity::Frame( const ovrFrameInput & vrFrame )
{
    // process input events first because this mirrors the behavior when OnKeyEvent was
    // a virtual function on VrAppInterface and was called by VrAppFramework.
    for ( int i = 0; i < vrFrame.Input.NumKeyEvents; i++ )
    {
        const int keyCode = vrFrame.Input.KeyEvents[i].KeyCode;
        const int repeatCount = vrFrame.Input.KeyEvents[i].RepeatCount;
        const KeyEventType eventType = vrFrame.Input.KeyEvents[i].EventType;

        // Key event handling
        if ( OnKeyEvent( keyCode, repeatCount, eventType ) )
        {
            continue;   // consumed the event
        }
        // If nothing consumed the key and it's a short-press of the back key, then exit the application to OculusHome.
        if ( keyCode == OVR_KEY_BACK && eventType == KEY_EVENT_SHORT_PRESS )
        {
            app->StartSystemActivity( PUI_CONFIRM_QUIT );
            continue;
        }
    }

    Scene * scene = GetScene();
    JNIEnv * jni = app->GetJava()->Env;
    jni->CallVoidMethod(app->GetJava()->ActivityObject, frameMethodId, (jlong)(intptr_t)&vrFrame);

    const bool headsetIsMounted = vrFrame.DeviceStatus.HeadsetIsMounted;
    if (!HmdMounted && headsetIsMounted) {
        jni->CallVoidMethod(app->GetJava()->ActivityObject, onHmdMountedMethodId);
    } else if (HmdMounted && !headsetIsMounted) {
        jni->CallVoidMethod(app->GetJava()->ActivityObject, onHmdUnmountedMethodId);
    }
    HmdMounted = headsetIsMounted;

    // Apply Camera movement to centerViewMatrix
    ovrMatrix4f input = vrFrame.DeviceStatus.DeviceIsDocked
            ? Matrix4f::Translation(scene->GetViewPosition())
            : Matrix4f::Translation(scene->GetViewPosition()) * Matrix4f(internalSensorRotation);
    Matrix4f centerViewMatrix = vrapi_GetCenterEyeViewMatrix( &app->GetHeadModelParms(), &vrFrame.Tracking, &input );

    scene->SetCenterViewMatrix(centerViewMatrix);

    // Update GUI systems last, but before rendering anything.
    GuiSys->Frame( vrFrame, centerViewMatrix);

    ovrFrameResult res;
	res.ClearColorBuffer = scene->IsClearEnabled();
	res.ClearColor = scene->GetClearColor();
	scene->GetFrameMatrices(app->GetHeadModelParms(), vrFrame.FovX, vrFrame.FovY, res.FrameMatrices);
	scene->GenerateFrameSurfaceList(res.FrameMatrices, res.Surfaces);

    frameParms = vrapi_DefaultFrameParms(app->GetJava(), VRAPI_FRAME_INIT_DEFAULT, vrapi_GetTimeInSeconds(), NULL);

    ovrFrameLayer & layer = frameParms.Layers[VRAPI_FRAME_LAYER_TYPE_WORLD];
    layer.Flags |= VRAPI_FRAME_LAYER_FLAG_CHROMATIC_ABERRATION_CORRECTION;
    for ( int eye = 0; eye < VRAPI_FRAME_LAYER_EYE_MAX; eye++ ) {
        layer.Textures[eye].ColorTextureSwapChain = vrFrame.ColorTextureSwapChain[eye];
        layer.Textures[eye].DepthTextureSwapChain = vrFrame.DepthTextureSwapChain[eye];
        layer.Textures[eye].TextureSwapChainIndex = vrFrame.TextureSwapChainIndex;
        layer.Textures[eye].TexCoordsFromTanAngles = vrFrame.TexCoordsFromTanAngles;
        layer.Textures[eye].HeadPose = vrFrame.Tracking.HeadPose;
    }

    res.FrameParms = (ovrFrameParmsExtBase *) & frameParms;

    GuiSys->AppendSurfaceList( res.FrameMatrices.CenterView, &res.Surfaces );

    return res;
}

bool MeganekkoActivity::OnKeyEvent(const int keyCode, const int repeatCount, const KeyEventType eventType)
{
    bool handled = false;

    switch (eventType)
    {
    case KEY_EVENT_NONE:
        break;
    case KEY_EVENT_SHORT_PRESS:
        handled = CallKeyEventMethod(onKeyShortPressMethodId, keyCode, repeatCount);
        break;
    case KEY_EVENT_DOUBLE_TAP:
        handled = CallKeyEventMethod(onKeyDoubleTapMethodId, keyCode, repeatCount);
        break;
    case KEY_EVENT_LONG_PRESS:
        handled = CallKeyEventMethod(onKeyLongPressMethodId, keyCode, repeatCount);
        break;
    case KEY_EVENT_DOWN:
        handled = CallKeyEventMethod(onKeyDownMethodId, keyCode, repeatCount);
        break;
    case KEY_EVENT_UP:
        handled = CallKeyEventMethod(onKeyUpMethodId, keyCode, repeatCount);
        break;
    case KEY_EVENT_MAX:
        handled = CallKeyEventMethod(onKeyMaxMethodId, keyCode, repeatCount);
        break;
    }

    if (handled == false)
    {
        handled = GuiSys->OnKeyEvent(keyCode, repeatCount, eventType);
    }

    return handled;
}

}
