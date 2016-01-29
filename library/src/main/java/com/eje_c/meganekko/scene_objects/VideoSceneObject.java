/* 
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko.scene_objects;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrFrame;

/**
 * A {@linkplain SceneObject scene object} that shows video, using the Android
 * {@link MediaPlayer}.
 */
public class VideoSceneObject extends SceneObject {
    private SurfaceTexture mSurfaceTexture = null;
    private MediaPlayer mMediaPlayer = null;
    private boolean mActive = true;

    /**
     * Play a video on a {@linkplain SceneObject scene object} with an
     * arbitrarily complex geometry, using the Android {@link MediaPlayer}. You
     * have to call {@code VideoSceneObject#setMediaPlayer(MediaPlayer)}} to
     * play video.
     */
    public VideoSceneObject() {
        // Setup material
        Material material = new Material();
        mSurfaceTexture = material.getSurfaceTexture();
        RenderData renderData = new RenderData();
        renderData.setMaterial(material);
        attachRenderData(renderData);
    }

    /**
     * Poll the {@link MediaPlayer} once per frame.
     * <p/>
     * This call does not directly affect the {@link MediaPlayer}. In
     * particular, activation is not the same as calling
     * {@link MediaPlayer#start()}.
     */
    public void activate() {
        mActive = true;
    }

    /**
     * Stop polling the {@link MediaPlayer}.
     * <p/>
     * This call does not directly affect the {@link MediaPlayer}. In
     * particular, deactivation is not the same as calling
     * {@link MediaPlayer#pause()}.
     */
    public void deactivate() {
        mActive = false;
    }

    /**
     * Returns the current {@link MediaPlayer} status.
     * <p/>
     * See {@link #activate()} and {@link #deactivate()}: polling activation is
     * not correlated with the {@code MediaPlayer} state.
     *
     * @return Whether or not we polling the {@code MediaPlayer} every frame.
     */
    public boolean isActive() {
        return mActive;
    }

    /**
     * Returns the current {@link MediaPlayer}, if any
     *
     * @return current {@link MediaPlayer}
     */
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    /**
     * Sets the current {@link MediaPlayer}
     *
     * @param mediaPlayer An Android {@link MediaPlayer}
     */
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        release();// any current MediaPlayer

        mMediaPlayer = mediaPlayer;
        Surface surface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    /**
     * Reset and {@link MediaPlayer#release() release()} the current
     * {@link MediaPlayer}, if any
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * Returns the current time stamp, in nanoseconds. This comes from
     * {@link SurfaceTexture#getTimestamp()}: you should read the Android
     * documentation on that before you use this value.
     *
     * @return current time stamp, in nanoseconds
     */
    public long getTimeStamp() {
        return mSurfaceTexture.getTimestamp();
    }

    @Override
    public void update(VrFrame vrFrame) {
        if (mMediaPlayer != null && mActive) {
            mSurfaceTexture.updateTexImage();
        }
        super.update(vrFrame);
    }
}
