package com.eje_c.meganekko;

/**
 * The interface for your application.
 */
public abstract class MeganekkoApp {
    /**
     * Will be called when initialization.
     * You will call {@link Meganekko#setSceneFromXML(int)} to set rendering scene.
     *
     * @param meganekko Meganekko system
     */
    public abstract void init(Meganekko meganekko);

    /**
     * Will be called on frame update.
     * Any animations or input handlings will be implemented in it.
     *
     * @param meganekko Meganekko system
     * @param vrFrame   Frame information
     */
    public abstract void update(Meganekko meganekko, Frame vrFrame);

    /**
     * Will be called when user leaves from app.
     *
     * @param meganekko Meganekko system
     */
    public void shutdown(Meganekko meganekko) {
    }

    /**
     * Will be called when user is resumed from sleeping.
     * Note: This method is called from UI-thread. Cannot perform GL related operations.
     */
    public void onResume() {
    }

    /**
     * Will be called when device is going to sleep mode.
     * Note: This method is called from UI-thread. Cannot perform GL related operations.
     */
    public void onPause() {
    }
}
