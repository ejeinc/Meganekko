package org.meganekkovr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.oculus.vrappframework.VrActivity;

import org.joml.Quaternionf;
import org.meganekkovr.ovrjni.OVRApp;
import org.meganekkovr.util.ObjectFactory;

import java.lang.reflect.InvocationTargetException;

public class GearVRActivity extends VrActivity implements MeganekkoContext {

    private static final float[] tmpValues = new float[4];
    private static final String TAG = "GearVRActivity";
    private final Quaternionf centerViewRotation = new Quaternionf();
    private MeganekkoApp app;
    private FrameInput frame;

    /** Load jni .so on initialization */
    static {
        Log.d(TAG, "LoadLibrary");
        System.loadLibrary("meganekko");
    }

    private static native long nativeSetAppInterface(VrActivity act, String fromPackageNameString, String commandString, String uriString);

    private static native boolean isLookingAt(long appPtr, long entityPointer, long geometryComponentPointer);

    private static native void getCenterViewRotation(long appPtr, float[] values);

    private static native void setClearColorBuffer(long appPtr, boolean clearColorBuffer);

    private static native boolean getClearColorBuffer(long appPtr);

    private static native void setClearColor(long appPtr, float r, float g, float b, float a);

    private static native void getClearColor(long appPtr, float[] clearColor);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create app
        String appClassName = getApplicationInfo().metaData.getString("org.meganekkovr.App", "org.meganekkovr.MeganekkoApp");
        try {
            app = (MeganekkoApp) ObjectFactory.newInstance(appClassName, this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            finish();
            return;
        }

        Intent intent = getIntent();
        String commandString = VrActivity.getCommandStringFromIntent(intent);
        String fromPackageNameString = VrActivity.getPackageStringFromIntent(intent);
        String uriString = VrActivity.getUriStringFromIntent(intent);

        // Create native GearVRActivity and get OVR::App pointer
        long appPtr = nativeSetAppInterface(this, fromPackageNameString, commandString, uriString);
        setAppPtr(appPtr);

        OVRApp.init(appPtr);
    }

    /**
     * Called from native thread.
     */
    protected void init() {
        app.setMeganekkoContext(this);
        app.init();
    }

    /**
     * Called from native thread.
     */
    protected void onHmdMounted() {
        app.onHmdMounted();
    }

    /**
     * Called from native thread.
     */
    protected void onHmdUnmounted() {
        app.onHmdUnmounted();
    }

    /**
     * Called from native thread.
     */
    protected void enteredVrMode() {
        app.enteredVrMode();
    }

    /**
     * Called from native thread.
     */
    protected void leavingVrMode() {
        app.leavingVrMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.onResume();
    }

    @Override
    protected void onPause() {
        app.onPause();
        super.onPause();
    }

    /**
     * Called from native thread.
     *
     * @param frameInputPointer native {@code ovrFrameInput}'s pointer
     */
    protected void update(long frameInputPointer) {

        if (frame == null) {
            frame = new FrameInput(frameInputPointer);
        }

        app.update(frame);

        // Clean native resources
        NativeReference.gc();
    }

    /**
     * Called from native thread.
     *
     * @param surfacesPointer {@code &ovrFrameResult.Surfaces} value.
     */
    protected void collectSurfaceDefs(long surfacesPointer) {
        app.collectSurfaceDefs(surfacesPointer);
    }

    /**
     * Called from native thread. Override this method to respond to key events.
     *
     * @param keyCode     One of {@link KeyCode} constant values.
     * @param repeatCount Repeat count.
     * @param eventType   One of {@link KeyEventType} constant values.
     * @return If event was consumed, return {@code true}. Otherwise {@code false}.
     */
    protected boolean onKeyEvent(int keyCode, int repeatCount, int eventType) {

        switch (eventType) {
            case KeyEventType.KEY_EVENT_NONE:
                return false;
            case KeyEventType.KEY_EVENT_SHORT_PRESS:
                return app.onKeyPressed(keyCode, repeatCount);
            case KeyEventType.KEY_EVENT_DOUBLE_TAP:
                return app.onKeyDoubleTapped(keyCode, repeatCount);
            case KeyEventType.KEY_EVENT_LONG_PRESS:
                return app.onKeyLongPressed(keyCode, repeatCount);
            case KeyEventType.KEY_EVENT_DOWN:
                return app.onKeyDown(keyCode, repeatCount);
            case KeyEventType.KEY_EVENT_UP:
                return app.onKeyUp(keyCode, repeatCount);
            case KeyEventType.KEY_EVENT_MAX:
                return app.onKeyMax(keyCode, repeatCount);
        }

        return false;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean isLookingAt(Entity entity) {

        if (!entity.isShown()) {
            return false;
        }

        // Check if entity has geometry
        GeometryComponent geometryComponent = entity.getComponent(GeometryComponent.class);
        if (geometryComponent == null) return false;

        return isLookingAt(getAppPtr(), entity.getNativePointer(), geometryComponent.getNativePointer());
    }

    @Override
    public Quaternionf getCenterViewRotation() {
        synchronized (this) {
            getCenterViewRotation(getAppPtr(), tmpValues);
            centerViewRotation.set(tmpValues[0], tmpValues[1], tmpValues[2], tmpValues[3]);
        }
        return centerViewRotation;
    }

    public void setCpuLevel(int cpuLevel) {
        OVRApp.getInstance().setCpuLevel(cpuLevel);
    }

    public void setGpuLevel(int gpuLevel) {
        OVRApp.getInstance().setGpuLevel(gpuLevel);
    }

    public void setShowFPS(boolean show) {
        OVRApp.getInstance().setShowFPS(show);
    }

    public void showInfoText(float duration, String fmt, Object... args) {
        OVRApp.getInstance().showInfoText(duration, String.format(fmt, args));
    }

    public void recenterYaw(boolean showBlack) {
        OVRApp.getInstance().recenterYaw(showBlack);
    }

    public MeganekkoApp getApp() {
        return app;
    }

    public void setClearColorBuffer(boolean clearColorBuffer) {
        setClearColorBuffer(getAppPtr(), clearColorBuffer);
    }

    public boolean getClearColorBuffer() {
        return getClearColorBuffer(getAppPtr());
    }

    public void setClearColor(float r, float g, float b, float a) {
        setClearColor(getAppPtr(), r, g, b, a);
    }

    public void getClearColor(float[] clearColor) {
        if (clearColor.length != 4) {
            throw new IllegalArgumentException("clearColor must be 4 element array.");
        }
        getClearColor(getAppPtr(), clearColor);
    }
}
