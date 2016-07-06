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

#ifndef ACTIVITY_JNI_H
#define ACTIVITY_JNI_H

#include "Scene.h"
#include "OESShader.h"

using namespace OVR;

namespace mgn
{

class MeganekkoActivity : public VrAppInterface
{
public:
                        MeganekkoActivity();
                        ~MeganekkoActivity();

    virtual void        Configure( ovrSettings & settings );
    virtual void        EnteredVrMode( const ovrIntentType intentType, const char * intentFromPackage, const char * intentJSON, const char * intentURI );
    virtual void        LeavingVrMode();
    virtual Matrix4f    DrawEyeView( const int eye, const float fovDegreesX, const float fovDegreesY, ovrFrameParms & frameParms );
    virtual ovrFrameResult Frame( const ovrFrameInput & vrFrame );
    virtual bool        OnKeyEvent( const int keyCode, const int repeatCount, const KeyEventType eventType );

    ovrLocale &         GetLocale() { return *Locale; }

    OvrGuiSys *         GuiSys;

    Scene * GetScene() {
        return GetScene(app->GetJava()->Env);
    }

    Scene * GetScene(JNIEnv * jni) {
        return reinterpret_cast<Scene*>(jni->CallLongMethod(app->GetJava()->ActivityObject, getNativeSceneMethodId));
    }

    void SetInternalSensorRotation(const Quatf & q) {
        internalSensorRotation = q;
    }

private:
    ovrSoundEffectContext *        SoundEffectContext;
    OvrGuiSys::SoundEffectPlayer * SoundEffectPlayer;
    ovrLocale *                    Locale;

    Quatf internalSensorRotation;

    bool                HmdMounted; // true if the HMT was mounted on the previous frame

    jmethodID           enteredVrModeMethodId;
    jmethodID           leavingVrModeMethodId;
    jmethodID           onHmdMountedMethodId;
    jmethodID           onHmdUnmountedMethodId;
    jmethodID           frameMethodId;
    jmethodID           onKeyShortPressMethodId;
    jmethodID           onKeyDoubleTapMethodId;
    jmethodID           onKeyLongPressMethodId;
    jmethodID           onKeyDownMethodId;
    jmethodID           onKeyUpMethodId;
    jmethodID           onKeyMaxMethodId;
    jmethodID           getNativeSceneMethodId;

    inline jmethodID GetMethodID(const char * name, const char * signature)
    {
    	return ovr_GetMethodID(app->GetJava()->Env, app->GetAppInterface()->ActivityClass, name, signature);
    }

    inline jboolean CallKeyEventMethod(const jmethodID keyEventMethodId, const int keyCode, const int repeatCount) {
        return app->GetJava()->Env->CallBooleanMethod(app->GetJava()->ActivityObject,
                keyEventMethodId, keyCode, repeatCount);
    }
};

}
#endif
