package org.meganekkovr;

public class Scene extends Entity {
    private static final String TAG = "Scene";
    private boolean initialized;

    /**
     * Called before first rendering.
     */
    public void init() {
    }

    /**
     * Called when this Scene is activated by {@link MeganekkoApp#setScene(Scene)}.
     * If you override this method, you must call {@code super.onStartRendering()}.
     */
    public void onStartRendering() {
        if (!initialized) {
            init();
            initialized = true;
        }
    }

    /**
     * Called when other Scene is activated by {@link MeganekkoApp#setScene(Scene)}.
     */
    public void onStopRendering() {
    }

    public boolean onKeyPressed(int keyCode, int repeatCount) {
        return false;
    }

    public boolean onKeyDoubleTapped(int keyCode, int repeatCount) {
        return false;
    }

    public boolean onKeyLongPressed(int keyCode, int repeatCount) {
        return false;
    }

    public boolean onKeyDown(int keyCode, int repeatCount) {
        return false;
    }

    public boolean onKeyUp(int keyCode, int repeatCount) {
        return false;
    }

    public boolean onKeyMax(int keyCode, int repeatCount) {
        return false;
    }
}
