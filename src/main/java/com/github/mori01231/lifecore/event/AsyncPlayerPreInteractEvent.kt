package com.github.mori01231.lifecore.event

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

/**
 * @deprecated misspelled
 */
@Deprecated("misspelled", ReplaceWith("AsyncPlayerPreInteractEntityEvent"))
class AsyncPlayerPreInteractEvent(val player: Player, val interactedEntity: Int) : CallableEvent(true), Cancellable {
    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList = HANDLER_LIST

    companion object {
        private val HANDLER_LIST = HandlerList()

        @Suppress("unused") // required for bukkit
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLER_LIST
    }
}
