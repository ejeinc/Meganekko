package org.meganekkovr.audio_engine

/**
 * Represents general sound. If you require only basic operations like a [.play],
 * [.pause] or [.stop], use this.
 */
interface Sound {

    /**
     * Checks if a sound is playing.
     *
     * @return true if the sound is being played
     */
    val isPlaying: Boolean

    /**
     * Indicates if sound is ended or stopped.
     *
     * @return true if sound is ended or stopped.
     */
    val isEnded: Boolean

    /**
     * Starts the playback of a sound.
     *
     * @param loopingEnabled enables looped playback
     */
    fun play(loopingEnabled: Boolean)

    /**
     * Pauses the playback of a sound.
     */
    fun pause()

    /**
     * Resumes the GVR Audio system.
     */
    fun resume()

    /**
     * Stops the playback of a sound and destroys the corresponding Sound Object or Soundfield.
     */
    fun stop()

    /**
     * Changes the volume of an existing sound.
     *
     * @param volume volume value. Should range from 0 (mute) to 1 (max)
     */
    fun setVolume(volume: Float)

    /**
     * Fade volume to target value.
     *
     * @param volume volume value. Should range from 0 (mute) to 1 (max)
     * @param time   fading time
     */
    fun fadeVolume(volume: Float, time: Float)

    /**
     * Set callback on end.
     *
     * @param callback
     */
    fun onEnd(callback: () -> Unit)

    /**
     * Add new timed event.
     *
     * @param time  Event time in seconds
     * @param event Event callback
     */
    fun addTimedEvent(time: Float, event: () -> Unit)
}
