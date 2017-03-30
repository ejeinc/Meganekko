# Common build settings for all VR apps
ifeq ($(OVR_DEBUG),1)
  BUILDTYPE := Debug
else
 BUILDTYPE := Release
endif

# This needs to be defined to get the right header directories for egl / etc
APP_PLATFORM := android-19

ifeq ($(OVR_TEST_ARM64),1)
	# 32+64 bit support... experimental!
	APP_ABI := armeabi-v7a,arm64-v8a
else
	# 32-bit only mode
	APP_ABI := armeabi-v7a
endif

# Statically link the GNU STL. This may not be safe for multi-so libraries but
# we don't know of any problems yet.
APP_STL := gnustl_static

# Make sure every shared lib includes a .note.gnu.build-id header, for crash reporting
APP_LDFLAGS := -Wl,--build-id

NDK_TOOLCHAIN_VERSION := clang

# Define the directories for $(import-module, ...) to look in
ROOT_DIR := $(patsubst %/,%,$(dir $(lastword $(MAKEFILE_LIST))))
NDK_MODULE_PATH := $(ROOT_DIR)
