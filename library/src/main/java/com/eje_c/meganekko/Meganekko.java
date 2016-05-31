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
