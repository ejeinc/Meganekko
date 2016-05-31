/*
 * Copyright 2016 eje inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko.javascript;

import android.util.Log;

/**
 * JavaScript <code>console</code> module.
 * <pre>
 *     console.log('hello world');
 *     console.error('hello error');
 *     console.info('info!');
 *     console.warn('warn!!');
 * </pre>
 */
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
