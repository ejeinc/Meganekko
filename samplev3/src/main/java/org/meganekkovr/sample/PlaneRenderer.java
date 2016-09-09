package org.meganekkovr.sample;

import android.graphics.Canvas;
import android.graphics.Color;

import org.meganekkovr.SurfaceRendererComponent;

public class PlaneRenderer extends SurfaceRendererComponent.CanvasRenderer {
    int color = Color.YELLOW;

    // Simple color texture requires only 1x1 pixel
    public PlaneRenderer() {
        super(1, 1);
    }

    @Override
    protected boolean render(Canvas canvas) {
        canvas.drawColor(color);

        // It tells this renderer requires continually drawing.
        return false;
    }
}