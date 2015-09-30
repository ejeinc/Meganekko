package com.eje_c.meganekko.xml;

import com.eje_c.meganekko.VrContext;

public class XmlSceneParserFactory {

    private static XmlSceneParserFactory instance;
    private final VrContext mVrContext;
    private XmlSceneParser mSceneParser;

    public XmlSceneParserFactory(VrContext vrContext) {
        mVrContext = vrContext;
    }

    public synchronized static XmlSceneParserFactory getInstance(VrContext vrContext) {
        if (instance == null) {
            instance = new XmlSceneParserFactory(vrContext);
        }
        return instance;
    }

    public synchronized XmlSceneParser getSceneParser() {
        if (mSceneParser == null) {
            mSceneParser = new XmlSceneParser(mVrContext);
        }
        return mSceneParser;
    }
}
