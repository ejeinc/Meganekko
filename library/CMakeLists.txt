# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
add_compile_options(-DANDROID_NDK)
add_compile_options(-Werror)
add_compile_options(-Wall)
add_compile_options(-Wextra)
add_compile_options(-Wno-strict-aliasing)
add_compile_options(-Wno-unused-parameter)
add_compile_options(-Wno-missing-field-initializers)
add_compile_options(-Wno-multichar)
add_compile_options(-Wno-type-limits)
add_compile_options(-Wno-invalid-offsetof)
add_compile_options(-std=c++11)
add_compile_options(-Wno-error=deprecated-declarations)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/LibOVRKernel/Src)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/minizip/src)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/OpenGL_Loader/Include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/stb/src)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/VrApi/Include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/VrAppFramework/Include)

add_library( vrapi SHARED IMPORTED )
set_property( TARGET vrapi PROPERTY IMPORTED_LOCATION
              ${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/libvrapi.so )

add_library( vrappframework STATIC IMPORTED )
set_property( TARGET vrappframework PROPERTY IMPORTED_LOCATION
              ${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/libvrappframework.a )

add_library( ovrkernel STATIC IMPORTED )
set_property( TARGET ovrkernel PROPERTY IMPORTED_LOCATION
              ${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/libovrkernel.a )

add_library( openglloader STATIC IMPORTED )
set_property( TARGET openglloader PROPERTY IMPORTED_LOCATION
              ${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/libopenglloader.a )

add_library( minizip STATIC IMPORTED )
set_property( TARGET minizip PROPERTY IMPORTED_LOCATION
              ${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/libminizip.a )

add_library( stb STATIC IMPORTED )
set_property( TARGET stb PROPERTY IMPORTED_LOCATION
              ${CMAKE_CURRENT_SOURCE_DIR}/ovr_sdk_mobile/libstb.a )

add_library( meganekko
             SHARED
             src/main/jni/Entity.cpp
             src/main/jni/FrameInput.cpp
             src/main/jni/GearVRActivity.cpp
             src/main/jni/GeometryComponent.cpp
             src/main/jni/HeadTransform.cpp
             src/main/jni/HybridObject.cpp
             src/main/jni/JavaSurface.cpp
             src/main/jni/JavaSurfaceTexture.cpp
             src/main/jni/LookDetector.cpp
             src/main/jni/NativeReference_jni.cpp
             src/main/jni/OVRApp.cpp
             src/main/jni/Shader.cpp
             src/main/jni/SurfaceRendererComponent.cpp )

target_link_libraries( meganekko
                       # OVR SDK Libraries
                       vrapi vrappframework ovrkernel openglloader stb minizip
                       # Android System Libraries
                       log android GLESv2 GLESv3 EGL OpenMAXAL OpenSLES z )