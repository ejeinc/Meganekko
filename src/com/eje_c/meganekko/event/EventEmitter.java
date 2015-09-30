package com.eje_c.meganekko.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventEmitter {

    private final Map<String, List<EventListener<?>>> mCallbacks = new HashMap<>();

    public EventEmitter on(String name, EventListener<?> callback) {

        List<EventListener<?>> callbacks = mCallbacks.get(name);
        if (callbacks == null) {
            callbacks = new ArrayList<>();
            mCallbacks.put(name, callbacks);
        }

        callbacks.add(callback);

        return this;
    }

    public EventEmitter off(String name, EventListener<?> callback) {

        List<EventListener<?>> callbacks = mCallbacks.get(name);
        if (callbacks != null) {
            callbacks.remove(callback);
        }

        return this;
    }

    public boolean emit(String name, EventObject eventObject) {

        List<EventListener<?>> callbacks = mCallbacks.get(name);
        if (callbacks != null) {
            for (EventListener callback : callbacks) {
                callback.onEvent(eventObject);
            }
        }

        return false;
    }

}
