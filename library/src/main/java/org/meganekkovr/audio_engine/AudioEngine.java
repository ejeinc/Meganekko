package org.meganekkovr.audio_engine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;

import com.google.vr.sdk.audio.GvrAudioEngine;

import org.joml.Quaternionf;
import org.meganekkovr.FrameInput;
import org.meganekkovr.HeadTransform;

import java.util.Iterator;
import java.util.Set;

/**
 * Engine to spatialize sound sources in 3D space.
 * This is actually a wrapper for {@link GvrAudioEngine}. You need to add a dependency {@code compile 'com.google.vr:sdk-audio:X.X.X'}.
 * This configuration is also required:
 * <pre>
 * defaultConfig {
 *   ndk {
 *     abiFilters 'armeabi-v7a'
 *   }
 * }
 * </pre>
 */
public class AudioEngine {

    private final GvrAudioEngine audioEngine;
    private final Set<SoundImpl> sounds = new ArraySet<>();
    private boolean resumed = true;

    /**
     * Create an audio engine with default (binaural high quality) mode.
     *
     * @param context Android context
     */
    public AudioEngine(Context context) {
        this(context, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
    }

    /**
     * Create an audio engine.
     *
     * @param context       Android context
     * @param renderingMode Must be a one of {@link com.google.vr.sdk.audio.GvrAudioEngine.RenderingMode} constants.
     */
    public AudioEngine(Context context, int renderingMode) {
        audioEngine = new GvrAudioEngine(context, renderingMode);
    }

    /**
     * Should be called from at every frame update.
     *
     * @param frame Frame information.
     */
    public void update(@NonNull FrameInput frame) {

        if (resumed) {

            // Apply head transform
            Quaternionf headTransform = HeadTransform.getInstance().getQuaternion();
            audioEngine.setHeadRotation(headTransform.x, headTransform.y, headTransform.z, headTransform.w);
            audioEngine.update();

            if (!sounds.isEmpty()) {
                updateSounds(frame);
            }
        }
    }

    private void updateSounds(@NonNull FrameInput frame) {

        float dt = frame.getDeltaSeconds();

        for (Iterator<SoundImpl> iterator = sounds.iterator(); iterator.hasNext(); ) {
            SoundImpl sound = iterator.next();

            // Run callbacks registered with Sound.addTimedEvent()
            if (sound.isPlaying()) {
                sound.deltaSeconds(dt);
            }

            // Run callback registered with Sound.onEnd()
            if (sound.isEnded()) {
                sound.notifyOnEnd();
                iterator.remove();
            }
        }
    }

    /**
     * Pauses the GVR Audio system.
     */
    public void pause() {
        this.resumed = false;
        audioEngine.pause();
    }

    /**
     * Resumes the GVR Audio system.
     */
    public void resume() {
        this.resumed = true;
        audioEngine.resume();
    }

    /**
     * Turns on/off the room reverberation effects.
     *
     * @param enable true to enable room effects
     */
    public void enableRoom(boolean enable) {
        audioEngine.enableRoom(enable);
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
    public void setRoomProperties(float sizeX, float sizeY, float sizeZ, int wallMaterial, int ceilingMaterial, int floorMaterial) {
        audioEngine.setRoomProperties(sizeX, sizeY, sizeZ, wallMaterial, ceilingMaterial, floorMaterial);
    }

    /**
     * Adjusts the properties of the current reverb, allowing changes to the reverb's gain, duration and low/high frequency balance.
     *
     * @param gain             reverb volume (linear) adjustment in range [0, 1] for attenuation, range [1, inf) for gain boost
     * @param timeAdjust       reverb time adjustment multiplier to scale the reverberation tail length.
     * @param brightnessAdjust reverb brightness adjustment that controls the reverberation ratio across low and high frequency bands
     */
    public void setRoomReverbAdjustments(float gain, float timeAdjust, float brightnessAdjust) {
        audioEngine.setRoomReverbAdjustments(gain, timeAdjust, brightnessAdjust);
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
    public void enableSpeakerStereoMode(boolean enable) {
        audioEngine.enableSpeakerStereoMode(enable);
    }

    /**
     * Preloads a sound file. This method can be used for both mono and Ambisonic sound files.
     *
     * @param fileName path/name of the file to be played
     * @return true on success
     */
    public boolean preload(String fileName) {
        return audioEngine.preloadSoundFile(fileName);
    }

    /**
     * Unloads a sound file from the sample cache.
     * Note that memory is freed at the moment the playback of the sound file stops.
     *
     * @param fileName of sound file to be freed from memory
     */
    public void unload(String fileName) {
        audioEngine.unloadSoundFile(fileName);
    }

    /*
     * Sound creation.
     */

    /**
     * Returns a new {@link SoundObject}.
     * Note that the sample should only contain a single audio channel (stereo sources are automatically downmixed to mono).
     * The handle automatically destroys itself at the moment the sound playback has stopped.
     *
     * @param fileName path/name of the file to be played
     * @return new {@link SoundObject}.
     * @throws IllegalArgumentException thrown if the sound file could not be loaded
     */
    @NonNull
    public SoundObject createSoundObject(String fileName) throws IllegalArgumentException {
        int soundObject = audioEngine.createSoundObject(fileName);
        if (soundObject != GvrAudioEngine.INVALID_ID) {
            return createSound(soundObject);
        } else {
            throw new IllegalArgumentException("Cannot create sound object from " + fileName + ". Is it a mono sound file?");
        }
    }

    /**
     * Returns a new Ambisonic soundfield handle.
     * Note that the sample must have 4 audio channels. Ambisonic soundfields do *not* need to be preloaded.
     * They are directly streamed and rendered from the compressed audio file.
     * The handle automatically destroys itself at the moment the sound playback has stopped.
     *
     * @param fileName path/name of the file to be played
     * @return new {@link SoundField}.
     * @throws IllegalArgumentException thrown if the sound file could not be loaded
     */
    @NonNull
    public SoundField createSoundfield(String fileName) throws IllegalArgumentException {
        int soundfield = audioEngine.createSoundfield(fileName);
        if (soundfield != GvrAudioEngine.INVALID_ID) {
            return createSound(soundfield);
        } else {
            throw new IllegalArgumentException("Cannot create sound field from " + fileName + ". Is it an ambisonic sound file?");
        }
    }

    /**
     * Returns a new non-spatialized stereo sound.
     * Note that the sample must have at most two audio channels. Both mono and stereo audio files are supported.
     * The handle automatically destroys itself at the moment the sound playback has stopped.
     *
     * @param fileName path/name of the file to be played
     * @return new {@link StereoSound}.
     * @throws IllegalArgumentException thrown if the sound file could not be loaded
     */
    @NonNull
    public StereoSound createStereoSound(String fileName) throws IllegalArgumentException {
        int stereoSound = audioEngine.createStereoSound(fileName);
        if (stereoSound != GvrAudioEngine.INVALID_ID) {
            return createSound(stereoSound);
        } else {
            throw new IllegalArgumentException("Cannot create stereo sound from " + fileName + ". Is it a stereo sound file?");
        }
    }

    @NonNull
    private SoundImpl createSound(int id) {
        SoundImpl sound = new SoundImpl(audioEngine, id);
        sounds.add(sound);
        return sound;
    }
}
