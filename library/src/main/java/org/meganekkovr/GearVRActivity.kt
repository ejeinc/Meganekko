package org.meganekkovr

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.oculus.vrappframework.VrActivity
import org.meganekkovr.ovrjni.OVRApp
import org.meganekkovr.util.ObjectFactory

open class GearVRActivity : VrActivity(), MeganekkoContext {

    lateinit var app: MeganekkoApp
    private lateinit var frame: FrameInput

    override val context: Context
        get() = this

    var clearColorBuffer: Boolean
        get() = getClearColorBuffer(appPtr)
        set(clearColorBuffer) = setClearColorBuffer(appPtr, clearColorBuffer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create app
        val app = createApp()

        if (app == null) {
            Log.e(TAG, "You have to declare <meta-data name=\"org.meganekkovr.App\" value\"YOUR_APP_CLASS_NAME\"/> or implement custom createApp() method.")
            finish()
            return
        }

        this.app = app

        val commandString = VrActivity.getCommandStringFromIntent(intent)
        val fromPackageNameString = VrActivity.getPackageStringFromIntent(intent)
        val uriString = VrActivity.getUriStringFromIntent(intent)

        // Create native GearVRActivity and get OVR::App pointer
        val appPtr = nativeSetAppInterface(this, fromPackageNameString, commandString, uriString)
        setAppPtr(appPtr)

        OVRApp.init(appPtr)
        LookDetector.init(appPtr)
        HeadTransform.init(appPtr)
    }

    /**
     * Create your [MeganekkoApp]'s instance.
     * App class can be specified with `<meta-data>` in AndroidManifest.xml. This is preferred way.
     * Or you can override this method to instantiate your app manually.
     *
     * @return
     */
    protected open fun createApp(): MeganekkoApp? {

        val appClassName = applicationInfo.metaData.getString("org.meganekkovr.App", "org.meganekkovr.MeganekkoApp")

        return try {
            ObjectFactory.newInstance(appClassName, this) as MeganekkoApp
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    /**
     * Called from native thread.
     */
    private fun init() {
        app.setMeganekkoContext(this)
        app.init()
    }

    /**
     * Called from native thread.
     */
    private fun onHmdMounted() {
        app.onHmdMounted()
    }

    /**
     * Called from native thread.
     */
    private fun onHmdUnmounted() {
        app.onHmdUnmounted()
    }

    /**
     * Called from native thread.
     */
    private fun enteredVrMode() {
        app.enteredVrMode()
    }

    /**
     * Called from native thread.
     */
    private fun leavingVrMode() {
        app.leavingVrMode()
    }

    override fun onResume() {
        super.onResume()
        app.onResume()
    }

    override fun onPause() {
        app.onPause()
        super.onPause()
    }

    /**
     * Called from native thread.
     *
     * @param frameInputPointer native `ovrFrameInput`'s pointer
     */
    private fun update(frameInputPointer: Long) {

        if (!::frame.isInitialized) {
            frame = FrameInput(frameInputPointer)
        }

        HeadTransform.instance.invalidate()

        app.update(frame)

        // Clean native resources
        NativeReference.gc()
    }

    /**
     * Called from native thread.
     *
     * @param surfacesPointer `&ovrFrameResult.Surfaces` value.
     */
    private fun collectSurfaceDefs(surfacesPointer: Long) {
        val scene = app.scene ?: return

        collectSurfaceDefs(scene, surfacesPointer)
    }

    /**
     * Called from native thread. Override this method to respond to key events.
     *
     * @param keyCode     One of [KeyCode] constant values.
     * @param repeatCount Repeat count.
     * @param eventType   One of [KeyEventType] constant values.
     * @return If event was consumed, return `true`. Otherwise `false`.
     */
    private fun onKeyEvent(keyCode: Int, repeatCount: Int, eventType: Int): Boolean {

        when (eventType) {
            KeyEventType.KEY_EVENT_NONE -> return false
            KeyEventType.KEY_EVENT_SHORT_PRESS -> return app.onKeyPressed(keyCode, repeatCount)
            KeyEventType.KEY_EVENT_DOUBLE_TAP -> return app.onKeyDoubleTapped(keyCode, repeatCount)
            KeyEventType.KEY_EVENT_LONG_PRESS -> return app.onKeyLongPressed(keyCode, repeatCount)
            KeyEventType.KEY_EVENT_DOWN -> return app.onKeyDown(keyCode, repeatCount)
            KeyEventType.KEY_EVENT_UP -> return app.onKeyUp(keyCode, repeatCount)
            KeyEventType.KEY_EVENT_MAX -> return app.onKeyMax(keyCode, repeatCount)
        }

        return false
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        setClearColor(appPtr, r, g, b, a)
    }

    fun getClearColor(clearColor: FloatArray) {
        if (clearColor.size != 4) {
            throw IllegalArgumentException("clearColor must be 4 element array.")
        }
        getClearColor(appPtr, clearColor)
    }

    /**
     * Prepare for rendering.
     *
     * @param surfacesPointer `&res.Surfaces`
     */
    private fun collectSurfaceDefs(entity: Entity, surfacesPointer: Long) {

        // Not visible
        if (!entity.isVisible) return

        // Check Entity has geometry and surface
        if (entity.isRenderable) {
            addSurfaceDef(entity.nativePointer, surfacesPointer)
        }

        // Recursive for all children
        entity.children.forEach { child -> collectSurfaceDefs(child, surfacesPointer) }
    }

    private external fun setClearColorBuffer(appPtr: Long, clearColorBuffer: Boolean)

    private external fun getClearColorBuffer(appPtr: Long): Boolean

    private external fun setClearColor(appPtr: Long, r: Float, g: Float, b: Float, a: Float)

    private external fun getClearColor(appPtr: Long, clearColor: FloatArray)

    private external fun addSurfaceDef(entityNativePtr: Long, surfacesPointer: Long)

    companion object {

        private val TAG = "GearVRActivity"

        /** Load jni .so on initialization  */
        init {
            Log.d(TAG, "LoadLibrary")
            System.loadLibrary("meganekko")
        }

        @JvmStatic
        private external fun nativeSetAppInterface(act: VrActivity, fromPackageNameString: String, commandString: String, uriString: String): Long
    }
}
