#include "includes.h"

using namespace OVR;

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

jdouble Java_ovr_VrFrame_getPredictedDisplayTimeInSeconds(JNIEnv *jni,
                                                          jclass clazz,
                                                          jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->PredictedDisplayTimeInSeconds;
}

jfloat Java_ovr_VrFrame_getDeltaSeconds(JNIEnv *jni, jclass clazz,
                                        jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->DeltaSeconds;
}

jint Java_ovr_VrFrame_getFrameNumber(JNIEnv *jni, jclass clazz,
                                     jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->FrameNumber;
}

jfloat Java_ovr_VrFrame_getSwipeFraction(JNIEnv *jni, jclass clazz,
                                         jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.swipeFraction;
}

jint Java_ovr_VrFrame_getButtonState(JNIEnv *jni, jclass clazz,
                                     jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.buttonState;
}

jint Java_ovr_VrFrame_getButtonPressed(JNIEnv *jni, jclass clazz,
                                       jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.buttonPressed;
}

jint Java_ovr_VrFrame_getButtonReleased(JNIEnv *jni, jclass clazz,
                                        jlong vrFramePtr) {
  ovrFrameInput *vrFrame = reinterpret_cast<ovrFrameInput *>(vrFramePtr);
  return vrFrame->Input.buttonReleased;
}
#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
