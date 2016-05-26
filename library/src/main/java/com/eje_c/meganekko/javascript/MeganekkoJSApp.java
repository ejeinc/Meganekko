package com.eje_c.meganekko.javascript;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

import java.io.IOException;
import java.net.URI;

public class MeganekkoJSApp extends MeganekkoApp {

    public MeganekkoJSApp(Meganekko meganekko) {
        super(meganekko);

        try {
            ActivityInfo info = getContext().getPackageManager().getActivityInfo(((Activity) getContext()).getComponentName(), PackageManager.GET_META_DATA);
            String main = info.metaData.getString("com.eje_c.meganekko.js.main");
            if (main != null) {
                JS.execURL(URI.create(main));
            }
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}