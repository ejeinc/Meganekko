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

    private static native void setClockLevels(long appPtr, int cpuLevel, int gpuLevel);

    private static native int getMinimumVsyncs(long appPtr);

    private static native void setMinimumVsyncs(long appPtr, int mininumVsyncs);

    public void recenterYaw(boolean showBlack) {
        recenterYaw(appPtr, showBlack);
    }

    public void showSystemUI(int type) {
        showSystemUI(appPtr, type);
    }

    public void setClockLevels(int cpuLevel, int gpuLevel) {
        setClockLevels(appPtr, cpuLevel, gpuLevel);
    }
}
