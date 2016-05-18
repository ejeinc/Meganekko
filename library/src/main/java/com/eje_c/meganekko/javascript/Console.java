package com.eje_c.meganekko.javascript;

import android.util.Log;

public class Console {
    private static final String TAG = "Meganekko.js console";

    public void log(String msg, Object... o) {
        Log.d(TAG, String.format(msg, o));
    }
}
