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

/**
 * Implement this interface to respond to swipe down gesture.
 *
 * @see com.eje_c.meganekko.Scene#onSwipeDown(SwipeDownEventListener)
 * @see com.eje_c.meganekko.MeganekkoActivity#onSwipeDown()
 */
public interface SwipeDownEventListener {
    /**
     * Called when swipe down gesture is detected.
     *
     * @param event
     */
    void onEvent(SwipeDownEvent event);
}
