LOCAL_PATH := $(call my-dir)

#--------------------------------------------------------
# libvrmodel.a
#
# VrModel
#--------------------------------------------------------
include $(CLEAR_VARS)				# clean everything up to prepare for a module

LOCAL_MODULE    := vrmodel			# generate libvrmodel.a

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../../Src

LOCAL_SRC_FILES := ../../../Libs/Android/$(TARGET_ARCH_ABI)/libvrmodel.a

ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
include $(PREBUILT_STATIC_LIBRARY)
endif