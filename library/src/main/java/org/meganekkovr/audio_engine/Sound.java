package org.meganekkovr.audio_engine;

import android.support.annotation.NonNull;

/**
 * Represents general sound. If you require only basic operations like a {@link #play(boolean)},
 * {@link #pause()} or {@link #stop()}, use this.
 */
public interface Sound {

    /**
     * Starts the playback of a sound.
     *
     * @param loopingEnabled enables looped playback
     */
    void play(boolean loopingEnabled);

    /**
     * Pauses the playback of a sound.
     */
    void pause();

    /**
     * Resumes the GVR Audio system.
     */
    void resume();

    /**
     * Stops the playback of a sound and destroys the corresponding Sound Object or Soundfield.
     */
    void stop();

    /**
     * Checks if a sound is playing.
     *
     * @return true if the sound is being played
     */
    boolean isPlaying();

    /**
     * Changes the volume of an existing sound.
     *
     * @param volume volume value. Should range from 0 (mute) to 1 (max)
     */
    void setVolume(float volume);

    /**
     * Fade volume to target value.
     *
     * @param volume volume value. Should range from 0 (mute) to 1 (max)
     * @param time   fading time
     */
    void fadeVolume(float volume, float time);

    /**
     * Set callback on end.
     *
     * @param callback
     */
    void onEnd(Runnable callback);

    /**
     * Indicates if sound is ended or stopped.
     *
     * @return true if sound is ended or stopped.
     */
    boolean isEnded();

    /**
     * Add new timed event.
     *
     * @param time  Event time in seconds
     * @param event Event callback
     */
    void addTimedEvent(float time, @NonNull Runnable event);
}
