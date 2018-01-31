package org.meganekkovr.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes

/**
 * Minimum alternative implementation for android.support.v4.content.ContextCompat.
 */
object ContextCompat {

    @JvmStatic
    fun getDrawable(context: Context, @DrawableRes id: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= 21) {
            context.getDrawable(id)
        } else {
            context.resources.getDrawable(id)
        }
    }
}
