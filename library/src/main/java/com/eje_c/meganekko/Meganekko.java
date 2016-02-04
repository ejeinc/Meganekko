package com.eje_c.meganekko;

import android.content.Context;

/**
 * The interface to Meganekko Framework system.
 * Currently implementation is only MeganekkoActivity.
 */
public interface Meganekko {

    MeganekkoApp createMeganekkoApp(Meganekko meganekko);

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
}
