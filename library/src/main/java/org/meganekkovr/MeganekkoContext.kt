package org.meganekkovr

import android.content.Context

/**
 * Abstraction interface for Activity.
 */
interface MeganekkoContext {

    /**
     * Get android context.
     *
     * @return Context
     */
    val context: Context

    /**
     * Run some code on Android UI thread.
     *
     * @param command Some code to be run in UI thread.
     */
    fun runOnUiThread(command: Runnable)
}
