/*
 * Copyright 2016 eje inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko.javascript;

import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

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
import java.net.URI;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * JavaScript engine wrapper.
 * JavaScript executing scope is related with {@link Scene}.
 */
public class JS {

    private static final Map<SceneObject, Scriptable> sCache = new WeakHashMap<>();
    private static android.content.Context sAndroidContext;
    private static Context sJSContext;
    private static Scriptable sGlobals;

    /**
     * Initialize JavaScript environment.
     *
     * @param app Meganekko app.
     */
    public static void init(@NonNull MeganekkoApp app) {

        sAndroidContext = app.getContext().getApplicationContext();

        sJSContext = Context.enter();
        sJSContext.setOptimizationLevel(-1); // disable optimization is required on Android
        sJSContext.setLanguageVersion(Context.VERSION_ES6);

        // Create global top level scope
        ScriptableObject importerTopLevel = new ImporterTopLevel(sJSContext);
        sGlobals = sJSContext.initStandardObjects(importerTopLevel);

        // Initialize global properties
        ScriptableObject.putConstProperty(sGlobals, "app", Context.javaToJS(app, sGlobals));
        ScriptableObject.putConstProperty(sGlobals, "console", Context.javaToJS(new Console(), sGlobals));
        ScriptableObject.putConstProperty(sGlobals, "http", Context.javaToJS(new Http(app), sGlobals));

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
     * Execute JavaScript on global scope. Code will be loaded from URI.
     *
     * @param uri Code URI
     * @return result.
     * @throws IOException
     */
    public static Object execURL(URI uri) throws IOException {
        return execURL(sGlobals, uri);
    }

    /**
     * Execute JavaScript on {@link Scene}. Code will be loaded from URI.
     *
     * @param scene Execution context {@link Scene}.
     * @param uri   Code URI
     * @return result.
     * @throws IOException
     */
    public static Object execURL(Scene scene, URI uri) throws IOException {
        return execURL(getScope(scene), uri);
    }

    private static Object execURL(Scriptable scope, URI uri) throws IOException {

        // asset:///assetPath
        if ("asset".equals(uri.getScheme())) {
            final String assetPath = uri.getPath().substring(1);
            return execAsset(scope, assetPath);
        }

        // res:///raw/script
        if ("res".equals(uri.getScheme())) {

            // from resource
            String resName = uri.getPath().substring(1);

            // trim extension
            if (resName.endsWith(".js")) {
                resName = resName.replace(".js", "");
            }

            int resId = sAndroidContext.getResources().getIdentifier(resName, null, sAndroidContext.getPackageName());
            return execRawResource(scope, resId);
        }

        // other URL
        return JS.exec(scope, uri.toURL().openStream());
    }

    /**
     * Execute JavaScript on {@link Scene}. Code will be loaded from raw resource.
     *
     * @param scene Execution context {@link Scene}.
     * @param resId resource id.
     * @return result
     * @throws IOException
     */
    public static Object execRawResource(Scene scene, @RawRes int resId) throws IOException {
        return execRawResource(getScope(scene), resId);
    }

    private static Object execRawResource(Scriptable scope, @RawRes int resId) throws IOException {
        InputStream inputStream = sAndroidContext.getResources().openRawResource(resId);
        return exec(scope, inputStream);
    }

    private static Object execAsset(Scriptable scope, String assetPath) throws IOException {
        InputStream inputStream = sAndroidContext.getAssets().open(assetPath);
        return exec(scope, inputStream);
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

    private static Object exec(@NonNull Scriptable scope, @NonNull String code) {
        return sJSContext.evaluateString(scope, code, "", 1, null);
    }

    private static Object exec(@NonNull Scriptable scope, @NonNull InputStream code) throws IOException {
        try (Reader reader = new InputStreamReader(code)) {
            return sJSContext.evaluateReader(scope, reader, "", 1, null);
        }
    }

    /**
     * Clear JavaScript environment.
     */
    public static void release() {
        Context.exit();
        sCache.clear();
        sJSContext = null;
    }

    /**
     * Get JavaScript part of {@link Scene}.
     *
     * @param scene Scene.
     * @return JavaScript part of Scene.
     */
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

    /**
     * Get JavaScript part of {@link SceneObject}.
     *
     * @param sceneObject SceneObject.
     * @return JavaScript part of SceneObject.
     */
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
