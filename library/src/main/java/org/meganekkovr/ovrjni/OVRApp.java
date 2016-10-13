package org.meganekkovr.ovrjni;

public class OVRApp {

    //-----------------------------------------------------------------
    // System Activity Commands
    //-----------------------------------------------------------------
    public static final String PUI_GLOBAL_MENU = "globalMenu";
    public static final String PUI_GLOBAL_MENU_TUTORIAL = "globalMenuTutorial";
    public static final String PUI_CONFIRM_QUIT = "confirmQuit";
    public static final String PUI_THROTTLED1 = "throttled1";      // Warn that Power Save Mode has been activated
    public static final String PUI_THROTTLED2 = "throttled2";      // Warn that Minimum Mode has been activated
    public static final String PUI_HMT_UNMOUNT = "HMT_unmount";    // the HMT has been taken off the head
    public static final String PUI_HMT_MOUNT = "HMT_mount";        // the HMT has been placed on the head
    public static final String PUI_WARNING = "warning";            // the HMT has been placed on the head and a warning message shows
    public static final String PUI_FAIL_MENU = "failMenu";         // display a FAIL() message in the System Activities
    public static final String PUI_KEYBOARD_MENU = "keyboardMenu"; // bring up a keyboard to edit a single string, send string back to calling app when done
    public static final String PUI_FILE_DIALOG = "fileDialog";     // bring up a folder browser to select the path to a file or folder.
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

    private static native void startSystemActivity(long appPtr, String command);

    private static native int getCpuLevel(long appPtr);

    private static native void setCpuLevel(long appPtr, int cpuLevel);

    private static native int getGpuLevel(long appPtr);

    private static native void setGpuLevel(long appPtr, int gpuLevel);

    private static native int getMinimumVsyncs(long appPtr);

    private static native void setMinimumVsyncs(long appPtr, int mininumVsyncs);

    private static native void setShowFPS(long appPtr, boolean show);

    private static native boolean getShowFPS(long appPtr);

    private static native void showInfoText(long appPtr, float duration, String text);

    public void recenterYaw(boolean showBlack) {
        recenterYaw(appPtr, showBlack);
    }

    public void startSystemActivity(String command) {
        startSystemActivity(appPtr, command);
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

    public boolean getShowFPS() {
        return getShowFPS(appPtr);
    }

    public void setShowFPS(boolean show) {
        setShowFPS(appPtr, show);
    }

    public void showInfoText(float duration, String text) {
        showInfoText(appPtr, duration, text);
    }
}
