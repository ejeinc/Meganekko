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
}
#endif