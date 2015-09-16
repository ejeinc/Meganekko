/* Copyright 2015 Samsung Electronics Co., LTD
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

/**
 * Implement this interface to create a callback that's called once per frame.
 * 
 * Activate your callbacks with
 * {@link MeganekkoActivity#registerFrameListener(GVRDrawFrameListener)};
 * deactivate a callback with
 * {@link MeganekkoActivity#unregisterFrameListener(GVRDrawFrameListener)}.
 * Per-frame callbacks are called after the
 * {@linkplain MeganekkoActivity#runOnGlThread(Runnable) 'one shot' queue} has
 * been called and before {@link MeganekkoActivity#frame()}.
 * 
 * @deprecated Use {@code OnFrameListener} and
 *             {@code Scene#addOnFrameListener(OnFrameListener)}
 */
public interface FrameListener {
    /**
     * Called each time a frame is drawn. Callbacks are called (from
     * {@link MeganekkoActivity#frame(long)}) in subscription-order, after any
     * {@linkplain GLContext#runOnGlThread(Runnable) 'one shot' callbacks} and
     * before {@link MeganekkoActivity#frame()}.
     */
    public void frame();
}
