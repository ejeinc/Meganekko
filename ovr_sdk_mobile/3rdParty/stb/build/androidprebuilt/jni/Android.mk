LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := stb

LOCAL_SRC_FILES := ../../../lib/android/$(TARGET_ARCH_ABI)/lib$(LOCAL_MODULE).a

LOCAL_EXPORT_C_INCLUDES :=  $(LOCAL_PATH)/../../../src

# NOTE: This check is added to prevent the following error when running a "make clean" where
# the prebuilt lib may have been deleted: "LOCAL_SRC_FILES points to a missing file"
ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
  include $(PREBUILT_STATIC_LIBRARY)
endif
