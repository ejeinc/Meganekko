/*
 * Copyright 2017 eje inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "JavaSurfaceTexture.h"
#include "Kernel/OVR_LogUtils.h"

namespace mgn {

JavaSurfaceTexture::JavaSurfaceTexture(JNIEnv *jni, int texName) : jni(jni) {

  // Reference: VrAppFramework/Src/SurfaceTexture.cpp
  static const char *className = "android/graphics/SurfaceTexture";
  const jclass surfaceTextureClass = jni->FindClass(className);
  if (surfaceTextureClass == 0) {
    FAIL("FindClass( %s ) failed", className);
  }

  // find the constructor that takes an int
  const jmethodID constructor =
      jni->GetMethodID(surfaceTextureClass, "<init>", "(I)V");
  if (constructor == 0) {
    FAIL("GetMethodID( <init> ) failed");
  }

  jobject obj = jni->NewObject(surfaceTextureClass, constructor, texName);
  if (obj == 0) {
    FAIL("NewObject() failed");
  }

  javaObject = jni->NewGlobalRef(obj);
  if (javaObject == 0) {
    FAIL("NewGlobalRef() failed");
  }

  // Now that we have a globalRef, we can free the localRef
  jni->DeleteLocalRef(obj);

  // jclass objects are localRefs that need to be freed
  jni->DeleteLocalRef(surfaceTextureClass);
}

JavaSurfaceTexture::~JavaSurfaceTexture() {
  LOG("Delete JavaSurfaceTexture");

  if (javaObject) {

    // Call SurfaceTexture.release()
    static const char *className = "android/graphics/SurfaceTexture";
    const jclass surfaceTextureClass = jni->FindClass(className);
    if (surfaceTextureClass == nullptr) {
      FAIL("FindClass( %s ) failed", className);
    }

    const jmethodID releaseMethodID =
        jni->GetMethodID(surfaceTextureClass, "release", "()V");
    if (releaseMethodID == nullptr) {
      FAIL("GetMethodID( release ) failed");
    }

    jni->CallVoidMethod(javaObject, releaseMethodID);

    // Delete reference for garbage collection
    jni->DeleteGlobalRef(javaObject);
    javaObject = nullptr;
  }
}

jobject JavaSurfaceTexture::GetJavaObject() { return javaObject; }
}