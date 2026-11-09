package com.github.mori01231.lifecore.event

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

abstract class CallableEvent(async: Boolean = false) : Event(async) {
    /**
     * Call the event. If the event is cancellable and is cancelled, it will return true. Otherwise, it will return false.
     * @return true if the event is cancelled, false otherwise
     */
    override fun callEvent(): Boolean {
        Bukkit.getPluginManager().callEvent(this)
        return this is Cancellable && this.isCancelled
    }
}
