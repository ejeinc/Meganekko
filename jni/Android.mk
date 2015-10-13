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

LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE    := assimp
LOCAL_SRC_FILES := ../libs/libassimp.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

ifndef OVR_MOBILE_SDK
	OVR_MOBILE_SDK=./ovr_mobile_sdk
endif

include $(OVR_MOBILE_SDK)/cflags.mk

LOCAL_MODULE := meganekko

FILE_LIST := $(wildcard $(LOCAL_PATH)/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)

LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/VrAppFramework/Include
LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/LibOVRKernel/Include
LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/LibOVRKernel/Src
LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/VrApi/Include
LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/VrAppSupport/VrGUI/Src
LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/VrAppSupport/VrLocale/Src
LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/VrAppSupport/VrModel/Src
LOCAL_C_INCLUDES += $(OVR_MOBILE_SDK)/VrAppSupport/VrSound/Include

LOCAL_C_INCLUDES += $(LOCAL_PATH)/contrib/assimp
LOCAL_C_INCLUDES +=	$(LOCAL_PATH)/contrib/assimp/include
LOCAL_C_INCLUDES +=	$(LOCAL_PATH)/contrib/assimp/include/Compiler

LOCAL_C_INCLUDES +=	$(LOCAL_PATH)/contrib/libpng
FILE_LIST := $(wildcard $(LOCAL_PATH)/contrib/libpng/*.c)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/contrib/libpng/*.s)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/contrib

FILE_LIST := $(wildcard $(LOCAL_PATH)/engine/importer/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/engine/picker/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/engine/renderer/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/engine/memory/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/gl/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/objects/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/objects/components/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/objects/textures/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/oculus/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/shaders/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/shaders/material/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)
FILE_LIST := $(wildcard $(LOCAL_PATH)/util/*.cpp)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)

LOCAL_SHARED_LIBRARIES += assimp
LOCAL_SHARED_LIBRARIES += vrapi
LOCAL_STATIC_LIBRARIES += libvrmodel
LOCAL_STATIC_LIBRARIES += vrsound vrlocale vrgui vrappframework libovrkernel

LOCAL_ARM_NEON := true

LOCAL_CPPFLAGS += -fexceptions -std=c++11 -D__GXX_EXPERIMENTAL_CXX0X__
LOCAL_CFLAGS := -Wattributes

LOCAL_LDLIBS += -ljnigraphics -llog -lGLESv3 -lEGL -lz -landroid

include $(BUILD_SHARED_LIBRARY)

$(call import-module,LibOVRKernel/Projects/AndroidPrebuilt/jni)
$(call import-module,VrApi/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppFramework/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppSupport/VrGui/Projects/Android/jni)
$(call import-module,VrAppSupport/VrLocale/Projects/Android/jni)
$(call import-module,VrAppSupport/VrSound/Projects/Android/jni)
