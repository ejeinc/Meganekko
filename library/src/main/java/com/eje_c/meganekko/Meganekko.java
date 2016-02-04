package com.eje_c.meganekko;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * The interface to Meganekko Framework system.
 * Currently implementation is only MeganekkoActivity.
 */
public interface Meganekko {

    MeganekkoApp createMeganekkoApp(Meganekko meganekko);

    /**
     * Parse
     *
     * @param xmlRes
     */
    void setSceneFromXML(int xmlRes);

    /**
     * @return Current {@link Scene}.
     */
    Scene getScene();

    /**
     * Set {@link Scene}.
     *
     * @param scene
     */
    void setScene(Scene scene);

    /**
     * Call this when background thread or GL-thread wants to notify callback on UI-thread.
     *
     * @param runnable Runnable wants to be run on UI-thread.
     */
    void runOnUiThread(Runnable runnable);

    /**
     * Reset head tracking forward direction.
     * Currently, it works only while device is attached to Gear VR.
     */
    void recenter();

    /**
     * @return Current {@code Context}
     */
    Context getContext();

    /**
     * @param object Target object.
     * @return If user is looking at object, return true. Otherwise false.
     */
    boolean isLookingAt(SceneObject object);
}
