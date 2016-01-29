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

    MeganekkoApp createMeganekkoApp();

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
     * Call this when background thread or UI-thread wants to notify callback on GL-thread.
     *
     * @param runnable Runnable wants to be run on GL-thread.
     */
    void runOnGlThread(Runnable runnable);

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
     * Call this to run {@code Animator} on UI-thread.
     *
     * @param anim        Animator
     * @param endCallback Optional callback. This will called on GL-thread after animation.
     */
    void animate(@NonNull final Animator anim, @Nullable final Runnable endCallback);

    /**
     * Call this to cancel {@code Animator} on UI-thread.
     *
     * @param anim        Animator
     * @param endCallback Optional callback. This will called on GL-thread after cancel.
     */
    void cancel(@NonNull final Animator anim, @Nullable final Runnable endCallback);

    /**
     * @return Current {@code Context}
     */
    Context getContext();

    /**
     * @return {@link Frame}
     */
    Frame getFrame();

    /**
     * @param object Target object.
     * @return If user is looking at object, return true. Otherwise false.
     */
    boolean isLookingAt(SceneObject object);
}
