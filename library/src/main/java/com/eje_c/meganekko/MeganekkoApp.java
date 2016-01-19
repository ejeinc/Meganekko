package com.eje_c.meganekko;

public interface MeganekkoApp {
    void init(Meganekko meganekko);

    void update(Meganekko meganekko, VrFrame vrFrame);

    void shutdown(Meganekko meganekko);

    void onResume();

    void onPause();
}
