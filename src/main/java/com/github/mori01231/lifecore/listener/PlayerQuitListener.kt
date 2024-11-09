package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.MapUtil
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

object PlayerQuitListener : Listener {
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(LifeCore::class.java), Runnable {
            MapUtil.renderedMapViews.removeIf { it.first == e.player.uniqueId }
        })
    }
}
