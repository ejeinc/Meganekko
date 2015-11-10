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
package com.eje_c.meganekko.xml;

import com.eje_c.meganekko.VrContext;

/**
 * Simple singleton implementation for {@link XmlSceneParser}.
 */
public class XmlSceneParserFactory {

    private static XmlSceneParserFactory instance;
    private final VrContext mVrContext;
    private XmlSceneParser mSceneParser;

    public XmlSceneParserFactory(VrContext vrContext) {
        mVrContext = vrContext;
    }

    /**
     * Get a singleton instance of {@link XmlSceneParserFactory}.
     *
     * @param vrContext
     * @return Singleton instance.
     */
    public synchronized static XmlSceneParserFactory getInstance(VrContext vrContext) {
        if (instance == null) {
            instance = new XmlSceneParserFactory(vrContext);
        }
        return instance;
    }

    /**
     * Get a singleton instance of {@link XmlSceneParser}.
     *
     * @return Singleton instance.
     */
    public synchronized XmlSceneParser getSceneParser() {
        if (mSceneParser == null) {
            mSceneParser = new XmlSceneParser(mVrContext);
        }
        return mSceneParser;
    }
}
