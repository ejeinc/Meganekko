package com.eje_c.meganekko.sample;

import android.media.MediaPlayer;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;

import ovr.KeyCode;

public class VideoScene extends Scene {
    private SceneObject player;
    private MediaPlayer mediaPlayer;

    @Override
    protected void initialize(MeganekkoApp app) {
        super.initialize(app);
        player = findObjectById(R.id.player);
        mediaPlayer = MediaPlayer.create(app.getContext(), R.raw.video);
        player.material(Material.from(mediaPlayer));
        mediaPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, int repeatCount) {
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, repeatCount);
    }

    @Override
    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            MyApp app = (MyApp) getApp();
            app.returnToHome();
            return true;
        }
        return super.onKeyShortPress(keyCode, repeatCount);
    }
}
