package org.meganekkovr

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import org.meganekkovr.audio_engine.AudioEngine
import org.meganekkovr.xml.XmlParser
import org.meganekkovr.xml.XmlParserException
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

open class MeganekkoApp {

    private val commands = LinkedBlockingQueue<() -> Unit>()
    var scene: Scene? = null
        set(scene) {
            assertGlThread()

            val currentScene = this.scene
            currentScene?.onStopRendering()

            field = scene

            if (scene != null) {
                scene.app = this
                scene.onStartRendering()
            }
        }

    private lateinit var meganekkoContext: MeganekkoContext
    private var glThreadId: Long = 0
    private var xmlParser: XmlParser? = null
    private var audioEngine: AudioEngine? = null

    /**
     * Called at app is launching. Override this to implement custom initialization.
     * If you override this method, you must call `super.init()`.
     */
    open fun init() {
        glThreadId = Thread.currentThread().id
    }

    /**
     * Called at every frame update. It will be called about 60 times per frame.
     * If you override this method, you must call `super.update(frame)`.
     *
     * @param frame Frame information
     */
    open fun update(frame: FrameInput) {

        audioEngine?.update(frame)

        // runOnGlThread handling
        while (!commands.isEmpty()) {
            commands.poll()()
        }

        this.scene?.update(frame)
    }

    /**
     * For internal use only.
     *
     * @param context
     */
    internal fun setMeganekkoContext(context: MeganekkoContext) {
        this.meganekkoContext = context
    }

    /**
     * Get [Context].
     *
     * @return context
     */
    val context: Context
        get() {
            return meganekkoContext.context
        }

    /**
     * Enqueue command that must run in GL thread. This command will be executed at next update.
     *
     * @param command Command
     */
    fun runOnGlThread(command: () -> Unit) {
        commands.add(command)
    }

    fun runOnUiThread(command: () -> Unit) {
        meganekkoContext.runOnUiThread(Runnable { command() })
    }

    private fun assertGlThread() {
        if (Thread.currentThread().id != glThreadId) {
            throw IllegalStateException("This operation must be in GL Thread")
        }
    }

    open fun onKeyPressed(keyCode: Int, repeatCount: Int): Boolean {
        return this.scene?.onKeyPressed(keyCode, repeatCount) ?: false
    }

    open fun onKeyDoubleTapped(keyCode: Int, repeatCount: Int): Boolean {
        return this.scene?.onKeyDoubleTapped(keyCode, repeatCount) ?: false
    }

    open fun onKeyLongPressed(keyCode: Int, repeatCount: Int): Boolean {
        return this.scene?.onKeyLongPressed(keyCode, repeatCount) ?: false
    }

    open fun onKeyDown(keyCode: Int, repeatCount: Int): Boolean {
        return this.scene?.onKeyDown(keyCode, repeatCount) ?: false
    }

    open fun onKeyUp(keyCode: Int, repeatCount: Int): Boolean {
        return this.scene?.onKeyUp(keyCode, repeatCount) ?: false
    }

    open fun onKeyMax(keyCode: Int, repeatCount: Int): Boolean {
        return this.scene?.onKeyMax(keyCode, repeatCount) ?: false
    }

    /**
     * Get an [XmlParser].
     *
     * @return XmlParser
     */
    @Synchronized
    fun getXmlParser(): XmlParser {
        if (xmlParser == null) {
            xmlParser = createXmlParser(context)
        }
        return xmlParser!!
    }

    /**
     * Get an [AudioEngine].
     * You have to include `compile 'com.google.vr:sdk-audio:X.X.X'` in build.gradle to use [AudioEngine].
     *
     * @return AudioEngine
     */
    fun getAudioEngine(): AudioEngine {
        if (audioEngine == null) {
            audioEngine = AudioEngine(context)
        }
        return audioEngine!!
    }

    /**
     * Instantiate [XmlParser]. Called at first time with [.getXmlParser].
     *
     * @param context context
     * @return new instance of XmlParser.
     */
    protected open fun createXmlParser(context: Context): XmlParser {
        return XmlParser(context)
    }

    fun setSceneFromXmlAsset(assetName: String): Scene {
        try {
            val entity = getXmlParser().parseAsset(assetName)
            if (entity is Scene) {
                scene = entity
                return entity
            } else {
                throw IllegalArgumentException("XML first element must be <scene>.")
            }
        } catch (e: XmlParserException) {
            throw RuntimeException("Cannot parse XML from $assetName", e)
        }

    }

    fun setSceneFromXml(uri: String): Scene {
        try {
            val entity = getXmlParser().parseUri(uri)
            if (entity is Scene) {
                scene = entity
                return entity
            } else {
                throw IllegalArgumentException("XML first element must be <scene>.")
            }
        } catch (e: XmlParserException) {
            throw RuntimeException("Cannot parse XML from $uri", e)
        }

    }

    fun setSceneFromXml(xmlRes: Int): Scene {
        try {
            val entity = getXmlParser().parseXmlResource(xmlRes)
            if (entity is Scene) {
                scene = entity
                return entity
            } else {
                throw IllegalArgumentException("XML first element must be <scene>.")
            }
        } catch (e: XmlParserException) {
            throw RuntimeException("Cannot parse XML from ${context.resources.getResourceName(xmlRes)}", e)
        }

    }

    fun setSceneFromXml(file: File): Scene {
        try {
            val entity = getXmlParser().parseFile(file)
            if (entity is Scene) {
                scene = entity
                return entity
            } else {
                throw IllegalArgumentException("XML first element must be <scene>.")
            }
        } catch (e: XmlParserException) {
            throw RuntimeException("Cannot parse XML from $file", e)
        }

    }

    /**
     * Run [Animator] on UI thread and notify end callback on GL thread.
     *
     * @param anim        [Animator].
     * @param endCallback Callback for animation end. This is **not** called when animation is canceled.
     * If you require more complicated callbacks, use `AnimatorListener` instead of this.
     */
    fun animate(anim: Animator, endCallback: (() -> Unit)?) {

        if (anim.isRunning) {
            cancel(anim, { animate(anim, endCallback) })
            return
        }

        // Register one time animation end callback
        if (endCallback != null) {
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    anim.removeListener(this)
                }

                override fun onAnimationEnd(animation: Animator) {
                    anim.removeListener(this)
                    runOnGlThread(endCallback)
                }
            })
        }

        runOnUiThread({ anim.start() })
    }

    /**
     * Cancel [Animator] running.
     *
     * @param anim     [Animator].
     * @param callback Callback for canceling operation was called in UI thread.
     */
    fun cancel(anim: Animator, callback: (() -> Unit)?) {
        runOnUiThread({
            anim.cancel()
            if (callback != null) runOnGlThread(callback)
        })
    }

    open fun onHmdMounted() {}

    open fun onHmdUnmounted() {}

    /**
     * This is called when Activity is resumed.
     * Note that it is called on Android's main thread.
     * If you do something with GL related tasks, use [.runOnGlThread].
     */
    open fun onResume() {}

    /**
     * This is called when Activity is paused.
     * Note that it is called on Android's main thread.
     * If you do something with GL related tasks, use [.runOnGlThread].
     */
    open fun onPause() {}

    /**
     * This will be called right after entering VR mode.
     * This can be considered as GL version of [Activity.onResume].
     */
    open fun enteredVrMode() {}

    /**
     * This will be called right before leaving VR mode.
     * This can be considered as GL version of [Activity.onPause].
     */
    open fun leavingVrMode() {}
}
