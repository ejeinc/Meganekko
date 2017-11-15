package org.meganekkovr.sampleexoplayer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.ext.gvr.GvrAudioProcessor;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.joml.Quaternionf;
import org.meganekkovr.Entity;
import org.meganekkovr.FrameInput;
import org.meganekkovr.HeadTransform;
import org.meganekkovr.MeganekkoApp;
import org.meganekkovr.SurfaceRendererComponent;

public class App extends MeganekkoApp {

    private final GvrAudioProcessor gvrAudioProcessor = new GvrAudioProcessor();
    private final SimpleExoPlayer exoPlayer;

    public App(Context context) {

        // Constructor is called in GearVRActivity.onCreate().
        // It's safe to create SimpleExoPlayer because this is in main thread.

        // Create custom RenderersFactory
        RenderersFactory renderersFactory = new DefaultRenderersFactory(context) {
            @Override
            protected AudioProcessor[] buildAudioProcessors() {
                return new AudioProcessor[]{gvrAudioProcessor};
            }
        };

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, new DefaultTrackSelector());
    }

    @Override
    public void init() {
        super.init();

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

        // Start playing
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void update(@NonNull FrameInput frame) {

        Quaternionf quaternion = HeadTransform.getInstance().getQuaternion();
        gvrAudioProcessor.updateOrientation(quaternion.w, quaternion.x, quaternion.y, quaternion.z);

        super.update(frame);
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
