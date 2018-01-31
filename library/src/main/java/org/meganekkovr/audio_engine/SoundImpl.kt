package org.meganekkovr.audio_engine

import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import com.google.vr.sdk.audio.GvrAudioEngine
import java.util.*

internal class SoundImpl(private val audioEngine: GvrAudioEngine, private val id: Int) : SoundObject, SoundField, StereoSound {
    private val timedEvents = TreeMap<Float, Runnable>()
    private var volume = 1f
    private var currentTime: Float = 0.toFloat()
    private var willBeInPlaying: Boolean = false
    private var onEndCallback: Runnable? = null

    override val isPlaying: Boolean
        get() = audioEngine.isSoundPlaying(id)

    override val isEnded: Boolean
        get() = willBeInPlaying && !isPlaying

    override fun play(loopingEnabled: Boolean) {
        this.willBeInPlaying = true
        audioEngine.playSound(id, loopingEnabled)
    }

    override fun pause() {
        this.willBeInPlaying = false
        audioEngine.pauseSound(id)
    }

    override fun resume() {
        this.willBeInPlaying = true
        audioEngine.resumeSound(id)
    }

    override fun stop() {
        // I don't set willBeInPlaying to false here for onEndCallback.
        audioEngine.stopSound(id)
    }

    override fun setPosition(x: Float, y: Float, z: Float) {
        audioEngine.setSoundObjectPosition(id, x, y, z)
    }

    override fun setDistanceRolloffModel(rolloffModel: Int, minDistance: Float, maxDistance: Float) {
        audioEngine.setSoundObjectDistanceRolloffModel(id, rolloffModel, minDistance, maxDistance)
    }

    override fun setVolume(volume: Float) {
        this.volume = volume
        audioEngine.setSoundVolume(id, volume)
    }

    override fun fadeVolume(volume: Float, time: Float) {
        val animator = ValueAnimator.ofFloat(this.volume, volume)
                .setDuration((time * 1000).toLong())

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            this@SoundImpl.setVolume(value)
        }

        Handler(Looper.getMainLooper()).post { animator.start() }
    }

    override fun setRotation(x: Float, y: Float, z: Float, w: Float) {
        audioEngine.setSoundfieldRotation(id, x, y, z, w)
    }

    override fun onEnd(callback: Runnable) {
        this.onEndCallback = callback
    }

    fun notifyOnEnd() {
        onEndCallback?.run()
        onEndCallback = null
    }

    override fun addTimedEvent(time: Float, event: Runnable) {
        timedEvents[time] = event
    }

    fun deltaSeconds(deltaSeconds: Float) {

        currentTime += deltaSeconds

        // Emit timed events
        if (!timedEvents.isEmpty()) {
            val firstKey = timedEvents.firstKey()
            if (firstKey < currentTime) {
                timedEvents.remove(firstKey)?.run()
            }
        }
    }
}
