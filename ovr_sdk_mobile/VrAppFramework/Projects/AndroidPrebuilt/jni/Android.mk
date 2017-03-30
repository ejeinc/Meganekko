LOCAL_PATH := $(call my-dir)

#--------------------------------------------------------
# libvrappframework.a
#
# VrAppFramework
#--------------------------------------------------------
include $(CLEAR_VARS)

LOCAL_MODULE := vrappframework

LOCAL_SRC_FILES := ../../../Libs/Android/$(TARGET_ARCH_ABI)/$(BUILDTYPE)/libvrappframework.a

LOCAL_EXPORT_C_INCLUDES := \
  $(LOCAL_PATH)/../../../../LibOVRKernel/Src \
  $(LOCAL_PATH)/../../../../VrApi/Include \
  $(LOCAL_PATH)/../../../Include

# GL platform interface
LOCAL_EXPORT_LDLIBS += -lEGL
# native multimedia
LOCAL_EXPORT_LDLIBS += -lOpenMAXAL 
# logging
LOCAL_EXPORT_LDLIBS += -llog
# native windows
LOCAL_EXPORT_LDLIBS += -landroid
# For minizip
LOCAL_EXPORT_LDLIBS += -lz
# audio
LOCAL_EXPORT_LDLIBS += -lOpenSLES

LOCAL_STATIC_LIBRARIES += libovrkernel minizip stb openglloader

# NOTE: This check is added to prevent the following error when running a "make clean" where
# the prebuilt lib may have been deleted: "LOCAL_SRC_FILES points to a missing file"
ifneq (,$(wildcard $(LOCAL_PATH)/$(LOCAL_SRC_FILES)))
include $(PREBUILT_STATIC_LIBRARY)
endif

$(call import-module,3rdParty/minizip/build/androidprebuilt/jni)
$(call import-module,3rdParty/stb/build/androidprebuilt/jni)
$(call import-module,1stParty/OpenGL_Loader/Projects/AndroidPrebuilt/jni)

# Note: Even though we depend on LibOVRKernel, we don't explicitly import it since our
# dependents may want either a prebuilt or from-source LibOVRKernel.

# Note: Even though we depend on VrApi, we don't explicitly import it since our
# dependents may want either a prebuilt or from-source VrApi.
