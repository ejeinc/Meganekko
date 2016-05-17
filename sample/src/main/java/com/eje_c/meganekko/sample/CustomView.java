package com.eje_c.meganekko.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomView extends View {
    // Save touched position
    private float touchX;
    private float touchY;
    private final Paint paint = new Paint();

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // set View size
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(500, 500);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paint.setColor(Color.RED);
                break;
            case MotionEvent.ACTION_MOVE:
                paint.setColor(Color.GREEN);
                break;
            case MotionEvent.ACTION_UP:
                paint.setColor(Color.BLUE);
                break;
            default:
                return super.onTouchEvent(event);
        }

        touchX = event.getX();
        touchY = event.getY();
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.argb(200, 0, 0, 0));
        canvas.drawCircle(touchX, touchY, 10, paint);
    }
}
