package org.meganekkovr.audio_engine

import android.content.Context
import com.google.vr.sdk.audio.GvrAudioEngine
import org.meganekkovr.FrameInput
import org.meganekkovr.HeadTransform

/**
 * Engine to spatialize sound sources in 3D space.
 * This is actually a wrapper for [GvrAudioEngine]. You need to add a dependency `compile 'com.google.vr:sdk-audio:X.X.X'`.
 * This configuration is also required:
 * <pre>
 * defaultConfig {
 * ndk {
 * abiFilters 'armeabi-v7a'
 * }
 * }
</pre> *
 */
class AudioEngine
/**
 * Create an audio engine.
 *
 * @param context       Android context
 * @param renderingMode Must be a one of [com.google.vr.sdk.audio.GvrAudioEngine.RenderingMode] constants.
 */
@JvmOverloads constructor(context: Context, renderingMode: Int = GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY) {

    private val audioEngine: GvrAudioEngine = GvrAudioEngine(context, renderingMode)
    private val sounds = mutableSetOf<SoundImpl>()
    private var resumed = true

    /**
     * Should be called from at every frame update.
     *
     * @param frame Frame information.
     */
    fun update(frame: FrameInput) {

        if (resumed) {

            // Apply head transform
            val headTransform = HeadTransform.instance.quaternion
            audioEngine.setHeadRotation(headTransform.x, headTransform.y, headTransform.z, headTransform.w)
            audioEngine.update()

            if (!sounds.isEmpty()) {
                updateSounds(frame)
            }
        }
    }

    private fun updateSounds(frame: FrameInput) {

        val dt = frame.deltaSeconds

        val iterator = sounds.iterator()
        while (iterator.hasNext()) {
            val sound = iterator.next()

            // Run callbacks registered with Sound.addTimedEvent()
            if (sound.isPlaying) {
                sound.deltaSeconds(dt)
            }

            // Run callback registered with Sound.onEnd()
            if (sound.isEnded) {
                sound.notifyOnEnd()
                iterator.remove()
            }
        }
    }

    /**
     * Pauses the GVR Audio system.
     */
    fun pause() {
        this.resumed = false
        audioEngine.pause()
    }

    /**
     * Resumes the GVR Audio system.
     */
    fun resume() {
        this.resumed = true
        audioEngine.resume()
    }

    /**
     * Turns on/off the room reverberation effects.
     *
     * @param enable true to enable room effects
     */
    fun enableRoom(enable: Boolean) {
        audioEngine.enableRoom(enable)
    }

    /**
     * Sets the room properties describing the dimensions and surface materials of a given room.
     *
     * @param sizeX           dimension along X axis
     * @param sizeY           dimension along Y axis
     * @param sizeZ           dimension along Z axis
     * @param wallMaterial    MaterialName id for the four walls
     * @param ceilingMaterial MaterialName id for the ceiling
     * @param floorMaterial   MaterialName id for the floor
     */
    fun setRoomProperties(sizeX: Float, sizeY: Float, sizeZ: Float, wallMaterial: Int, ceilingMaterial: Int, floorMaterial: Int) {
        audioEngine.setRoomProperties(sizeX, sizeY, sizeZ, wallMaterial, ceilingMaterial, floorMaterial)
    }

    /**
     * Adjusts the properties of the current reverb, allowing changes to the reverb's gain, duration and low/high frequency balance.
     *
     * @param gain             reverb volume (linear) adjustment in range [0, 1] for attenuation, range [1, inf) for gain boost
     * @param timeAdjust       reverb time adjustment multiplier to scale the reverberation tail length.
     * @param brightnessAdjust reverb brightness adjustment that controls the reverberation ratio across low and high frequency bands
     */
    fun setRoomReverbAdjustments(gain: Float, timeAdjust: Float, brightnessAdjust: Float) {
        audioEngine.setRoomReverbAdjustments(gain, timeAdjust, brightnessAdjust)
    }

    /**
     * Enables the stereo speaker mode.
     * It enforces stereo-panning when headphone are not plugged into the phone.
     * This helps to avoid HRTF-based coloring effects and reduces computational complexity
     * when speaker playback is active. By default the stereo speaker mode optimization is disabled.
     * Note that switching between rendering modes can lead to varying CPU usage based on the audio output routing.
     *
     * @param enable true to enable the speaker stereo mode
     */
    fun enableSpeakerStereoMode(enable: Boolean) {
        audioEngine.enableSpeakerStereoMode(enable)
    }

    /**
     * Preloads a sound file. This method can be used for both mono and Ambisonic sound files.
     *
     * @param fileName path/name of the file to be played
     * @return true on success
     */
    fun preload(fileName: String): Boolean {
        return audioEngine.preloadSoundFile(fileName)
    }

    /**
     * Unloads a sound file from the sample cache.
     * Note that memory is freed at the moment the playback of the sound file stops.
     *
     * @param fileName of sound file to be freed from memory
     */
    fun unload(fileName: String) {
        audioEngine.unloadSoundFile(fileName)
    }

    /*
     * Sound creation.
     */

    /**
     * Returns a new [SoundObject].
     * Note that the sample should only contain a single audio channel (stereo sources are automatically downmixed to mono).
     * The handle automatically destroys itself at the moment the sound playback has stopped.
     *
     * @param fileName path/name of the file to be played
     * @return new [SoundObject].
     */
    fun createSoundObject(fileName: String): SoundObject {
        val soundObject = audioEngine.createSoundObject(fileName)
        check(soundObject != GvrAudioEngine.INVALID_ID) { "Cannot create sound object from $fileName. Is it a mono sound file?" }
        return createSound(soundObject)
    }

    /**
     * Returns a new Ambisonic soundfield handle.
     * Note that the sample must have 4 audio channels. Ambisonic soundfields do *not* need to be preloaded.
     * They are directly streamed and rendered from the compressed audio file.
     * The handle automatically destroys itself at the moment the sound playback has stopped.
     *
     * @param fileName path/name of the file to be played
     * @return new [SoundField].
     */
    fun createSoundfield(fileName: String): SoundField {
        val soundfield = audioEngine.createSoundfield(fileName)
        check(soundfield != GvrAudioEngine.INVALID_ID) { "Cannot create sound field from $fileName. Is it an ambisonic sound file?" }
        return createSound(soundfield)
    }

    /**
     * Returns a new non-spatialized stereo sound.
     * Note that the sample must have at most two audio channels. Both mono and stereo audio files are supported.
     * The handle automatically destroys itself at the moment the sound playback has stopped.
     *
     * @param fileName path/name of the file to be played
     * @return new [StereoSound].
     */
    fun createStereoSound(fileName: String): StereoSound {
        val stereoSound = audioEngine.createStereoSound(fileName)
        check(stereoSound != GvrAudioEngine.INVALID_ID) { "Cannot create stereo sound from $fileName. Is it a stereo sound file?" }
        return createSound(stereoSound)
    }

    private fun createSound(id: Int): SoundImpl {
        val sound = SoundImpl(audioEngine, id)
        sounds.add(sound)
        return sound
    }
}
