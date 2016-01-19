package com.eje_c.meganekko.xml.attribute_parser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Surface;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.RenderData;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.xml.XmlAttributeParser;

public class DrawableParser implements XmlAttributeParser {

    @Override
    public void parse(Context context, SceneObject object, AttributeSet attributeSet) {

        String drawable = attributeSet.getAttributeValue(NAMESPACE, "drawable");

        // alternative attribute
        if (drawable == null) {
            drawable = attributeSet.getAttributeValue(NAMESPACE, "texture");
        }

        if (drawable == null) return;

        if (drawable.startsWith("@drawable/") || drawable.startsWith("@mipmap/")) {
            int res = attributeSet.getAttributeResourceValue(NAMESPACE, "drawable", 0);
            setDrawable(object, ContextCompat.getDrawable(context, res));
        }
    }

    private void setDrawable(SceneObject object, Drawable d) {
        RenderData renderData = object.getRenderData();

        if (renderData == null) {
            renderData = new RenderData();
            object.attachRenderData(renderData);
        }

        final Material material = renderData.getMaterial();
        SurfaceTexture surfaceTexture = material.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(d.getIntrinsicWidth(), d.getIntrinsicHeight());
        Surface surface = new Surface(surfaceTexture);
        Canvas canvas = surface.lockCanvas(null);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        surface.unlockCanvasAndPost(canvas);
        surface.release();
        surfaceTexture.updateTexImage();
    }
}
