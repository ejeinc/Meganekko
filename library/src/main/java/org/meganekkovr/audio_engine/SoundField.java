package org.meganekkovr.audio_engine;

/**
 * Represents sound field (ambisonic) specific operation of {@link com.google.vr.sdk.audio.GvrAudioEngine}.
 */
public interface SoundField extends SoundObject {

    /**
     * Sets the rotation of an existing Ambisonic soundfield.
     *
     * @param x x component of the quaternion describing the rotation
     * @param y y component of the quaternion describing the rotation
     * @param z z component of the quaternion describing the rotation
     * @param w w component of the quaternion describing the rotation
     */
    void setRotation(float x, float y, float z, float w);
}
