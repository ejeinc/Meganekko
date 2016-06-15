package com.eje_c.meganekko.sample.exoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.net.Uri;
import android.view.Surface;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.SceneObject;
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

import ovr.JoyButton;

public class MyApp extends MeganekkoApp {
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2763.0 Safari/537.36";
    private SurfaceTexture surfaceTexture;
    private Surface surface;

    public MyApp(Meganekko meganekko) {
        super(meganekko);
        setSceneFromXML(R.xml.scene);

        // Get scene object
        SceneObject player = getScene().findObjectById(R.id.player);

        // Create Material for scene object
        Material material = new Material();
        player.material(material);

        // Get Surface and SurfaceTexture for rendering video
        surfaceTexture = material.getSurfaceTexture();
        surface = new Surface(surfaceTexture);

        // Create ExoPlayer in UI thread because ExoPlayer has to be created in Looper thread
        String videoUrl = getContext().getString(R.string.video_url);
        Context context = getContext();

        DataSource dataSource = new DefaultUriDataSource(context, USER_AGENT);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(videoUrl), dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);

        TrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);

        ExoPlayer exoPlayer = getExoPlayer();
        exoPlayer.prepare(videoRenderer, audioRenderer);
        exoPlayer.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void update() {

        ExoPlayer exoPlayer = getExoPlayer();

        // Toggle playing state when tapped
        if (JoyButton.contains(getFrame().getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE)) {
            exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
        }

        // Update surface texture while playing
        if (exoPlayer.getPlayWhenReady() && exoPlayer.isPlayWhenReadyCommitted()) {
            surfaceTexture.updateTexImage();
        }

        super.update();
    }

    private ExoPlayer getExoPlayer() {
        return ((MainActivity) getContext()).getExoPlayer();
    }
}
