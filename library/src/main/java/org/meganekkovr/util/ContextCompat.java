package org.meganekkovr.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * Minimum alternative implementation for android.support.v4.content.ContextCompat.
 */
public class ContextCompat {
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
