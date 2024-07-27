package com.github.mori01231.lifecore.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CallableEvent extends Event {
    public CallableEvent(boolean async) {
        super(async);
    }

    public CallableEvent() {
        this(false);
    }

    /**
     * Call the event. If the event is cancellable and is cancelled, it will return true. Otherwise, it will return false.
     * @return true if the event is cancelled, false otherwise
     */
    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        return this instanceof Cancellable && ((Cancellable) this).isCancelled();
    }
}
