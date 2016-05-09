package com.eje_c.meganekko.sample;

import android.media.MediaPlayer;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.utility.Log;

/**
 * This scene demonstrates playing 360 equirectangular video.
 */
public class VideoScene extends SecondScene {
    private static final String TAG = VideoScene.class.getSimpleName();
    private SceneObject player;
    private MediaPlayer mediaPlayer;

    @Override
    protected void initialize(MeganekkoApp app) {
        super.initialize(app);

        // Get player SceneObject
        player = findObjectById(R.id.player);

        // prepare MediaPlayer to playing video
        mediaPlayer = MediaPlayer.create(app.getContext(), R.raw.video);

        // Map MediaPlayer as texture of player SceneObject
        player.material(Material.from(mediaPlayer));

        // Start playing
        mediaPlayer.start();
    }

    /**
     * This method will be called when other {@link com.eje_c.meganekko.Scene Scene} is activated.
     * In this sample you cannot return to this scene. So you should call {@code MediaPlayer.release()}.
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause was called.");

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
