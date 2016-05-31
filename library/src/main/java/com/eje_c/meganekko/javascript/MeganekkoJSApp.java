package com.eje_c.meganekko.javascript;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

import java.io.IOException;
import java.net.URI;

public class MeganekkoJSApp extends MeganekkoApp {

    public MeganekkoJSApp(Meganekko meganekko) {
        super(meganekko);
    }

    public void runScriptFromUri(String uri) throws IOException {
        JS.execURL(URI.create(uri));
    }
}