package com.eje_c.meganekko;

public interface KeyEventListener {
    boolean onKeyShortPress(int keyCode, int repeatCount);

    boolean onKeyDoubleTap(int keyCode, int repeatCount);

    boolean onKeyLongPress(int keyCode, int repeatCount);

    boolean onKeyDown(int keyCode, int repeatCount);

    boolean onKeyUp(int keyCode, int repeatCount);

    boolean onKeyMax(int keyCode, int repeatCount);
}
