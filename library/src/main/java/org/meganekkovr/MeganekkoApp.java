package org.meganekkovr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joml.Quaternionf;
import org.meganekkovr.ovrjni.OVRApp;
import org.meganekkovr.xml.XmlParser;
import org.meganekkovr.xml.XmlParserException;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MeganekkoApp {

    private static final String TAG = "MeganekkoApp";
    private final Queue<Runnable> commands = new LinkedBlockingQueue<>();
    private Scene scene;
    private MeganekkoContext context;
    private long glThreadId;
    private XmlParser xmlParser;

    /**
     * Called at app is launching. Override this to implement custom initialization.
     * If you override this method, you must call {@code super.init()}.
     */
    public void init() {
        glThreadId = Thread.currentThread().getId();
    }

    /**
     * Called at every frame update. It will be called about 60 times per frame.
     * If you override this method, you must call {@code super.update(frame)}.
     *
     * @param frame Frame information
     */
    public void update(@NonNull FrameInput frame) {

        // runOnGlThread handling
        while (!commands.isEmpty()) {
            commands.poll().run();
        }

        if (scene != null) {
            scene.update(frame);
        }
    }

    @Nullable
    public Scene getScene() {
        return scene;
    }

    public void setScene(@Nullable Scene scene) {
        assertGlThread();

        Scene currentScene = this.scene;
        if (currentScene != null) {
            currentScene.onStopRendering();
        }

        this.scene = scene;

        if (scene != null) {
            scene.setApp(this);
            scene.onStartRendering();
        }
    }

    /**
     * For internal use only.
     *
     * @param context
     */
    void setMeganekkoContext(@NonNull MeganekkoContext context) {
        this.context = context;
    }

    /**
     * Get {@link Context}.
     *
     * @return context
     */
    public Context getContext() {
        return context.getContext();
    }

    /**
     * Enqueue command that must run in GL thread. This command will be executed at next update.
     *
     * @param command Command
     */
    public void runOnGlThread(@NonNull Runnable command) {
        commands.add(command);
    }

    public void runOnUiThread(@NonNull Runnable command) {
        context.runOnUiThread(command);
    }

    private void assertGlThread() {
        if (Thread.currentThread().getId() != glThreadId) {
            throw new IllegalStateException("This operation must be in GL Thread");
        }
    }

    public boolean onKeyPressed(int keyCode, int repeatCount) {
        return scene.onKeyPressed(keyCode, repeatCount);
    }

    public boolean onKeyDoubleTapped(int keyCode, int repeatCount) {
        return scene.onKeyDoubleTapped(keyCode, repeatCount);
    }

    public boolean onKeyLongPressed(int keyCode, int repeatCount) {
        return scene.onKeyLongPressed(keyCode, repeatCount);
    }

    public boolean onKeyDown(int keyCode, int repeatCount) {
        return scene.onKeyDown(keyCode, repeatCount);
    }

    public boolean onKeyUp(int keyCode, int repeatCount) {
        return scene.onKeyUp(keyCode, repeatCount);
    }

    public boolean onKeyMax(int keyCode, int repeatCount) {
        return scene.onKeyMax(keyCode, repeatCount);
    }

    /**
     * Check if user is looking at this entity.
     *
     * @param entity Target {@link Entity}.
     * @return {@code true} if user is looking at. Otherwise {@code false}.
     * @deprecated Use {@link LookDetector#isLookingAt(Entity)}.
     */
    public boolean isLookingAt(@NonNull Entity entity) {
        return LookDetector.getInstance().isLookingAt(entity);
    }

    /**
     * Note that returned value is only valid in a frame.
     * This object will be updated in the future at {@link #getCenterViewRotation()} will be called.
     * If you want to save this value over frames, copy it manually.
     *
     * @return center view rotation
     * @deprecated Use {@link HeadTransform#getQuaternion()}.
     */
    @NonNull
    public Quaternionf getCenterViewRotation() {
        return HeadTransform.getInstance().getQuaternion();
    }

    /**
     * Get an {@link XmlParser}.
     *
     * @return XmlParser
     */
    @NonNull
    public synchronized XmlParser getXmlParser() {
        if (xmlParser == null) {
            xmlParser = createXmlParser(getContext());
        }
        return xmlParser;
    }

    /**
     * Instantiate {@link XmlParser}. Called at first time with {@link #getXmlParser()}.
     *
     * @param context context
     * @return new instance of XmlParser.
     */
    @NonNull
    protected XmlParser createXmlParser(@NonNull Context context) {
        return new XmlParser(context);
    }

    @NonNull
    public Scene setSceneFromXmlAsset(@NonNull String assetName) {
        try {
            Entity entity = getXmlParser().parseAsset(assetName);
            if (entity instanceof Scene) {
                Scene scene = (Scene) entity;
                setScene(scene);
                return scene;
            } else {
                throw new IllegalArgumentException("XML first element must be <scene>.");
            }
        } catch (XmlParserException e) {
            throw new RuntimeException("Cannot parse XML from " + assetName, e);
        }
    }

    @NonNull
    public Scene setSceneFromXml(@NonNull String uri) {
        try {
            Entity entity = getXmlParser().parseUri(uri);
            if (entity instanceof Scene) {
                Scene scene = (Scene) entity;
                setScene(scene);
                return scene;
            } else {
                throw new IllegalArgumentException("XML first element must be <scene>.");
            }
        } catch (XmlParserException e) {
            throw new RuntimeException("Cannot parse XML from " + uri, e);
        }
    }

    @NonNull
    public Scene setSceneFromXml(int xmlRes) {
        try {
            Entity entity = getXmlParser().parseXmlResource(xmlRes);
            if (entity instanceof Scene) {
                Scene scene = (Scene) entity;
                setScene(scene);
                return scene;
            } else {
                throw new IllegalArgumentException("XML first element must be <scene>.");
            }
        } catch (XmlParserException e) {
            throw new RuntimeException("Cannot parse XML from " + getContext().getResources().getResourceName(xmlRes), e);
        }
    }

    @NonNull
    public Scene setSceneFromXml(@NonNull File file) {
        try {
            Entity entity = getXmlParser().parseFile(file);
            if (entity instanceof Scene) {
                Scene scene = (Scene) entity;
                setScene(scene);
                return scene;
            } else {
                throw new IllegalArgumentException("XML first element must be <scene>.");
            }
        } catch (XmlParserException e) {
            throw new RuntimeException("Cannot parse XML from " + file, e);
        }
    }

    /**
     * Run {@link Animator} on UI thread and notify end callback on GL thread.
     *
     * @param anim        {@link Animator}.
     * @param endCallback Callback for animation end. This is <b>not</b> called when animation is canceled.
     *                    If you require more complicated callbacks, use {@code AnimatorListener} instead of this.
     */
    public void animate(@NonNull final Animator anim, @Nullable final Runnable endCallback) {

        if (anim.isRunning()) {
            cancel(anim, new Runnable() {
                @Override
                public void run() {
                    animate(anim, endCallback);
                }
            });
            return;
        }

        // Register one time animation end callback
        if (endCallback != null) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    anim.removeListener(this);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    anim.removeListener(this);
                    runOnGlThread(endCallback);
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                anim.start();
            }
        });
    }

    /**
     * Cancel {@link Animator} running.
     *
     * @param anim     {@link Animator}.
     * @param callback Callback for canceling operation was called in UI thread.
     */
    public final void cancel(@NonNull final Animator anim, @Nullable final Runnable callback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                anim.cancel();
                if (callback != null) runOnGlThread(callback);
            }
        });
    }

    public void onHmdMounted() {
    }

    public void onHmdUnmounted() {
    }

    /**
     * This is called when Activity is resumed.
     * Note that it is called on Android's main thread.
     * If you do something with GL related tasks, use {@link #runOnGlThread(Runnable)}.
     */
    public void onResume() {
    }

    /**
     * This is called when Activity is paused.
     * Note that it is called on Android's main thread.
     * If you do something with GL related tasks, use {@link #runOnGlThread(Runnable)}.
     */
    public void onPause() {
    }

    /**
     * Will be removed in future update.
     *
     * @param showBlack Show black
     * @deprecated Use {@link OVRApp#recenterYaw(boolean)}.
     */
    public void recenterYaw(boolean showBlack) {
        OVRApp.getInstance().recenterYaw(showBlack);
    }

    /**
     * This will be called right after entering VR mode.
     * This can be considered as GL version of {@link Activity#onResume()}.
     */
    public void enteredVrMode() {
    }

    /**
     * This will be called right before leaving VR mode.
     * This can be considered as GL version of {@link Activity#onPause()}.
     */
    public void leavingVrMode() {
    }
}
