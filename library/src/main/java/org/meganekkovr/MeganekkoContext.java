package org.meganekkovr;

import android.content.Context;

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
     * Run some code on Android UI thread.
     *
     * @param command Some code to be run in UI thread.
     */
    void runOnUiThread(Runnable command);
}
