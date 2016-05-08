package com.eje_c.meganekko.sample;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.utility.Log;

public class MyApp extends MeganekkoApp {
    private static final String TAG = "MGN";
    private FirstScene firstScene;

    public MyApp(Meganekko meganekko) {
        super(meganekko);
        setSceneFromXML(R.xml.first_scene);
    }

    /**
     * Called on every frame update.
     */
    @Override
    public void update() {
        super.update();
//        Log.d(TAG, "update");
    }

    @Override
    public void shutdown() {
        Log.d(TAG, "shutdown");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "resumed");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "paused");
    }

    /**
     * Called from {@link FirstScene}.
     */
    public void onTapButton() {
        if (getScene() instanceof FirstScene) {
            firstScene = (FirstScene) getScene();
            setSceneFromXML(R.xml.second_scene);
        }
    }

    /**
     * Called from {@link SecondScene}.
     */
    public void onBackFromSecondScene() {
        setScene(firstScene);
    }
}
