package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.region.CuboidRegion.Companion.isIncomplete
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.UUID

class EscapeLobbyListener(private val plugin: LifeCore) : Listener {
    private val lastMove = mutableMapOf<UUID, Long>()

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (e.player.hasPermission("lifecore.lobby-bypass")) return
        // cooldown
        if (lastMove.containsKey(e.player.uniqueId)) {
            if (System.currentTimeMillis() - lastMove[e.player.uniqueId]!! < 1000) {
                return
            }
        }
        lastMove[e.player.uniqueId] = System.currentTimeMillis()

        val lobby = plugin.lifeCoreConfig.lobbyRegion
        if (lobby.isIncomplete()) return
        if (e.player.world.name != lobby?.world) return
        if (e.player.location.y < 0) {
            Bukkit.dispatchCommand(e.player, "spawn")
        }
        if (lobby.contains(e.player.location)) return
        Bukkit.dispatchCommand(e.player, "spawn")
    }

    @EventHandler
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        if (e.player.hasPermission("lifecore.lobby-bypass")) return
        val lobby = plugin.lifeCoreConfig.lobbyRegion
        if (lobby.isIncomplete()) return
        if (e.to.world.name != lobby?.world) return
        if (lobby.contains(e.to)) return
        e.isCancelled = true
    }
}
