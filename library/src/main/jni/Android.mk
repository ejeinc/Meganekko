 # Copyright 2015 eje inc.
 # Copyright 2015 Samsung Electronics Co., LTD
 #
 # Licensed under the Apache License, Version 2.0 (the "License");
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at
 #
 #     http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 #

OVR_SDK_MOBILE := ../ovr_sdk_mobile

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
include $(OVR_SDK_MOBILE)/cflags.mk

LOCAL_MODULE := meganekko

# jni/** all .cpp
FILE_LIST := $(wildcard $(LOCAL_PATH)/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)

# Include directories
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrAppFramework/Include
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/LibOVRKernel/Src
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrApi/Include

LOCAL_SHARED_LIBRARIES := vrapi
LOCAL_STATIC_LIBRARIES := vrappframework 

include $(BUILD_SHARED_LIBRARY)

$(call import-module,LibOVRKernel/Projects/AndroidPrebuilt/jni)
$(call import-module,VrApi/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppFramework/Projects/AndroidPrebuilt/jni)
