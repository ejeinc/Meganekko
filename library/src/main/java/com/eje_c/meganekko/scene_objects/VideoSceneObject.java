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
import com.eje_c.meganekko.Material.ShaderType;
import com.eje_c.meganekko.MaterialShaderId;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.VrFrame;

import java.io.IOException;

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
     *
     * @param vrContext current {@link VrContext}
     */
    public VideoSceneObject(VrContext vrContext) {
        super(vrContext);

        // Setup material
        Material material = new Material(vrContext, ShaderType.OES.ID);
        mSurfaceTexture = material.getSurfaceTexture();
        RenderData renderData = new RenderData(vrContext);
        renderData.setMaterial(material);
        attachRenderData(renderData);
    }

    @Deprecated
    public VideoSceneObject(VrContext vrContext, Mesh mesh, MediaPlayer mediaPlayer, int videoType) {
        this(vrContext, mesh, mediaPlayer, VideoType.values()[videoType]);
    }

    /**
     * Play a video on a {@linkplain SceneObject scene object} with an
     * arbitrarily complex geometry, using the Android {@link MediaPlayer}
     *
     * @param vrContext   current {@link VrContext}
     * @param mesh        a {@link Mesh} - see
     *                    {@link VrContext#loadMesh(com.eje_c.meganekko.AndroidResource)}
     *                    and {@link VrContext#createQuad(float, float)}
     * @param mediaPlayer an Android {@link MediaPlayer}
     * @param videoType   One of the {@linkplain VideoType video type constants}
     * @throws IllegalArgumentException on an invalid {@code videoType} parameter
     */
    public VideoSceneObject(VrContext vrContext, Mesh mesh, MediaPlayer mediaPlayer, VideoType videoType) {
        super(vrContext, mesh);

        MaterialShaderId materialType;
        switch (videoType) {
            case MONO:
                materialType = ShaderType.OES.ID;
                break;
            case HORIZONTAL_STEREO:
                materialType = ShaderType.OESHorizontalStereo.ID;
                break;
            case VERTICAL_STEREO:
                materialType = ShaderType.OESVerticalStereo.ID;
                break;
            default:
                throw new IllegalArgumentException();
        }
        Material material = new Material(vrContext, materialType);
        mSurfaceTexture = material.getSurfaceTexture();
        getRenderData().setMaterial(material);

        mMediaPlayer = mediaPlayer;
    }

    /**
     * Play a video on a 2D, rectangular {@linkplain SceneObject scene object,}
     * using the Android {@link MediaPlayer}
     *
     * @param vrContext   current {@link VrContext}
     * @param width       the rectangle's width
     * @param height      the rectangle's height
     * @param mediaPlayer an Android {@link MediaPlayer}
     * @param videoType   One of the {@linkplain VideoType video type constants}
     * @throws IllegalArgumentException on an invalid {@code videoType} parameter
     */
    public VideoSceneObject(VrContext vrContext, float width,
                            float height, MediaPlayer mediaPlayer, VideoType videoType) {
        this(vrContext, vrContext.createQuad(width, height), mediaPlayer,
                videoType);
    }

    @Deprecated
    public VideoSceneObject(VrContext vrContext, float width,
                            float height, MediaPlayer mediaPlayer, int videoType) {
        this(vrContext, width, height, mediaPlayer, VideoType.values()[videoType]);
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

    public void setVideoType(VideoType videoType) {

        switch (videoType) {
            case MONO:
                getRenderData().getMaterial().setShaderType(ShaderType.OES.ID);
                break;
            case HORIZONTAL_STEREO:
                getRenderData().getMaterial().setShaderType(ShaderType.OESHorizontalStereo.ID);
                break;
            case VERTICAL_STEREO:
                getRenderData().getMaterial().setShaderType(ShaderType.OESVerticalStereo.ID);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onEvent(VrFrame vrFrame) {
        if (mMediaPlayer != null && mActive) {
            mSurfaceTexture.updateTexImage();
        }
        super.onEvent(vrFrame);
    }

    /**
     * Video type constants, for use with {@link VideoSceneObject}
     */
    public enum VideoType {
        MONO, HORIZONTAL_STEREO, VERTICAL_STEREO
    }
}
