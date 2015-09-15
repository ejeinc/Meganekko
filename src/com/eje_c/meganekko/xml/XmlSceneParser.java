/* Copyright 2015 eje inc.
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

package com.eje_c.meganekko.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.eje_c.meganekko.Camera;
import com.eje_c.meganekko.GLContext;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;

import android.util.Xml;

public class XmlSceneParser {

    private final GLContext context;
    private final XmlSceneObjectParser objectParser;

    public XmlSceneParser(GLContext context) {
        this.context = context;
        this.objectParser = new XmlSceneObjectParser(context);
    }

    /**
     * Parse scene from {@code URL}. XML can be loaded any where.
     * 
     * @param in
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public Scene parse(String url, Scene scene) throws XmlPullParserException, IOException {
        return parse(new URL(url).openStream(), scene);
    }

    /**
     * Parse scene from {@code InputStream}. XML can be loaded any where.
     * 
     * @param in
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public Scene parse(InputStream in, Scene scene) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return parse(parser, scene);
        } finally {
            in.close();
        }
    }

    /**
     * Parse scene from {@code XmlPullParser}. This method can be used with
     * {@code Resources#getXml(int)}.
     * 
     * @param parser
     * @param scene
     *            can be null.
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public Scene parse(XmlPullParser parser, Scene scene) throws XmlPullParserException, IOException {

        if (scene == null) {
            scene = new Scene(context);
        }

        while (parser.next() != XmlPullParser.END_DOCUMENT) {

            switch (parser.getEventType()) {

            case XmlPullParser.START_TAG:

                if ("camera".equals(parser.getName())) {

                    Camera camera = scene.getMainCamera();

                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        SceneObject object = objectParser.parse(parser);
                        if (object != null) {
                            camera.addChildObject(object);
                        }
                    }
                } else {

                    SceneObject object = objectParser.parse(parser);

                    if (object != null) {
                        scene.addSceneObject(object);
                    }
                }
                break;

            case XmlPullParser.END_TAG:
                break;
            }
        }

        return scene;
    }

    /**
     * Set true to use asynchronous loading. If set false, textures are loaded
     * synchronically. Default is true.
     * 
     * @param useAsyncLoading
     */
    public void setAsyncTextureLoading(boolean useAsyncLoading) {
        objectParser.setAsyncTextureLoading(useAsyncLoading);
    }
}
