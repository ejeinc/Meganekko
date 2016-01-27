package com.eje_c.meganekko.sample;

import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.gearvr.MeganekkoActivity;

public class MainActivity extends MeganekkoActivity {
    @Override
    public MeganekkoApp createMeganekkoApp() {
        return new MyApp();
    }
}
