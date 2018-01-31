package org.meganekkovr.audio_engine

/**
 * Represents sound object specific operation of [com.google.vr.sdk.audio.GvrAudioEngine].
 */
interface SoundObject : Sound {

    /**
     * Repositions an existing sound object.
     *
     * @param x x coordinate the sound will be placed at
     * @param y y coordinate the sound will be placed at
     * @param z z coordinate the sound will be placed at
     */
    fun setPosition(x: Float, y: Float, z: Float)

    /**
     * Sets the given sound object source's distance attenuation method with minimum and maximum distances.
     * Maximum distance must be greater than the minimum distance for the method to be set.
     *
     * @param rolloffModel DistanceRolloffModel, note setting the rolloff model to DistanceRolloffModel::NONE will allow distance attenuation to be set manually
     * @param minDistance  minimum distance to apply distance attenuation method
     * @param maxDistance  maximum distance to apply distance attenuation method
     */
    fun setDistanceRolloffModel(rolloffModel: Int, minDistance: Float, maxDistance: Float)
}
