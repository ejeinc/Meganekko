package com.eje_c.meganekko.event;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Node.js like EventEmitter.
 */
public class EventEmitter {

    private final Map<String, List<EventHandler>> handlers = new ArrayMap<>();

    /**
     * Attach an event handler function for one or more events.
     *
     * @param eventNames   One or more space-separated event types, such as "keydown" or "touchsingle".
     * @param eventHandler A function to execute when the event is triggered.
     */
    public void on(String eventNames, EventHandler eventHandler) {

        for (String eventName : eventNames.split(" ")) {
            List<EventHandler> handlerList = handlers.get(eventName);
            if (handlerList == null) {
                handlerList = new ArrayList<>();
                handlers.put(eventName.toLowerCase(), handlerList);
            }

            handlerList.add(eventHandler);
        }
    }

    /**
     * Remove an event handler.
     *
     * @param eventNames   One or more space-separated event types.
     * @param eventHandler A handler function previously attached for the event(s).
     */
    public void off(String eventNames, EventHandler eventHandler) {

        for (String eventName : eventNames.split(" ")) {
            List<EventHandler> handlerList = handlers.get(eventName);
            if (handlerList == null) return;

            handlerList.remove(eventHandler);

            if (handlerList.isEmpty()) {
                handlers.remove(eventName);
            }
        }
    }

    /**
     * Remove an event handler.
     *
     * @param eventNames One or more space-separated event types.
     */
    public void off(String eventNames) {
        for (String eventName : eventNames.split(" ")) {
            handlers.remove(eventName);
        }
    }

    /**
     * Execute all handlers for the given event type.
     *
     * @param eventName A string containing a event type, such as touchsingle or keydown.
     * @param value     Additional parameters to pass along to the event handler.
     */
    public void emit(String eventName, Object value) {
        List<EventHandler> handlerList = handlers.get(eventName);
        if (handlerList == null) return;

        for (EventHandler handler : handlerList) {
            handler.onEvent(value);
        }
    }

    public boolean hasEventHandler(String eventName) {
        return handlers.containsKey(eventName);
    }

    public boolean isEmpty() {
        return handlers.isEmpty();
    }
}
