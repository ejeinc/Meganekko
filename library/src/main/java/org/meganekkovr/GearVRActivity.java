package org.meganekkovr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.oculus.vrappframework.VrActivity;

import org.meganekkovr.ovrjni.OVRApp;
import org.meganekkovr.util.ObjectFactory;

import java.lang.reflect.InvocationTargetException;

public class GearVRActivity extends VrActivity implements MeganekkoContext {

    private static final String TAG = "GearVRActivity";

    /** Load jni .so on initialization */
    static {
        Log.d(TAG, "LoadLibrary");
        System.loadLibrary("meganekko");
    }

    private MeganekkoApp app;
    private FrameInput frame;

    private static native long nativeSetAppInterface(VrActivity act, String fromPackageNameString, String commandString, String uriString);

    private static native void setClearColorBuffer(long appPtr, boolean clearColorBuffer);

    private static native boolean getClearColorBuffer(long appPtr);

    private static native void setClearColor(long appPtr, float r, float g, float b, float a);

    private static native void getClearColor(long appPtr, float[] clearColor);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create app
        app = createApp();

        if (app == null) {
            Log.e(TAG, "You have to declare <meta-data name=\"org.meganekkovr.App\" value\"YOUR_APP_CLASS_NAME\"/> or implement custom createApp() method.");
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
        LookDetector.init(appPtr);
        HeadTransform.init(appPtr);
    }

    /**
     * Create your {@link MeganekkoApp}'s instance.
     * App class can be specified with {@code <meta-data>} in AndroidManifest.xml. This is preferred way.
     * Or you can override this method to instantiate your app manually.
     *
     * @return
     */
    protected MeganekkoApp createApp() {

        String appClassName = getApplicationInfo().metaData.getString("org.meganekkovr.App", "org.meganekkovr.MeganekkoApp");

        try {
            return (MeganekkoApp) ObjectFactory.newInstance(appClassName, this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
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

        HeadTransform.getInstance().invalidate();

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
        Scene scene = app.getScene();
        collectSurfaceDefs(scene, surfacesPointer);
    }

    /**
     * Prepare for rendering.
     *
     * @param surfacesPointer {@code &res.Surfaces}
     */
    private static void collectSurfaceDefs(Entity entity, long surfacesPointer) {

        // Not visible
        if (!entity.isVisible()) return;

        // Check Entity has geometry and surface
        if (entity.isRenderable()) {
            addSurfaceDef(entity.getNativePointer(), surfacesPointer);
        }

        // Recursive for all children
        for (Entity child : entity.getChildren()) {
            collectSurfaceDefs(child, surfacesPointer);
        }
    }

    private static native void addSurfaceDef(long entityNativePtr, long surfacesPointer);

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

    /**
     * Will be removed in future update.
     *
     * @param cpuLevel CPU level
     * @deprecated Use {@link OVRApp#setCpuLevel(int)}.
     */
    public void setCpuLevel(int cpuLevel) {
        OVRApp.getInstance().setCpuLevel(cpuLevel);
    }

    /**
     * Will be removed in future update.
     *
     * @param gpuLevel GPU level
     * @deprecated Use {@link OVRApp#setGpuLevel(int)}.
     */
    public void setGpuLevel(int gpuLevel) {
        OVRApp.getInstance().setGpuLevel(gpuLevel);
    }

    public MeganekkoApp getApp() {
        return app;
    }

    public boolean getClearColorBuffer() {
        return getClearColorBuffer(getAppPtr());
    }

    public void setClearColorBuffer(boolean clearColorBuffer) {
        setClearColorBuffer(getAppPtr(), clearColorBuffer);
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
