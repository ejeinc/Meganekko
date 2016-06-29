package com.eje_c.meganekko.sample.quadbinaural;

import android.content.Context;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class QuadBinauralPlayer {

    private final Context context;
    private final File pd;
    private int pdHandle;
    private String audioPath;
    private int seekTo = 0;
    private boolean playing;

    public QuadBinauralPlayer(Context context) {
        this.context = context;
        pd = new File(context.getFilesDir(), "quad_binaural.pd");
    }

    /**
     * Initialize with default parameter values.
     *
     * @throws IOException
     */
    public void init() throws IOException {
        init(1, true);
    }

    /**
     * You must call this method before any methods.
     *
     * @throws IOException
     */
    public void init(int ticksPerBuffer, boolean restart) throws IOException {

        // Copy pd file
        IoUtils.extractResource(context.getResources().openRawResource(R.raw.quad_binaural), pd.getName(), pd.getParentFile());

        final int sampleRate = AudioParameters.suggestSampleRate();
        final int outChannels = AudioParameters.suggestOutputChannels();
        PdAudio.initAudio(sampleRate, 0, outChannels, ticksPerBuffer, restart);
        PdAudio.startAudio(context);
        pdHandle = PdBase.openPatch(pd);
    }

    /**
     * Set audio file.
     *
     * @param audioPath
     * @throws IOException
     */
    public void setAudioPath(String audioPath) throws IOException {
        this.audioPath = audioPath;
        this.seekTo = 0;
        this.playing = false;
        PdBase.sendMessage("message", "open", audioPath, seekTo);
    }

    public void seekTo(int seekTo) {
        this.seekTo = seekTo;
        PdBase.sendMessage("message", "open", audioPath, seekTo);

        if (playing) {
            start();
        }
    }

    /**
     * Reset Pd. You can reuse player later.
     */
    public void reset() {
        PdBase.closePatch(pdHandle);
        PdAudio.stopAudio();
    }

    /**
     * You must call this method when player is no longer used.
     */
    public void release() {
        PdAudio.release();
        PdBase.release();
    }

    /**
     * Call this to spatialize audio.
     *
     * @param lookAtVector 3-dimensional normalized vector that represents head orientation.
     *                     [x, y, z]
     *                     +x is right
     *                     +y is up (currently not used)
     *                     +z is forward
     */
    public void setLookDirection(float[] lookAtVector) {
        float dist = (float) Math.sqrt(lookAtVector[0] * lookAtVector[0] + lookAtVector[2] * lookAtVector[2]);
        if (dist > 0.001f) {
            float x = lookAtVector[0] / dist;
            float z = lookAtVector[2] / dist;
            PdBase.sendFloat("x", x);
            PdBase.sendFloat("z", z);
        }
    }

    /**
     * Start playing.
     */
    public void start() {
        playing = true;
        PdBase.sendFloat("control", 1);
    }

    /**
     * Stop playing.
     */
    public void stop() {
        playing = false;
        PdBase.sendFloat("control", 0);
    }
}
