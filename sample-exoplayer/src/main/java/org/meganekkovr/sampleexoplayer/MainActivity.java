package org.meganekkovr.sampleexoplayer;

import android.os.Bundle;

import com.google.android.exoplayer.ExoPlayer;

import org.meganekkovr.GearVRActivity;

public class MainActivity extends GearVRActivity {

    private ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ExoPlayer.Factory.newInstance() must be called in UI thread
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
