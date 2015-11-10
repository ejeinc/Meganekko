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
 * Implement this interface to respond to key long press event.
 *
 * @see com.eje_c.meganekko.Scene#onKeyLongPress(KeyLongPressEventListener)
 * @see com.eje_c.meganekko.MeganekkoActivity#onKeyLongPress(int, int)
 */
public interface KeyLongPressEventListener {
    /**
     * Called when key long press event is occurred.
     *
     * @param event Key event. Call {@link KeyEvent#preventDefault()} to cancel default behavior.
     */
    void onEvent(KeyLongPressEvent event);
}
