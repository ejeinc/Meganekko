package org.meganekkovr.sampleexoplayer;

import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import org.meganekkovr.GearVRActivity;

public class MainActivity extends GearVRActivity {

    private SimpleExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ExoPlayer.Factory.newInstance() must be called in UI thread
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
    }

    @Override
    protected void onDestroy() {
        exoPlayer.release();
        super.onDestroy();
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
