package com.github.mori01231.lifecore.listener

import org.bukkit.GameMode
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

object VoidListener : Listener {
    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if ((e.player.gameMode == GameMode.SURVIVAL || e.player.gameMode == GameMode.ADVENTURE) && e.from.y < -64) {
            e.player.damage(99999999.0, DamageSource.builder(DamageType.OUT_OF_WORLD).build())
            e.player.health = 0.0
        }
    }
}
