package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.util.TowniaUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.HashMap
import java.util.UUID

class TowniaOutlawListener : Listener {
    private val lastMove: MutableMap<UUID, Long> = HashMap()

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        val lastTime = lastMove[player.uniqueId]
        if (lastTime != null && System.currentTimeMillis() - lastTime < 1000) {
            return
        }
        lastMove[player.uniqueId] = System.currentTimeMillis()
        if (!player.hasPermission("lifecore.bypass-outlaw") && isInOutlawedTown(player, player.location)) {
            player.sendMessage(Component.text("出入り禁止になっている街に進入することはできません！", NamedTextColor.RED))
            player.teleport(player.world.spawnLocation)
        }
    }

    @EventHandler
    fun onTeleport(e: PlayerTeleportEvent) {
        val to = e.to
        if (!e.player.hasPermission("lifecore.bypass-outlaw") && isInOutlawedTown(e.player, to)) {
            e.isCancelled = true
            e.player.sendMessage(Component.text("この場所にはテレポートできません。", NamedTextColor.RED))
        }
    }

    private fun isInOutlawedTown(player: Player, location: Location): Boolean {
        val town = TowniaUtil.getTownAt(location) ?: return false
        if (TowniaUtil.isOutlaw(town, player.uniqueId)) {
            return !TowniaUtil.isResident(town, player.uniqueId)
        }
        return false
    }
}
