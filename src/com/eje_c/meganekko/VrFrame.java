package com.eje_c.meganekko;

/**
 * Native VrFrame wrapper.
 */
public class VrFrame {

    private long nativePtr;

    VrFrame(long nativePtr) {
        this.nativePtr = nativePtr;
    }

    void setNativePtr(long nativePtr) {
        this.nativePtr = nativePtr;
    }

    public double getPredictedDisplayTimeInSeconds() {
        return getPredictedDisplayTimeInSeconds(nativePtr);
    }

    public float getDeltaSeconds() {
        return getDeltaSeconds(nativePtr);
    }

    public int getFrameNumber() {
        return getFrameNumber(nativePtr);
    }

    public float getSwipeFraction() {
        return getSwipeFraction(nativePtr);
    }

    public int getButtonState() {
        return getButtonState(nativePtr);
    }

    public int getButtonPressed() {
        return getButtonPressed(nativePtr);
    }

    public int getButtonReleased() {
        return getButtonReleased(nativePtr);
    }

    private static native double getPredictedDisplayTimeInSeconds(long vrFramePtr);

    private static native float getDeltaSeconds(long vrFramePtr);

    private static native int getFrameNumber(long vrFramePtr);

    private static native float getSwipeFraction(long vrFramePtr);

    private static native int getButtonState(long vrFramePtr);

    private static native int getButtonPressed(long vrFramePtr);

    private static native int getButtonReleased(long vrFramePtr);
}
