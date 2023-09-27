package com.github.mori01231.lifecore.listener

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent

object VoidListener : Listener {
    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if ((e.player.gameMode == GameMode.SURVIVAL || e.player.gameMode == GameMode.ADVENTURE) && e.to.y < 0) {
            e.player.lastDamageCause = EntityDamageEvent(e.player, EntityDamageEvent.DamageCause.VOID, 99999999.0)
            e.player.damage(99999999.0)
            e.player.health = 0.0
        }
    }
}
