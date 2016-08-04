LOCAL_PATH := $(call my-dir)

#--------------------------------------------------------
# libovrkernel.a
#
# LibOVRKernel
#--------------------------------------------------------
include $(CLEAR_VARS)

LOCAL_MODULE := libovrkernel

LOCAL_SRC_FILES := ../../../Libs/Android/$(TARGET_ARCH_ABI)/libovrkernel.a

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../../Include \
					$(LOCAL_PATH)/../../../Src

LOCAL_STATIC_LIBRARIES := openglloader

ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
  include $(PREBUILT_STATIC_LIBRARY)
endif
$(call import-module,1stParty/OpenGL_Loader/Projects/AndroidPrebuilt/jni)
