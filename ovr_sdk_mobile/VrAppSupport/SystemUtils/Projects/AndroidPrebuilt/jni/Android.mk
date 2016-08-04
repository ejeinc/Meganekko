LOCAL_PATH := $(call my-dir)

#--------------------------------------------------------
# libsystemutils.a
#
# SystemUtils
#--------------------------------------------------------
include $(CLEAR_VARS)

LOCAL_MODULE := systemutils

LOCAL_SRC_FILES := ../../../Libs/Android/$(TARGET_ARCH_ABI)/libsystemutils.a

LOCAL_EXPORT_C_INCLUDES := \
  $(LOCAL_PATH)/../../../../LibOVRKernel/Src \
  $(LOCAL_PATH)/../../../../VrApi/Include \
  $(LOCAL_PATH)/../../../Include

LOCAL_STATIC_LIBRARIES += libovrkernel

ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
include $(PREBUILT_STATIC_LIBRARY)
endif

$(call import-module,LibOVRKernel/Projects/AndroidPrebuilt/jni)