package com.eje_c.meganekko.event;

import java.util.EventObject;

public interface EventListener<E extends EventObject> {
    void onEvent(E event);
}
