package org.meganekkovr.sampleexoplayer;

import android.media.MediaCodec;
import android.net.Uri;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

import org.meganekkovr.Entity;
import org.meganekkovr.MeganekkoApp;
import org.meganekkovr.SurfaceRendererComponent;

public class App extends MeganekkoApp {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2763.0 Safari/537.36";

    private ExoPlayer exoPlayer;

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
        DataSource dataSource = new DefaultUriDataSource(context, USER_AGENT);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(videoUrl), dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);

        TrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);

        exoPlayer.prepare(videoRenderer, audioRenderer);

        // Connect ExoPlayer output to SurfaceRendererComponent
        exoPlayer.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surfaceRenderer.getSurface());
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
