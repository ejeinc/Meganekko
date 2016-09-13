package org.meganekkovr;

import android.content.Context;

import org.joml.Quaternionf;

/**
 * Abstraction interface for Activity.
 */
interface MeganekkoContext {

    /**
     * Get android context.
     *
     * @return Context
     */
    Context getContext();

    /**
     * Get Meganekko application
     *
     * @return MeganekkoApp
     */
    MeganekkoApp getApp();

    /**
     * Check if user is looking at this entity.
     *
     * @param entity Target entity.
     * @return {@code true} if user is looking at it. Otherwise {@code false}.
     */
    boolean isLookingAt(Entity entity);

    /**
     * @return Head rotation.
     */
    Quaternionf getCenterViewRotation();

    /**
     * Run some code on Android UI thread.
     *
     * @param command Some code to be run in UI thread.
     */
    void runOnUiThread(Runnable command);

    /**
     * Reset forward orientation to current orientation.
     *
     * @param showBlack {@code true} to show black frame for a moment.
     */
    void recenterYaw(boolean showBlack);
}
