package com.eje_c.meganekko.gearvr;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.javascript.MeganekkoJSApp;

public class MeganekkoJSActivity extends MeganekkoActivity {
    @Override
    public MeganekkoJSApp createMeganekkoApp(Meganekko meganekko) {
        return new MeganekkoJSApp(meganekko);
    }
}
