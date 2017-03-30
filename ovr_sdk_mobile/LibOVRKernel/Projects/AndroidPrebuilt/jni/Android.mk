LOCAL_PATH := $(call my-dir)

#--------------------------------------------------------
# libovrkernel.a
#
# LibOVRKernel
#--------------------------------------------------------
include $(CLEAR_VARS)

LOCAL_MODULE := libovrkernel

LOCAL_SRC_FILES := ../../../Libs/Android/$(TARGET_ARCH_ABI)/$(BUILDTYPE)/libovrkernel.a

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../../Include \
					$(LOCAL_PATH)/../../../Src

# NOTE: This check is added to prevent the following error when running a "make clean" where
# the prebuilt lib may have been deleted: "LOCAL_SRC_FILES points to a missing file"
ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
  include $(PREBUILT_STATIC_LIBRARY)
endif
