package org.meganekkovr.sampletbe;

import com.twobigears.TBAudioEngine.TBAudioAsset;
import com.twobigears.TBAudioEngine.TBAudioEngine;
import com.twobigears.TBAudioEngine.TBQuat;
import com.twobigears.TBAudioEngine.TBSpatDecoder;

import org.joml.Quaternionf;
import org.meganekkovr.FrameInput;
import org.meganekkovr.MeganekkoApp;

public class App extends MeganekkoApp {

    private TBSpatDecoder decoder;

    @Override
    public void init() {
        super.init();

        setSceneFromXmlAsset("scene.xml");

        // Specify sample rate, buffer size and activity context
        TBAudioEngine.init(44100, 512, getContext());
        // Start audio processing in the engine
        TBAudioEngine.start();

        // Create a new instance of TBSpatDecoder
        decoder = new TBSpatDecoder();

        // Load the asset (from the app's asset folder) and set it to be streamed
        decoder.loadAsset("Radio.tbe", TBAudioAsset.AssetLoadType.STREAMING, TBAudioAsset.AssetLocation.APP_BUNDLE);

        // Or load with absolute path. The code below loads the asset from the external storage
        // String path = new File(Environment.getExternalStorageDirectory(), "/AudioAssets/MyAudio.tbe").getAbsolutePath();
        // decoder.loadAsset(path, TBAudioAsset.AssetLoadType.STREAMING, TBAudioAsset.AssetLocation.ABSOLUTE_PATH);

        // This would ideally be called when the video needs to be
        // played back
        decoder.play();
    }

    @Override
    public void update(FrameInput frame) {

        // Update the listener orientation
        Quaternionf q = getCenterViewRotation().invert();
        TBAudioEngine.setListenerOrientation(new TBQuat(q.x, q.y, q.z, q.w));

        super.update(frame);
    }

    /**
     * This should be called when audio player is no more needed.
     */
    public void release() {

        // Destroy all components first and TBEngine LAST
        decoder.destroy();
        decoder = null;
        TBAudioEngine.destroy();
    }
}
