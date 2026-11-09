package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent

class GameModeChangeLoggerListener(private val plugin: LifeCore) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onGameModeChange(e: PlayerGameModeChangeEvent) {
        plugin.logger.info("Game mode of " + e.player.name + " changed from " + e.player.gameMode.name + " to " + e.newGameMode.name)
    }
}
