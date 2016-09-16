/*
 * Copyright 2016 eje inc.
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
#ifndef CONVERT_H_
#define CONVERT_H_

/**
 * Java & native data convert methods.
 */

#include <jni.h>

namespace mgn {

/**
 * Fill Java float array with float container value from 0 index.
 * T value argument will be a float container such as Vector3f, Quatf or
 * Matrix4f.
 * This method does not check array length. Use carefully.
 */
template <typename T>
static inline void FillElementsUnSafe(JNIEnv *jni, jfloatArray array, T value) {
  jsize size = sizeof(T) / sizeof(jfloat);
  jni->SetFloatArrayRegion(array, 0, size, reinterpret_cast<jfloat *>(&value));
}

/**
 * Java float[] to Array<Vector2f>
 * ex. {0, 1, 2, 3, 4, 5, 6, 7} => [{0, 1}, {2, 3}, {4, 5}, {6, 7}]
 */
static inline OVR::Array<OVR::Vector2f>
floatArrayToVector2fArray(JNIEnv *jni, jfloatArray array) {

  OVR::Array<OVR::Vector2f> result;
  jsize size = jni->GetArrayLength(array);
  jfloat *elements = jni->GetFloatArrayElements(array, 0);

  for (int i = 0; i < size; i += 2) {
    float x = elements[i];
    float y = elements[i + 1];
    result.PushBack(OVR::Vector2f(x, y));
  }

  jni->ReleaseFloatArrayElements(array, elements, 0);

  return result;
}

/**
 * Java float[] to Array<Vector3f>
 * ex. {0, 1, 2, 3, 4, 5, 6, 7, 8} => [{0, 1, 2}, {3, 4, 5}, {6, 7, 8}]
 */
static inline OVR::Array<OVR::Vector3f>
floatArrayToVector3fArray(JNIEnv *jni, jfloatArray array) {

  OVR::Array<OVR::Vector3f> result;
  jsize size = jni->GetArrayLength(array);
  jfloat *elements = jni->GetFloatArrayElements(array, 0);

  for (int i = 0; i < size; i += 3) {
    float x = elements[i];
    float y = elements[i + 1];
    float z = elements[i + 2];
    result.PushBack(OVR::Vector3f(x, y, z));
  }

  jni->ReleaseFloatArrayElements(array, elements, 0);

  return result;
}

/**
 * Java float[] to Array<Vector4f>
 * ex. {0, 1, 2, 3, 4, 5, 6, 7} => [{0, 1, 2, 3}, {4, 5, 6, 7}]
 */
static inline OVR::Array<OVR::Vector4f>
floatArrayToVector4fArray(JNIEnv *jni, jfloatArray array) {

  OVR::Array<OVR::Vector4f> result;
  jsize size = jni->GetArrayLength(array);
  jfloat *elements = jni->GetFloatArrayElements(array, 0);

  for (int i = 0; i < size; i += 4) {
    float x = elements[i];
    float y = elements[i + 1];
    float z = elements[i + 2];
    float w = elements[i + 3];
    result.PushBack(OVR::Vector4f(x, y, z, w));
  }

  jni->ReleaseFloatArrayElements(array, elements, 0);

  return result;
}

/**
 * Java int[] to Array<TriangleIndex>
 */
static inline OVR::Array<OVR::TriangleIndex>
intArrayToTriangleIndex(JNIEnv *jni, jintArray array) {

  OVR::Array<OVR::TriangleIndex> result;
  jsize size = jni->GetArrayLength(array);
  jint *elements = jni->GetIntArrayElements(array, 0);

  result.Resize(size);

  for (int i = 0; i < size; ++i) {
    result[i] = elements[i];
  }

  jni->ReleaseIntArrayElements(array, elements, 0);

  return result;
}

/**
 * Java float[] to Matrix4f
 */
static inline OVR::Matrix4f floatArrayToMatrix4f(JNIEnv *jni,
                                                 jfloatArray array) {

  jfloat *elements = jni->GetFloatArrayElements(array, 0);

  Matrix4f result(                                          //
      elements[0], elements[4], elements[8], elements[12],  //
      elements[1], elements[5], elements[9], elements[13],  //
      elements[2], elements[6], elements[10], elements[14], //
      elements[3], elements[7], elements[11], elements[15]);

  jni->ReleaseFloatArrayElements(array, elements, 0);

  return result;
}
}
#endif