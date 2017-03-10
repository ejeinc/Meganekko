package org.meganekkovr;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Abstraction interface for Activity.
 */
interface MeganekkoContext {

    /**
     * Get android context.
     *
     * @return Context
     */
    @NonNull
    Context getContext();

    /**
     * Run some code on Android UI thread.
     *
     * @param command Some code to be run in UI thread.
     */
    void runOnUiThread(@NonNull Runnable command);
}
