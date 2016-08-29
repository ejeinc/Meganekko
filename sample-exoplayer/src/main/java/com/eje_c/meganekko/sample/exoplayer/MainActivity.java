package com.eje_c.meganekko.sample.exoplayer;

import android.os.Bundle;

import com.eje_c.meganekko.gearvr.MeganekkoActivity;
import com.google.android.exoplayer.ExoPlayer;

public class MainActivity extends MeganekkoActivity {
    private ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exoPlayer = ExoPlayer.Factory.newInstance(2);
    }

    @Override
    protected void onDestroy() {
        exoPlayer.release();
        super.onDestroy();
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
