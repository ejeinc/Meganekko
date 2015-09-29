/* Copyright 2015 Samsung Electronics Co., LTD
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

import java.io.IOException;

import com.eje_c.meganekko.ExternalTexture;
import com.eje_c.meganekko.FrameListener;
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Material.ShaderType;
import com.eje_c.meganekko.MaterialShaderId;
import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;

/**
 * A {@linkplain SceneObject scene object} that shows video, using the Android
 * {@link MediaPlayer}.
 */
public class VideoSceneObject extends SceneObject {
    private final Video mVideo;

    /** Video type constants, for use with {@link VideoSceneObject} */
    public enum VideoType {
        MONO, HORIZONTAL_STEREO, VERTICAL_STEREO
    }

    /**
     * Play a video on a {@linkplain SceneObject scene object} with an
     * arbitrarily complex geometry, using the Android {@link MediaPlayer}. You
     * have to call {@code VideoSceneObject#setMediaPlayer(MediaPlayer)}} to
     * play video.
     * 
     * @param vrContext
     *            current {@link VrContext}
     * @param mesh
     *            a {@link Mesh} - see
     *            {@link VrContext#loadMesh(com.eje_c.meganekko.GVRAndroidResource)}
     *            and {@link VrContext#createQuad(float, float)}
     * @param mediaPlayer
     *            an Android {@link MediaPlayer}
     * @param videoType
     *            One of the {@linkplain VideoType video type constants}
     * @throws IllegalArgumentException
     *             on an invalid {@code videoType} parameter
     */
    public VideoSceneObject(VrContext vrContext) {
        super(vrContext);

        ExternalTexture texture = new ExternalTexture(vrContext);
        Material material = new Material(vrContext, ShaderType.OES.ID);
        material.setMainTexture(texture);
        RenderData renderData = new RenderData(vrContext);
        renderData.setMaterial(material);
        attachRenderData(renderData);

        mVideo = new Video(null, texture);
        vrContext.registerFrameListener(mVideo);
    }

    @Deprecated
    public VideoSceneObject(VrContext vrContext, Mesh mesh,
            MediaPlayer mediaPlayer, int videoType) {
        this(vrContext, mesh, mediaPlayer, VideoType.values()[videoType]);
    }

    /**
     * Play a video on a {@linkplain SceneObject scene object} with an
     * arbitrarily complex geometry, using the Android {@link MediaPlayer}
     * 
     * @param vrContext
     *            current {@link VrContext}
     * @param mesh
     *            a {@link Mesh} - see
     *            {@link VrContext#loadMesh(com.eje_c.meganekko.GVRAndroidResource)}
     *            and {@link VrContext#createQuad(float, float)}
     * @param mediaPlayer
     *            an Android {@link MediaPlayer}
     * @param videoType
     *            One of the {@linkplain VideoType video type constants}
     * @throws IllegalArgumentException
     *             on an invalid {@code videoType} parameter
     */
    public VideoSceneObject(VrContext vrContext, Mesh mesh,
            MediaPlayer mediaPlayer, VideoType videoType) {
        super(vrContext, mesh);
        ExternalTexture texture = new ExternalTexture(vrContext);

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
            try {
                texture.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new IllegalArgumentException();
        }
        Material material = new Material(vrContext, materialType);
        material.setMainTexture(texture);
        getRenderData().setMaterial(material);

        mVideo = new Video(mediaPlayer, texture);
        vrContext.registerFrameListener(mVideo);
    }

    /**
     * Play a video on a 2D, rectangular {@linkplain SceneObject scene object,}
     * using the Android {@link MediaPlayer}
     * 
     * @param vrContext
     *            current {@link VrContext}
     * @param width
     *            the rectangle's width
     * @param height
     *            the rectangle's height
     * @param mediaPlayer
     *            an Android {@link MediaPlayer}
     * @param videoType
     *            One of the {@linkplain VideoType video type constants}
     * @throws IllegalArgumentException
     *             on an invalid {@code videoType} parameter
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
     * 
     * <p>
     * This call does not directly affect the {@link MediaPlayer}. In
     * particular, activation is not the same as calling
     * {@link MediaPlayer#start()}.
     */
    public void activate() {
        mVideo.activate();
    }

    /**
     * Stop polling the {@link MediaPlayer}.
     * 
     * <p>
     * This call does not directly affect the {@link MediaPlayer}. In
     * particular, deactivation is not the same as calling
     * {@link MediaPlayer#pause()}.
     */
    public void deactivate() {
        mVideo.deactivate();
    }

    /**
     * Returns the current {@link MediaPlayer} status.
     * 
     * See {@link #activate()} and {@link #deactivate()}: polling activation is
     * not correlated with the {@code MediaPlayer} state.
     * 
     * @return Whether or not we polling the {@code MediaPlayer} every frame.
     */
    public boolean isActive() {
        return mVideo.isActive();
    }

    /**
     * Returns the current {@link MediaPlayer}, if any
     * 
     * @return current {@link MediaPlayer}
     */
    public MediaPlayer getMediaPlayer() {
        return mVideo.getMediaPlayer();
    }

    /**
     * Sets the current {@link MediaPlayer}
     * 
     * @param mediaPlayer
     *            An Android {@link MediaPlayer}
     */
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        mVideo.setMediaPlayer(mediaPlayer);
    }

    /**
     * Reset and {@link MediaPlayer#release() release()} the current
     * {@link MediaPlayer}, if any
     */
    public void release() {
        mVideo.release();
    }

    /**
     * Returns the current time stamp, in nanoseconds. This comes from
     * {@link SurfaceTexture#getTimestamp()}: you should read the Android
     * documentation on that before you use this value.
     * 
     * @return current time stamp, in nanoseconds
     */
    public long getTimeStamp() {
        return mVideo.getTimeStamp();
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

    private static class Video implements FrameListener {

        private SurfaceTexture mSurfaceTexture = null;
        private MediaPlayer mMediaPlayer = null;
        private boolean mActive = true;

        /**
         * Constructs a GVRVideo with a {@link MediaPlayer} and a
         * {@link ExternalTexture} to be used
         * 
         * @param mediaPlayer
         *            the {@link MediaPlayer} type object to be used in the
         *            class
         * @param texture
         *            the {@link ExternalTexture} type object to be used in the
         *            class
         */
        public Video(MediaPlayer mediaPlayer, ExternalTexture texture) {
            mSurfaceTexture = new SurfaceTexture(texture.getId());
            if (mediaPlayer != null) {
                setMediaPlayer(mediaPlayer);
            }
        }

        /**
         * On top of the various {@link MediaPlayer} states, this wrapper may be
         * 'active' or 'inactive'. When the wrapper is active, it updates the
         * screen each time {@link FrameListener#onDrawFrame(float)} is called;
         * when the wrapper is inactive, {@link MediaPlayer} changes do not show
         * on the screen.
         * 
         * <p>
         * Note that calling {@link #activate()} does not call
         * {@link MediaPlayer#start()}, and calling {@link #deactivate()} does
         * not call {@link MediaPlayer#pause()}.
         * 
         * @return Whether this wrapper is actively polling its
         *         {@link MediaPlayer}
         */
        public boolean isActive() {
            return mActive;
        }

        /**
         * Tell the wrapper to poll its {@link MediaPlayer} each time
         * {@link FrameListener#onDrawFrame(float)} is called.
         * 
         * <p>
         * Note that activation is not the same as calling
         * {@link MediaPlayer#start()}.
         */
        public void activate() {
            mActive = true;
        }

        /**
         * Tell the wrapper to stop polling its {@link MediaPlayer} each time
         * {@link FrameListener#onDrawFrame(float)} is called.
         * 
         * <p>
         * Note that deactivation is not the same as calling
         * {@link MediaPlayer#pause()}.
         */
        public void deactivate() {
            mActive = false;
        }

        /**
         * Returns the current {@link MediaPlayer}, if any
         * 
         * @return the current {@link MediaPlayer}
         */
        public MediaPlayer getMediaPlayer() {
            return mMediaPlayer;
        }

        /**
         * Set the {@link MediaPlayer} used to show video
         * 
         * @param mediaPlayer
         *            An Android {@link MediaPlayer}
         */
        public void setMediaPlayer(MediaPlayer mediaPlayer) {
            release();// any current MediaPlayer

            mMediaPlayer = mediaPlayer;
            Surface surface = new Surface(mSurfaceTexture);
            mMediaPlayer.setSurface(surface);
            surface.release();
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

        /**
         * Reset and {@link MediaPlayer#release() release()} the
         * {@link MediaPlayer}
         */
        public void release() {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        @Override
        public void frame() {
            if (mMediaPlayer != null && mActive) {
                mSurfaceTexture.updateTexImage();
            }
        }
    }

}
