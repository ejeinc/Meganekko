package com.eje_c.meganekko.gearvr;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.javascript.MeganekkoJSApp;

import java.io.IOException;

public class MeganekkoJSActivity extends MeganekkoActivity {

    @Override
    public MeganekkoJSApp createMeganekkoApp(Meganekko meganekko) {
        MeganekkoJSApp app = new MeganekkoJSApp(meganekko);

        // Exacute main script on global scope when specified
        String mainScriptUri = getMainScriptUri();

        if (mainScriptUri != null) {
            try {
                app.runScriptFromUri(mainScriptUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return app;
    }

    /**
     * Default behavior is to get <meta-data android:name="com.eje_c.meganekko.js.main"> value.
     * You can override this behavior to get other URI.
     *
     * @return Uri that points main script or null.
     */
    @Nullable
    protected String getMainScriptUri() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);

            if (info.metaData != null) {
                return info.metaData.getString("com.eje_c.meganekko.js.main");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(); // Probably this is not called because getComponentName() returns exactly existing component.
        }

        return null;
    }
}
