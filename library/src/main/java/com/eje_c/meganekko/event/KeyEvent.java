package com.eje_c.meganekko.event;

public class KeyEvent {
    public final int keyCode;
    public final int repeatCount;
    private boolean mPreventDefaultCalled;

    public KeyEvent(int keyCode, int repeatCount) {
        this.keyCode = keyCode;
        this.repeatCount = repeatCount;
    }

    public void preventDefault() {
        mPreventDefaultCalled = true;
    }

    public boolean isPreventDefaultCalled() {
        return mPreventDefaultCalled;
    }
}
