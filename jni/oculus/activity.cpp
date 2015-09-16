/* Copyright 2015 Samsung Electronics Co., LTD
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

#include "activity.h"

#include <VrApi.h>
#include <VrApi_Helpers.h>

#include "objects/scene.h"
#include "objects/scene_object.h"
#include "Input.h"

namespace gvr
{

GVRActivity::GVRActivity() :
      GuiSys( OvrGuiSys::Create() ),
      Locale( NULL )
{
    centerViewMatrix = ovrMatrix4f_CreateIdentity();
    deviceIsDocked = false;
}

GVRActivity::~GVRActivity()
{
    OvrGuiSys::Destroy( GuiSys );
}

void GVRActivity::Configure(ovrSettings & settings)
{
}

void GVRActivity::OneTimeInit(const char * fromPackage, const char * launchIntentJSON, const char * launchIntentURI)
{
    auto java = app->GetJava();
    SoundEffectContext.reset( new ovrSoundEffectContext( *java->Env, java->ActivityObject ) );
    SoundEffectContext->Initialize();
    SoundEffectPlayer.reset( new OvrGuiSys::ovrDummySoundEffectPlayer() );

    Locale = ovrLocale::Create( *app, "default" );

    String fontName;
    GetLocale().GetString( "@string/font_name", "efigs.fnt", fontName );
    GuiSys->Init( this->app, *SoundEffectPlayer, fontName.ToCStr(), &app->GetDebugLines() );

    jmethodID oneTimeInitMethodId = GetMethodID("oneTimeInit", "()V");
    app->GetVrJni()->CallVoidMethod(app->GetJavaObject(), oneTimeInitMethodId);

    // cache method IDs
    frameMethodId = GetMethodID("frame", "(J)V");
    onKeyShortPressMethodId = GetMethodID("onKeyShortPress", "(II)Z");
    onKeyDoubleTapMethodId = GetMethodID("onKeyDoubleTap", "(II)Z");
    onKeyLongPressMethodId = GetMethodID("onKeyLongPress", "(II)Z");
    onKeyDownMethodId = GetMethodID("onKeyDown", "(II)Z");
    onKeyUpMethodId = GetMethodID("onKeyUp", "(II)Z");
    onKeyMaxMethodId = GetMethodID("onKeyMax", "(II)Z");
    onSwipeUpMethodId = GetMethodID("onSwipeUp", "()V");
    onSwipeDownMethodId = GetMethodID("onSwipeDown", "()V");
    onSwipeForwardMethodId = GetMethodID("onSwipeForward", "()V");
    onSwipeBackMethodId = GetMethodID("onSwipeBack", "()V");
    onTouchSingleMethodId = GetMethodID("onTouchSingle", "()V");
    onTouchDoubleMethodId = GetMethodID("onTouchDouble", "()V");
}

void GVRActivity::OneTimeShutdown()
{
    jmethodID oneTimeShutdownMethodId = GetMethodID("oneTimeShutDown", "()V");
    app->GetVrJni()->CallVoidMethod(app->GetJavaObject(), oneTimeShutdownMethodId);
}

Matrix4f GVRActivity::DrawEyeView(const int eye, const float fovDegrees, ovrFrameParms & frameParms)
{
    frameParms.MinimumVsyncs = MinimumVsyncs;

    const Matrix4f eyeViewMatrix = vrapi_GetEyeViewMatrix( &app->GetHeadModelParms(), &centerViewMatrix, eye );
    const Matrix4f eyeProjectionMatrix = Matrix4f::PerspectiveRH( DegreeToRad( fovDegrees ), 1.0f, 0.01f, 2000.0f );
    const Matrix4f eyeViewProjection = eyeProjectionMatrix * eyeViewMatrix;

    context->RenderEyeView(eyeViewMatrix, eyeProjectionMatrix, eyeViewProjection, eye);

    GuiSys->RenderEyeView( centerViewMatrix, eyeViewProjection );

    return eyeViewProjection;

}

Matrix4f GVRActivity::Frame( const VrFrame & vrFrame )
{
    // Update Camera orientation
    Camera * camera = const_cast<Camera *>(context->scene->main_camera());
    JNIEnv * jni = app->GetVrJni();

    if (deviceIsDocked)
    {
        camera->transform()->set_rotation(vrFrame.Tracking.HeadPose.Pose.Orientation);
    }

    // Input handling
    int buttonPressed = vrFrame.Input.buttonPressed;

    if (buttonPressed & OVR::JoyButton::BUTTON_SWIPE_UP) {
        jni->CallVoidMethod(app->GetJavaObject(), onSwipeUpMethodId);
    } else if (buttonPressed & OVR::JoyButton::BUTTON_SWIPE_DOWN) {
        jni->CallVoidMethod(app->GetJavaObject(), onSwipeDownMethodId);
    } else if (buttonPressed & OVR::JoyButton::BUTTON_SWIPE_FORWARD) {
        jni->CallVoidMethod(app->GetJavaObject(), onSwipeForwardMethodId);
    } else if (buttonPressed & OVR::JoyButton::BUTTON_SWIPE_BACK) {
        jni->CallVoidMethod(app->GetJavaObject(), onSwipeBackMethodId);
    } else if (buttonPressed & OVR::JoyButton::BUTTON_TOUCH_SINGLE) {
        jni->CallVoidMethod(app->GetJavaObject(), onTouchSingleMethodId);
    } else if (buttonPressed & OVR::JoyButton::BUTTON_TOUCH_DOUBLE) {
        jni->CallVoidMethod(app->GetJavaObject(), onTouchDoubleMethodId);
    }

    jni->CallVoidMethod(app->GetJavaObject(), frameMethodId, (jlong)(intptr_t)&vrFrame);

    // Apply Camera movement to centerViewMatrix
    ovrMatrix4f input = deviceIsDocked
            ? Matrix4f::Translation(camera->transform()->position())
            : camera->transform()->getModelMatrix();
    centerViewMatrix = vrapi_GetCenterEyeViewMatrix( &app->GetHeadModelParms(), &vrFrame.Tracking, &input );

    // Update GUI systems last, but before rendering anything.
    GuiSys->Frame( vrFrame, centerViewMatrix);

    gl_delete.processQueues();

    return centerViewMatrix;
}

bool GVRActivity::OnKeyEvent(const int keyCode, const int repeatCount, const KeyEventType eventType)
{
    bool handled = false;

    switch (eventType)
    {
    case KEY_EVENT_NONE:
        break;
    case KEY_EVENT_SHORT_PRESS:
        handled = CallKeyEventMethod(onKeyShortPressMethodId, keyCode);
        break;
    case KEY_EVENT_DOUBLE_TAP:
        handled = CallKeyEventMethod(onKeyDoubleTapMethodId, keyCode);
        break;
    case KEY_EVENT_LONG_PRESS:
        handled = CallKeyEventMethod(onKeyLongPressMethodId, keyCode);
        break;
    case KEY_EVENT_DOWN:
        handled = CallKeyEventMethod(onKeyDownMethodId, keyCode);
        break;
    case KEY_EVENT_UP:
        handled = CallKeyEventMethod(onKeyUpMethodId, keyCode);
        break;
    case KEY_EVENT_MAX:
        handled = CallKeyEventMethod(onKeyMaxMethodId, keyCode);
        break;
    }

    if (handled == false)
    {
        handled = GuiSys->OnKeyEvent(keyCode, repeatCount, eventType);
    }

    // if not handled back key long press, show global menu
    if (handled == false && keyCode == 4 && eventType == KEY_EVENT_LONG_PRESS)
    {
        app->StartSystemActivity(PUI_GLOBAL_MENU);
    }

    return handled;
}

}
