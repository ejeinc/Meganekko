package com.eje_c.meganekko;

public class KeyEventListenerAdapter implements KeyEventListener {
    @Override
    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        return false;
    }

    @Override
    public boolean onKeyDoubleTap(int keyCode, int repeatCount) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, int repeatCount) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, int repeatCount) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, int repeatCount) {
        return false;
    }

    @Override
    public boolean onKeyMax(int keyCode, int repeatCount) {
        return false;
    }
}
