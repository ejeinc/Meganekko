package org.meganekkovr.samplevideo;

import android.media.MediaPlayer;

import org.meganekkovr.Entity;
import org.meganekkovr.MeganekkoApp;
import org.meganekkovr.SurfaceRendererComponent;

public class App extends MeganekkoApp {

    private MediaPlayer mediaPlayer;

    @Override
    public void init() {
        super.init();

        setSceneFromXmlAsset("scene.xml");
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.video);

        // Attach SurfaceRendererComponent to render video output to entity's surface
        SurfaceRendererComponent surfaceRenderer = new SurfaceRendererComponent();
        surfaceRenderer.setContinuousUpdate(true); // This is important!

        // Connect output from MediaPlayer to SurfaceRendererComponent
        mediaPlayer.setSurface(surfaceRenderer.getSurface());

        // Query entity
        Entity background = getScene().findById("background");
        background.add(surfaceRenderer);

        mediaPlayer.start();
    }

    @Override
    public void onHmdMounted() {
        super.onHmdMounted();

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onHmdUnmounted() {
        super.onHmdUnmounted();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
}
