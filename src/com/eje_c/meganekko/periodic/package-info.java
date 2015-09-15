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

/** Schedule Runnables to run on the GL thread, in the future.
 * 
 * This is an optional component of GVRF: You need to {@linkplain 
 * com.eje_c.meganekko.periodic.PeriodicEngine#getInstance(com.eje_c.meganekko.GVRContext)
 * get an instance} to use it.
 * 
 * <p>
 * You can then schedule {@linkplain 
 * com.eje_c.meganekko.periodic.PeriodicEngine#runAfter(Runnable, float) 
 * run-once events} and periodic events. Periodic events {@linkplain 
 * com.eje_c.meganekko.periodic.PeriodicEngine#runEvery(Runnable, float, float, com.eje_c.meganekko.periodic.PeriodicEngine.KeepRunning)
 * may} or {@linkplain 
 * com.eje_c.meganekko.periodic.PeriodicEngine#runEvery(Runnable, float, float) 
 * may not} have a {@linkplain 
 * com.eje_c.meganekko.periodic.PeriodicEngine.KeepRunning callback.}
 * 
 * <p>
 * Scheduling an event gives you {@linkplain 
 * com.eje_c.meganekko.periodic.PeriodicEngine.PeriodicEvent 
 * an interface} you can use to check on the event's status and/or to change its
 * scheduling.
 */
package com.eje_c.meganekko.periodic;