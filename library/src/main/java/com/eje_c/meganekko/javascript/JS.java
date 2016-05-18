package com.eje_c.meganekko.javascript;

import android.support.annotation.NonNull;

import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.event.EventHandler;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * JavaScript engine wrapper.
 * JavaScript executing scope is related with {@link Scene}.
 */
public class JS {

    private static final Map<SceneObject, Scriptable> sCache = new WeakHashMap<>();
    private static Context sJSContext;
    private static Scriptable sGlobals;

    /**
     * Initialize JavaScript environment.
     *
     * @param app Meganekko app.
     */
    public static void init(@NonNull MeganekkoApp app) {

        sJSContext = Context.enter();
        sJSContext.setOptimizationLevel(-1); // disable optimization is required on Android
        sJSContext.setLanguageVersion(Context.VERSION_ES6);

        // Create global top level scope
        ScriptableObject importerTopLevel = new ImporterTopLevel(sJSContext);
        sGlobals = sJSContext.initStandardObjects(importerTopLevel);

        // Initialize global properties
        ScriptableObject.putConstProperty(sGlobals, "app", Context.javaToJS(app, sGlobals));
        ScriptableObject.putConstProperty(sGlobals, "console", Context.javaToJS(new Console(), sGlobals));

        // import global classes
        sJSContext.evaluateString(sGlobals, "importPackage(Packages.org.joml)", "", 1, null);
        sJSContext.evaluateString(sGlobals, "importClass(Packages.ovr.JoyButton)", "", 1, null);
        sJSContext.evaluateString(sGlobals, "importClass(Packages.ovr.KeyCode)", "", 1, null);
        sJSContext.evaluateString(sGlobals, "importClass(Packages.com.eje_c.meganekko.Material)", "", 1, null);
        sJSContext.evaluateString(sGlobals, "importClass(Packages.com.eje_c.meganekko.Mesh)", "", 1, null);
        sJSContext.evaluateString(sGlobals, "importClass(Packages.com.eje_c.meganekko.Scene)", "", 1, null);
        sJSContext.evaluateString(sGlobals, "importClass(Packages.com.eje_c.meganekko.SceneObject)", "", 1, null);
        sJSContext.evaluateString(sGlobals, "importPackage(Packages." + app.getContext().getPackageName() + ")", "", 1, null); // for R
    }

    /**
     * Execute JavaScript code on {@link Scene}.
     *
     * @param scene Scene
     * @param code  JavaScript code.
     * @return result.
     */
    public static Object exec(@NonNull Scene scene, @NonNull String code) {
        return exec(getScope(scene), code);
    }

    /**
     * Execute JavaScript code on {@link Scene}.
     *
     * @param scene Scene
     * @param code  Streamed JavaScript code.
     * @return result.
     * @throws IOException
     */
    public static Object exec(@NonNull Scene scene, @NonNull InputStream code) throws IOException {
        Scriptable scope = getScope(scene);
        try (Reader reader = new InputStreamReader(code)) {
            return sJSContext.evaluateReader(scope, reader, "", 1, null);
        }
    }

    private static Object exec(@NonNull Scriptable scope, @NonNull String code) {
        return sJSContext.evaluateString(scope, code, "", 1, null);
    }

    /**
     * Clear JavaScript environment.
     */
    public static void release() {
        Context.exit();
        sCache.clear();
        sJSContext = null;
    }

    private static Scriptable getScope(@NonNull Scene scene) {
        Scriptable scope = sCache.get(scene);
        if (scope == null) {
            scope = sJSContext.initStandardObjects();
            scope.setParentScope(sGlobals);
            ScriptableObject.putConstProperty(scope, "scene", scene);
            sCache.put(scene, scope);
        }
        return scope;
    }

    private static Scriptable getJSBinding(SceneObject sceneObject) {
        Scriptable jsBinding = sCache.get(sceneObject);
        if (jsBinding == null) {
            jsBinding = new NativeJavaObject(sGlobals, sceneObject, sceneObject.getClass());
            jsBinding.setParentScope(getScope(sceneObject.getScene()));
            sCache.put(sceneObject, jsBinding);
        }
        return jsBinding;
    }

    /**
     * Create {@link EventHandler} from JavaScript code.
     *
     * @param object {@code this} object.
     * @param code   JavaScript code.
     * @return New EventHandler.
     */
    public static EventHandler createEventHandler(final SceneObject object, final String code) {
        return new EventHandler() {
            Function function;
            Object[] args = new Object[1];

            @Override
            public void onEvent(Object event) {
                args[0] = event;

                Scriptable jsBinding = getJSBinding(object);

                // Compile code at first time
                if (function == null) {
                    function = sJSContext.compileFunction(jsBinding, "function(event) {" + code + "}", "", 1, null);
                }

                function.call(sJSContext, sGlobals, jsBinding, args);
            }
        };
    }
}
