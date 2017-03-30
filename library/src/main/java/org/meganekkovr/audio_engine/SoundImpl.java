package org.meganekkovr.audio_engine;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.vr.sdk.audio.GvrAudioEngine;

import java.util.SortedMap;
import java.util.TreeMap;

class SoundImpl implements SoundObject, SoundField, StereoSound {
    private final GvrAudioEngine audioEngine;
    private final int id;
    private final SortedMap<Float, Runnable> timedEvents = new TreeMap<>();
    private float volume = 1;
    private float currentTime;
    private boolean willBeInPlaying;
    private Runnable onEndCallback;

    SoundImpl(GvrAudioEngine audioEngine, int id) {
        this.audioEngine = audioEngine;
        this.id = id;
    }

    @Override
    public void play(boolean loopingEnabled) {
        this.willBeInPlaying = true;
        audioEngine.playSound(id, loopingEnabled);
    }

    @Override
    public void pause() {
        this.willBeInPlaying = false;
        audioEngine.pauseSound(id);
    }

    @Override
    public void resume() {
        this.willBeInPlaying = true;
        audioEngine.resumeSound(id);
    }

    @Override
    public void stop() {
        // I don't set willBeInPlaying to false here for onEndCallback.
        audioEngine.stopSound(id);
    }

    @Override
    public boolean isPlaying() {
        return audioEngine.isSoundPlaying(id);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        audioEngine.setSoundObjectPosition(id, x, y, z);
    }

    @Override
    public void setDistanceRolloffModel(int rolloffModel, float minDistance, float maxDistance) {
        audioEngine.setSoundObjectDistanceRolloffModel(id, rolloffModel, minDistance, maxDistance);
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        audioEngine.setSoundVolume(id, volume);
    }

    @Override
    public void fadeVolume(float volume, float time) {
        final ValueAnimator animator = ValueAnimator.ofFloat(this.volume, volume)
                .setDuration((long) (time * 1000));

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                SoundImpl.this.setVolume(value);
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                animator.start();
            }
        });
    }

    @Override
    public void setRotation(float x, float y, float z, float w) {
        audioEngine.setSoundfieldRotation(id, x, y, z, w);
    }

    @Override
    public void onEnd(Runnable callback) {
        this.onEndCallback = callback;
    }

    @Override
    public boolean isEnded() {
        return willBeInPlaying && !isPlaying();
    }

    void notifyOnEnd() {
        if (onEndCallback != null) {
            onEndCallback.run();
            onEndCallback = null;
        }
    }

    @Override
    public void addTimedEvent(float time, @NonNull Runnable event) {
        timedEvents.put(time, event);
    }

    void deltaSeconds(float deltaSeconds) {

        currentTime += deltaSeconds;

        // Emit timed events
        if (!timedEvents.isEmpty()) {
            Float firstKey = timedEvents.firstKey();
            if (firstKey < currentTime) {
                Runnable runnable = timedEvents.remove(firstKey);
                runnable.run();
            }
        }
    }
}
