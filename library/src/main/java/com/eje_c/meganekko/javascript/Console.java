package com.eje_c.meganekko.javascript;

import android.util.Log;

public class Console {
    private static final String TAG = "Meganekko.js console";

    public void log(String msg, Object... o) {
        Log.d(TAG, String.format(msg, o));
    }

    public void error(String msg, Object... o) {
        Log.e(TAG, String.format(msg, o));
    }

    public void info(String msg, Object... o) {
        Log.i(TAG, String.format(msg, o));
    }

    public void warn(String msg, Object... o) {
        Log.w(TAG, String.format(msg, o));
    }
}
