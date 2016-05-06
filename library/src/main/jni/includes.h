/* 
 * Copyright 2016 eje inc.
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

/*
 * This file must be included from all .cpp files.
 * System header files and OVR SDK header files should be included in this file.
 */

/*
 * std
 */
#include <algorithm>
#include <limits>
#include <memory>
#include <map>
#include <string>
#include <vector>

/*
 * Android
 */
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <jni.h>

/*
 * LibOVR Kernel
 */
#include "Android/JniUtils.h"
#include "Kernel/OVR_Math.h"
#include "Kernel/OVR_Geometry.h"

/*
 * VR API
 */
#include "VrApi.h"
#include "VrApi_Helpers.h"
#include "VrApi_LocalPrefs.h"

/*
 * VR App Framework
 */
#include "App.h"
#include "AppLocal.h"
#include "EyeBuffers.h"
#include "Input.h"
#include "SurfaceTexture.h"

/*
 * VR App Support / VrGUI
 */
#include "GuiSys.h"
#include "GazeCursor.h"

/*
 * VR App Support / VrLocale
 */
#include "OVR_Locale.h"

/*
 * VR App Support / VrSound
 */
#include "SoundEffectContext.h"
