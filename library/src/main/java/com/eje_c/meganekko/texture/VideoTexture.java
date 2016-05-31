/*
 * Copyright 2016 eje inc.
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

package com.eje_c.meganekko.texture;

import android.media.MediaPlayer;
import android.view.Surface;

import com.eje_c.meganekko.Frame;

import java.lang.ref.WeakReference;

/**
 * Texture which renders video stream from {@link MediaPlayer}.
 */
public class VideoTexture extends Texture {
    private WeakReference<MediaPlayer> ref;
    private Surface surface;

    /**
     * Default constructor. Call {@link #setMediaPlayer(MediaPlayer)} later.
     */
    public VideoTexture() {
    }

    /**
     * Equivalent to
     * <pre>
     *     VideoTexture vt = new VideoTexture();
     *     vt.setMediaPlayer(mediaPlayer);
     * </pre>
     *
     * @param mediaPlayer MediaPlayer
     */
    public VideoTexture(MediaPlayer mediaPlayer) {
        setMediaPlayer(mediaPlayer);
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        ref = new WeakReference<>(mediaPlayer);
        setSurface(mediaPlayer);
    }

    private void setSurface(MediaPlayer mediaPlayer) {
        surface = new Surface(getSurfaceTexture());
        mediaPlayer.setSurface(surface);
    }

    @Override
    public void update(Frame vrFrame) {
        if (ref != null) {
            MediaPlayer mp = ref.get();
            if (mp != null) {
                getSurfaceTexture().updateTexImage();
            } else {
                ref = null;
            }
        }
    }

    /**
     * Release native resources.
     */
    @Override
    public void release() {

        if (ref != null) {
            MediaPlayer mp = ref.get();
            if (mp != null) {
                mp.release();
                ref.clear();
            }
        }

        if (surface != null) {
            surface.release();
            surface = null;
        }

        super.release();
    }
}
