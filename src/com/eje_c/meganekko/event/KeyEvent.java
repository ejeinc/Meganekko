package com.eje_c.meganekko.event;

import java.util.EventObject;

public class KeyEvent extends EventObject {
    public final int keyCode;
    public final int repeatCount;
    private boolean mPreventDefaultCalled;

    public KeyEvent(Object source, int keyCode, int repeatCount) {
        super(source);
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
