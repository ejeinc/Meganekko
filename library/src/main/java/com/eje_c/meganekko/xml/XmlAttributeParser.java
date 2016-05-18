package com.eje_c.meganekko.xml;

import android.content.Context;
import android.util.AttributeSet;

import com.eje_c.meganekko.SceneObject;

import org.xmlpull.v1.XmlPullParserException;

@Deprecated
public interface XmlAttributeParser {

    // Reserved for future update
    String NAMESPACE = null;

    void parse(Context context, SceneObject object, AttributeSet attributeSet) throws XmlPullParserException;
}
