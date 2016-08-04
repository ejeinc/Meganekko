LOCAL_PATH := $(call my-dir)

#--------------------------------------------------------
# libvrgui.a
#
# VrGUI
#--------------------------------------------------------
include $(CLEAR_VARS)				# clean everything up to prepare for a module

LOCAL_MODULE    := vrgui			# generate libvrgui.a

LOCAL_SRC_FILES := ../../../Libs/Android/$(TARGET_ARCH_ABI)/libvrgui.a

LOCAL_EXPORT_C_INCLUDES := \
  $(LOCAL_PATH)/../../../../VrAppFramework/Include \
  $(LOCAL_PATH)/../../../../VrAppSupport/SystemUtils/Include \
  $(LOCAL_PATH)/../../../Src

LOCAL_STATIC_LIBRARIES += vrappframework systemutils

ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
include $(PREBUILT_STATIC_LIBRARY)
endif

# Note: Even though we depend on VrAppFramework, we don't explicitly import it
# since our dependent projects may want either a prebuilt or from-source version.

# Note: Even though we depend on SystemUtils, we don't explicitly import it
# since our dependent projects may want either a prebuilt or from-source version.