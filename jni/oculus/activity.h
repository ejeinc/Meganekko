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

#ifndef ACTIVITY_JNI_H
#define ACTIVITY_JNI_H

#include <OVR.h>
#include "App.h"
#include "AppLocal.h"
#include "GuiSys.h"
#include "GuiSysLocal.h"
#include "OVR_Locale.h"
#include "SoundEffectContext.h"
#include "Input.h"
#include "Android/JniUtils.h"
#include "SoundEffectContext.h"
#include "objects/scene.h"
#include "shaders/shader_manager.h"

using namespace OVR;

namespace mgn
{

class MeganekkoActivity : public VrAppInterface
{
public:
                        MeganekkoActivity();
                        ~MeganekkoActivity();

    virtual void        Configure( ovrSettings & settings );
    virtual void        OneTimeInit( const char * fromPackage, const char * launchIntentJSON, const char * launchIntentURI );
    virtual void        OneTimeShutdown();
    virtual Matrix4f    DrawEyeView( const int eye, const float fovDegrees, ovrFrameParms & frameParms );
    virtual Matrix4f    Frame( const VrFrame & vrFrame );
    virtual bool        OnKeyEvent( const int keyCode, const int repeatCount, const KeyEventType eventType );

    ovrLocale &         GetLocale() { return *Locale; }

    OvrGuiSys *         GuiSys;
    bool                deviceIsDocked;
    int                 MinimumVsyncs;

    Scene*              scene;
    ShaderManager*      shaderManager;

private:
    std::unique_ptr<ovrSoundEffectContext> SoundEffectContext;
    std::unique_ptr<OvrGuiSys::SoundEffectPlayer> SoundEffectPlayer;
    ovrLocale *         Locale;

    ovrMatrix4f         centerViewMatrix;

    jmethodID           frameMethodId;
    jmethodID           onKeyShortPressMethodId;
    jmethodID           onKeyDoubleTapMethodId;
    jmethodID           onKeyLongPressMethodId;
    jmethodID           onKeyDownMethodId;
    jmethodID           onKeyUpMethodId;
    jmethodID           onKeyMaxMethodId;
    jmethodID           onSwipeUpMethodId;
    jmethodID           onSwipeDownMethodId;
    jmethodID           onSwipeForwardMethodId;
    jmethodID           onSwipeBackMethodId;
    jmethodID           onTouchSingleMethodId;
    jmethodID           onTouchDoubleMethodId;

    inline jmethodID GetMethodID(const char * name, const char * signature)
    {
    	return ovr_GetMethodID(app->GetVrJni(), app->GetAppInterface()->ActivityClass, name, signature);
    }

    inline jboolean CallKeyEventMethod(const jmethodID keyEventMethodId, const int keyCode) {
        return app->GetVrJni()->CallBooleanMethod(app->GetJavaObject(),
                keyEventMethodId, keyCode);
    }
};

}
#endif
