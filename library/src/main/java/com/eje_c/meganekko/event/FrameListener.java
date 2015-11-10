/*
 * Copyright 2015 eje inc.
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
package com.eje_c.meganekko.event;

import com.eje_c.meganekko.VrFrame;

/**
 * Implement this interface to create a callback that's called once per frame.
 *
 * @see com.eje_c.meganekko.Scene#onFrame(FrameListener)
 * @see com.eje_c.meganekko.MeganekkoActivity#onFrame(FrameListener)
 */
public interface FrameListener {
    /**
     * Called each time on frame update.
     *
     * @param vrFrame Per frame info.
     */
    void onEvent(VrFrame vrFrame);
}
