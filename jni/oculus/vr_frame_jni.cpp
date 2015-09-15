#include <jni.h>
#include <VrApi.h>
#include <OVR.h>
#include <Input.h>

using namespace OVR;

namespace gvr
{

extern "C"
{

jdouble Java_com_eje_1c_meganekko_VrFrame_getPredictedDisplayTimeInSeconds(JNIEnv * jni, jclass clazz, jlong vrFramePtr)
{
    VrFrame * vrFrame = reinterpret_cast<VrFrame*>(vrFramePtr);
    return vrFrame->PredictedDisplayTimeInSeconds;
}

jfloat Java_com_eje_1c_meganekko_VrFrame_getDeltaSeconds(JNIEnv * jni, jclass clazz, jlong vrFramePtr)
{
    VrFrame * vrFrame = reinterpret_cast<VrFrame*>(vrFramePtr);
    return vrFrame->DeltaSeconds;
}

jint Java_com_eje_1c_meganekko_VrFrame_getFrameNumber(JNIEnv * jni, jclass clazz, jlong vrFramePtr)
{
    VrFrame * vrFrame = reinterpret_cast<VrFrame*>(vrFramePtr);
    return vrFrame->FrameNumber;
}

jfloat Java_com_eje_1c_meganekko_VrFrame_getSwipeFraction(JNIEnv * jni, jclass clazz, jlong vrFramePtr)
{
    VrFrame * vrFrame = reinterpret_cast<VrFrame*>(vrFramePtr);
    return vrFrame->Input.swipeFraction;
}

jint Java_com_eje_1c_meganekko_VrFrame_getButtonState(JNIEnv * jni, jclass clazz, jlong vrFramePtr)
{
    VrFrame * vrFrame = reinterpret_cast<VrFrame*>(vrFramePtr);
    return vrFrame->Input.buttonState;
}

jint Java_com_eje_1c_meganekko_VrFrame_getButtonPressed(JNIEnv * jni, jclass clazz, jlong vrFramePtr)
{
    VrFrame * vrFrame = reinterpret_cast<VrFrame*>(vrFramePtr);
    return vrFrame->Input.buttonPressed;
}

jint Java_com_eje_1c_meganekko_VrFrame_getButtonReleased(JNIEnv * jni, jclass clazz, jlong vrFramePtr)
{
    VrFrame * vrFrame = reinterpret_cast<VrFrame*>(vrFramePtr);
    return vrFrame->Input.buttonReleased;
}

} // extern "C"

} // namespace gvr
