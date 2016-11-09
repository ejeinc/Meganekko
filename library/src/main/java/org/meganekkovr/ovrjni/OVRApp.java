package org.meganekkovr.ovrjni;

public class OVRApp {

    //-----------------------------------------------------------------
    // System Activity Commands
    //-----------------------------------------------------------------
    public static final int SYSTEM_UI_GLOBAL_MENU = 0;
    public static final int SYSTEM_UI_CONFIRM_QUIT_MENU = 1;
    public static final int SYSTEM_UI_KEYBOARD_MENU = 2;
    public static final int SYSTEM_UI_FILE_DIALOG_MENU = 3;
    private static OVRApp instance; // singleton
    private final long appPtr;

    private OVRApp(long appPtr) {
        this.appPtr = appPtr;
    }

    public synchronized static void init(long appPtr) {
        if (instance != null)
            throw new IllegalStateException("init was called twice!");

        instance = new OVRApp(appPtr);
    }

    public static OVRApp getInstance() {
        return instance;
    }

    private static native void recenterYaw(long appPtr, boolean showBlack);

    private static native void showSystemUI(long appPtr, int type);

    private static native int getCpuLevel(long appPtr);

    private static native void setCpuLevel(long appPtr, int cpuLevel);

    private static native int getGpuLevel(long appPtr);

    private static native void setGpuLevel(long appPtr, int gpuLevel);

    private static native int getMinimumVsyncs(long appPtr);

    private static native void setMinimumVsyncs(long appPtr, int mininumVsyncs);

    public void recenterYaw(boolean showBlack) {
        recenterYaw(appPtr, showBlack);
    }

    public void showSystemUI(int type) {
        showSystemUI(appPtr, type);
    }

    public int getCpuLevel() {
        return getCpuLevel(appPtr);
    }

    public void setCpuLevel(int cpuLevel) {
        setCpuLevel(appPtr, cpuLevel);
    }

    public int getGpuLevel() {
        return getGpuLevel(appPtr);
    }

    public void setGpuLevel(int gpuLevel) {
        setGpuLevel(appPtr, gpuLevel);
    }

    public int getMinimumVsyncs() {
        return getMinimumVsyncs(appPtr);
    }

    public void setMinimumVsyncs(int mininumVsyncs) {
        setMinimumVsyncs(appPtr, mininumVsyncs);
    }
}
