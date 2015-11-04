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
LOCAL_SRC_FILES := ../../../libs/libassimp.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

include $(OVR_SDK_MOBILE)/cflags.mk

LOCAL_MODULE := meganekko

# jni/** all .cpp .c .s files
FILE_LIST := $(wildcard $(LOCAL_PATH)/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/**/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/**/**/*.cpp)
FILE_LIST += $(wildcard $(LOCAL_PATH)/contrib/libpng/*.c)
FILE_LIST += $(wildcard $(LOCAL_PATH)/contrib/libpng/*.s)
LOCAL_SRC_FILES += $(FILE_LIST:$(LOCAL_PATH)/%=%)

# Include directories
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrAppFramework/Include
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/LibOVRKernel/Include
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/LibOVRKernel/Src
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrApi/Include
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrAppSupport/VrGUI/Src
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrAppSupport/VrLocale/Src
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrAppSupport/VrModel/Src
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrAppSupport/VrSound/Include
LOCAL_C_INCLUDES += $(OVR_SDK_MOBILE)/VrAppSupport/SystemUtils/Include

LOCAL_C_INCLUDES += $(LOCAL_PATH)/contrib/assimp
LOCAL_C_INCLUDES +=	$(LOCAL_PATH)/contrib/assimp/include
LOCAL_C_INCLUDES +=	$(LOCAL_PATH)/contrib/assimp/include/Compiler
LOCAL_C_INCLUDES +=	$(LOCAL_PATH)/contrib/libpng
LOCAL_C_INCLUDES += $(LOCAL_PATH)/contrib

LOCAL_SHARED_LIBRARIES += assimp
LOCAL_SHARED_LIBRARIES += vrapi
LOCAL_STATIC_LIBRARIES += systemutils vrsound vrlocale vrgui vrappframework libovrkernel

LOCAL_ARM_NEON := true

LOCAL_CPPFLAGS += -fexceptions -std=c++11 -D__GXX_EXPERIMENTAL_CXX0X__
LOCAL_CFLAGS := -Wattributes

LOCAL_LDLIBS += -ljnigraphics -llog -lGLESv3 -lEGL -lz -landroid

include $(BUILD_SHARED_LIBRARY)

$(call import-module,LibOVRKernel/Projects/AndroidPrebuilt/jni)
$(call import-module,VrApi/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppFramework/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppSupport/VrGui/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppSupport/VrLocale/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppSupport/VrSound/Projects/AndroidPrebuilt/jni)
$(call import-module,VrAppSupport/SystemUtils/Projects/AndroidPrebuilt/jni)
