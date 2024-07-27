package com.github.mori01231.lifecore.event

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a player tries to update a sign.
 * @param player The player who tried to update the sign.
 * @param pos The position of the sign.
 * @param lines The lines of the sign.
 */
data class AsyncPreSignChangeEvent(
    val player: Player,
    val pos: Location,
    val lines: List<String>,
) : CallableEvent(true), Cancellable {
    private var cancelled = false

    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        @get:JvmName("getHandlerList")
        val handlerList = HandlerList()
    }

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }
}
