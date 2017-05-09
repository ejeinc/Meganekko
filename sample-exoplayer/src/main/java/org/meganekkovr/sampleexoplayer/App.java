package org.meganekkovr.sampleexoplayer;

import android.net.Uri;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.meganekkovr.Entity;
import org.meganekkovr.MeganekkoApp;
import org.meganekkovr.SurfaceRendererComponent;

public class App extends MeganekkoApp {

    private SimpleExoPlayer exoPlayer;

    @Override
    public void init() {
        super.init();

        // Get ExoPlayer
        // Note that ExoPlayer must be created in UI thread not in GL thread.
        MainActivity context = (MainActivity) getContext();
        exoPlayer = context.getExoPlayer();

        // Set scene
        setSceneFromXml(R.xml.scene);

        // Attach SurfaceRendererComponent to render video output to entity's surface
        SurfaceRendererComponent surfaceRenderer = new SurfaceRendererComponent();
        surfaceRenderer.setContinuousUpdate(true); // This is important!

        // Get entity
        Entity background = getScene().findById("background");

        // Set renderer
        background.add(surfaceRenderer);

        // Load video
        String videoUrl = getContext().getString(R.string.video_url);
        String userAgent = Util.getUserAgent(getContext(), "MeganekkoSample");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), userAgent);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        exoPlayer.prepare(new ExtractorMediaSource(Uri.parse(videoUrl), dataSourceFactory, extractorsFactory, null, null));

        // Connect ExoPlayer output to SurfaceRendererComponent
        exoPlayer.setVideoSurface(surfaceRenderer.getSurface());
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onHmdMounted() {
        super.onHmdMounted();
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onHmdUnmounted() {
        super.onHmdUnmounted();
        exoPlayer.setPlayWhenReady(false);
    }
}
