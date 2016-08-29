package com.eje_c.meganekko.sample.quadbinaural;

import com.eje_c.meganekko.MeganekkoApp;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

import ovr.JoyButton;

public class MyApp extends MeganekkoApp {
    private QuadBinauralPlayer player;
    private final Vector3f forward = new Vector3f(0, 0, -1);
    private final Vector3f vec = new Vector3f();
    private final float[] values = new float[3];

    @Override
    public void init() {
        super.init();
        setSceneFromXML(R.xml.scene);

        File audio = new File(getContext().getFilesDir(), "audio.ogg");
        if (!audio.exists()) {
            try {
                IoUtils.extractResource(getContext().getResources().openRawResource(R.raw.sample), audio.getName(), audio.getParentFile());
            } catch (IOException e) {
                throw new RuntimeException("Cannot extract audio file", e);
            }
        }

        player = new QuadBinauralPlayer(getContext());

        try {
            player.init();
            player.setAudioPath(audio.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Cannot setup pd", e);
        }

        player.start();
    }

    @Override
    public void update() {

        if (JoyButton.contains(getFrame().getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE)) {
            player.seekTo(0);
        }

        Quaternionf q = getScene().getViewOrientation();
        q.transform(forward, vec);

        values[0] = vec.x;
        values[1] = vec.y;
        values[2] = vec.z;

        player.setLookDirection(values);

        super.update();
    }

    @Override
    public void shutdown() {
        player.release();
        super.shutdown();
    }
}
