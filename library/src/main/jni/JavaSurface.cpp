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
#include "JavaSurface.h"
#include "Kernel/OVR_LogUtils.h"

namespace mgn {

JavaSurface::JavaSurface(JNIEnv *jni, jobject javaSurfaceTexture) : jni(jni) {

  // Reference: VrAppFramework/Include/SurfaceTexture.h
  // Get Surface class
  static const char *className = "android/view/Surface";
  const jclass surfaceClass = jni->FindClass(className);
  if (surfaceClass == nullptr) {
    FAIL("FindClass( %s ) failed", className);
  }

  // Get constructor
  const jmethodID constructor = jni->GetMethodID(
      surfaceClass, "<init>", "(Landroid/graphics/SurfaceTexture;)V");
  if (constructor == nullptr) {
    FAIL("GetMethodID( <init> ) failed");
  }

  // Create instance
  jobject obj = jni->NewObject(surfaceClass, constructor, javaSurfaceTexture);
  if (obj == nullptr) {
    FAIL("NewObject() failed");
  }

  // Store to field
  javaObject = jni->NewGlobalRef(obj);
  if (javaObject == nullptr) {
    FAIL("NewGlobalRef() failed");
  }

  // Now that we have a globalRef, we can free the localRef
  jni->DeleteLocalRef(obj);
}

JavaSurface::~JavaSurface() {
  LOG("Delete JavaSurface");

  if (javaObject) {

    // Call Surface.release()
    static const char *className = "android/view/Surface";
    const jclass surfaceClass = jni->FindClass(className);
    if (surfaceClass == nullptr) {
      FAIL("FindClass( %s ) failed", className);
    }

    const jmethodID releaseMethodID =
        jni->GetMethodID(surfaceClass, "release", "()V");
    if (releaseMethodID == nullptr) {
      FAIL("GetMethodID( release ) failed");
    }

    jni->CallVoidMethod(javaObject, releaseMethodID);

    // Delete reference for garbage collection
    jni->DeleteGlobalRef(javaObject);
    javaObject = nullptr;
  }
}

jobject JavaSurface::GetJavaObject() { return javaObject; }
}