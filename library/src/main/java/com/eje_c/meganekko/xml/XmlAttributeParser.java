package com.eje_c.meganekko.xml;

import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;

import org.xmlpull.v1.XmlPullParserException;

public interface XmlAttributeParser {

    // Reserved for future update
    String NAMESPACE = null;

    void parse(SceneObject object, AttributeSet attributeSet) throws XmlPullParserException;
}
