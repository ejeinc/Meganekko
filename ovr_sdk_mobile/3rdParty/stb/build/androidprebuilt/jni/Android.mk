LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := stb

LOCAL_SRC_FILES := ../../../lib/android/$(TARGET_ARCH_ABI)/lib$(LOCAL_MODULE).a

LOCAL_EXPORT_C_INCLUDES :=  $(LOCAL_PATH)/../../../src

ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
  include $(PREBUILT_STATIC_LIBRARY)
endif
