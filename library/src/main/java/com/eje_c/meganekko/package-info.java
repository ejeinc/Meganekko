/*
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
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


/**
 * Main classes for Meganekko.
 * <p/>
 * You have to extend {@link com.eje_c.meganekko.gearvr.MeganekkoActivity} instead of {@code Activity},
 * and implement {@link com.eje_c.meganekko.gearvr.MeganekkoActivity#createMeganekkoApp()}
 * and return your own {@link com.eje_c.meganekko.MeganekkoApp}.
 * You don't need to override {@code onCreate} typically.
 * <p/>
 * A {@linkplain com.eje_c.meganekko.Scene scene graph} contains any number of {@linkplain com.eje_c.meganekko.SceneObject scene objects.}
 * Scene objects have an {@linkplain com.eje_c.meganekko.Transform 4x4 matrix} which positions them in the scene.
 * Each scene object can have child scene objects: moving the parent moves the children,
 * maintaining the children's positions with relation to each other.
 * <p/>
 * To be visible, a scene object must have {@linkplain com.eje_c.meganekko.RenderData render data},
 * including a {@linkplain com.eje_c.meganekko.Mesh GL mesh} which defines the surface geometry and
 * a {@linkplain com.eje_c.meganekko.Material material} which defines the surface appearance by specifying a GL shader and its parameters.
 * <p/>
 * <a name="scenegraph"><h3>Scene Graph</h3></a>
 * <p/>
 * A scene graph is created from Java code or XML. XML is preferable choice but two approaches are used properly in cases.
 * The relation of XML and Java are similar to HTML and JavaScript. XML represents a static state of scene and Java will change objects in scene dynamically.
 * <p/>
 * A simple scene graph example:
 * <pre>
 *     &lt;scene&gt;
 *       &lt;view
 *         position="0.0 0.0 -5.0"
 *         layout="@layout/hello_world" /&gt;
 *     &lt;/scene&gt;
 * </pre>
 * Put a file at {@code res/xml/scene.xml} and call {@link com.eje_c.meganekko.Meganekko#setSceneFromXML(int)}.
 */
package com.eje_c.meganekko;