package org.meganekkovr;

import android.content.Context;

import org.joml.Quaternionf;

/**
 * Abstraction interface for Activity.
 */
interface MeganekkoContext {
    Context getContext();

    boolean isLookingAt(Entity entity);

    Quaternionf getCenterViewRotation();

    void runOnUiThread(Runnable command);

    void recenterYaw(boolean showBlack);
}
