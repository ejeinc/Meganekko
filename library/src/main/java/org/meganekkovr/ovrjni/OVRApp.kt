package org.meganekkovr.ovrjni

class OVRApp private constructor(private val appPtr: Long) {

    fun recenterYaw(showBlack: Boolean) {
        recenterYaw(appPtr, showBlack)
    }

    fun showSystemUI(type: Int) {
        showSystemUI(appPtr, type)
    }

    fun setClockLevels(cpuLevel: Int, gpuLevel: Int) {
        setClockLevels(appPtr, cpuLevel, gpuLevel)
    }

    private external fun recenterYaw(appPtr: Long, showBlack: Boolean)

    private external fun showSystemUI(appPtr: Long, type: Int)

    private external fun setClockLevels(appPtr: Long, cpuLevel: Int, gpuLevel: Int)

    private external fun getMinimumVsyncs(appPtr: Long): Int

    private external fun setMinimumVsyncs(appPtr: Long, mininumVsyncs: Int)

    companion object {

        //-----------------------------------------------------------------
        // System Activity Commands
        //-----------------------------------------------------------------
        const val SYSTEM_UI_GLOBAL_MENU = 0
        const val SYSTEM_UI_CONFIRM_QUIT_MENU = 1
        const val SYSTEM_UI_KEYBOARD_MENU = 2
        const val SYSTEM_UI_FILE_DIALOG_MENU = 3

        @JvmStatic
        lateinit var instance: OVRApp

        @Synchronized
        @JvmStatic
        internal fun init(appPtr: Long) {
            instance = OVRApp(appPtr)
        }
    }
}
