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
#include "Input.h"
#include <jni.h>

using namespace OVR;

namespace mgn {
extern "C" {

jdouble Java_org_meganekkovr_FrameInput_getPredictedDisplayTimeInSeconds(
    JNIEnv *jni, jclass clazz, jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->PredictedDisplayTimeInSeconds;
}

jfloat Java_org_meganekkovr_FrameInput_getDeltaSeconds(JNIEnv *jni,
                                                       jclass clazz,
                                                       jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->DeltaSeconds;
}

jint Java_org_meganekkovr_FrameInput_getFrameNumber(JNIEnv *jni, jclass clazz,
                                                    jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->FrameNumber;
}

jfloat Java_org_meganekkovr_FrameInput_getSwipeFraction(JNIEnv *jni,
                                                        jclass clazz,
                                                        jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.swipeFraction;
}

jint Java_org_meganekkovr_FrameInput_getButtonState(JNIEnv *jni, jclass clazz,
                                                    jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.buttonState;
}

jint Java_org_meganekkovr_FrameInput_getButtonPressed(JNIEnv *jni, jclass clazz,
                                                      jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.buttonPressed;
}

jint Java_org_meganekkovr_FrameInput_getButtonReleased(JNIEnv *jni,
                                                       jclass clazz,
                                                       jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.buttonReleased;
}
} // extern C
} // namespace mgn
