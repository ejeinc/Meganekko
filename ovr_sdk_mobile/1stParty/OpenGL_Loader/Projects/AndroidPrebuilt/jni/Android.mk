LOCAL_PATH := $(call my-dir)

#--------------------------------------------------------
# openglloader.a
#
# OpenGL Loader
#--------------------------------------------------------
include $(CLEAR_VARS)				# clean everything up to prepare for a module

LOCAL_MODULE    := openglloader		

LOCAL_SRC_FILES := ../../../Libs/Android/$(TARGET_ARCH_ABI)/libopenglloader.a

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../../Include

LOCAL_STATIC_LIBRARIES := openglloader

ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
  include $(PREBUILT_STATIC_LIBRARY)
endif
