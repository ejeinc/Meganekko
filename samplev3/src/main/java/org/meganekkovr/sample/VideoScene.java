package org.meganekkovr.sample;

import android.media.MediaPlayer;

import com.eje_c.meganekko.sample3.R;

import org.meganekkovr.Entity;
import org.meganekkovr.SurfaceRendererComponent;

/**
 * This scene demonstrates playing 360 equirectangular video.
 */
public class VideoScene extends SecondScene {
    private static final String TAG = VideoScene.class.getSimpleName();
    private Entity player;
    private MediaPlayer mediaPlayer;

    @Override
    public void init() {
        super.init();

        // Get player Entity
        player = findById(R.id.player);

        // prepare MediaPlayer to playing video
        mediaPlayer = MediaPlayer.create(getApp().getContext(), R.raw.video);

        // Map MediaPlayer as texture of player Entity
        SurfaceRendererComponent surfaceRendererComponent = new SurfaceRendererComponent();
        surfaceRendererComponent.setContinuousUpdate(true);
        mediaPlayer.setSurface(surfaceRendererComponent.getSurface());

        player.add(surfaceRendererComponent);

        // Start playing
        mediaPlayer.start();
    }

    /**
     * This method will be called when other Scene is activated.
     * In this sample you cannot return to this scene. So you call {@code MediaPlayer.release()}.
     */
    @Override
    public void onStopRendering() {
        super.onStopRendering();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
