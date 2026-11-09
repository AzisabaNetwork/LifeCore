package com.github.mori01231.lifecore.listener

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

@Suppress("DEPRECATION")
class CancelJoinAfterStartupListener : Listener {
    @EventHandler
    fun onAsyncPlayerPreLogin(e: AsyncPlayerPreLoginEvent) {
        if (Bukkit.getCurrentTick() < TICKS) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, MESSAGE)
        }
    }

    @EventHandler
    fun onPlayerLogin(e: PlayerLoginEvent) {
        if (Bukkit.getCurrentTick() < TICKS) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, MESSAGE)
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (Bukkit.getCurrentTick() < TICKS) {
            e.player.kick(Component.text("Unsafe login"))
        }
    }

    companion object {
        private const val TICKS = 200
        private val MESSAGE = Component.text("You cannot join the server right now. Please try again 10 seconds later.")
    }
}
