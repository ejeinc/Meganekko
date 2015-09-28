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


/** Meganekko animations, version 2.0.
 * 
 * <p>
 * You can animate any Meganekko object's properties. By default, when you start an 
 * animation it runs once and terminates, without any need for further action
 * on your part. You can, however, specify an {@linkplain 
 * com.eje_c.meganekko.animation.Animation#setOnFinish(GVROnFinish)
 *  on-finish callback;} you can specify a {@linkplain 
 * com.eje_c.meganekko.animation.Animation#setRepeatCount(int) 
 * repeat count} and/or a {@linkplain 
 * com.eje_c.meganekko.animation.Animation#setRepeatMode(int)
 * repeat mode}; you can even specify an {@linkplain 
 * com.eje_c.meganekko.animation.OnRepeat on-repeat callback}
 * that lets you run an animation until your app reaches a desired state.
 * 
 * <p>
 * The animation engine is an optional part of Meganekko; you need to create an instance
 * of the {@linkplain com.eje_c.meganekko.animation.AnimationEngine animation 
 * engine.} See {@link 
 * com.eje_c.meganekko.animation.Animation GVRAnimation} for the details, but
 * the general use model is
 * <ul>
 * <li>{@code new} an animation, supplying the required parameters
 * <li>optionally, override any or all of the default parameters
 * <li>start the animation by calling {@linkplain 
 * com.eje_c.meganekko.animation.Animation#start(AnimationEngine) 
 * start(animationEngine)} 
 * </ul>
 * 
 * @version 2.0 
 */
package com.eje_c.meganekko.animation;