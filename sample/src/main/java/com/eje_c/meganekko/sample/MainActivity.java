package com.eje_c.meganekko.sample;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.gearvr.MeganekkoActivity;

/**
 * Meganekko's entry point.
 */
public class MainActivity extends MeganekkoActivity {
    @Override
    public MeganekkoApp createMeganekkoApp(Meganekko meganekko) {
        return new MyApp(meganekko);
    }
}
