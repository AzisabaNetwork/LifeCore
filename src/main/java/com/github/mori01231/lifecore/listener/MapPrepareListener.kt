package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.MapUtil
import org.bukkit.Bukkit
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.world.EntitiesLoadEvent

/**
 * Prepares SerializedMapData maps as soon as they appear, instead of waiting for the periodic inventory check.
 * Everything runs on the main thread (the work is deferred by one tick so entities/inventories are not
 * modified while they are still being loaded).
 */
class MapPrepareListener(private val plugin: LifeCore) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntitiesLoad(e: EntitiesLoadEvent) {
        if (plugin.config.getBoolean("disable-item-frame-map-update", false)) return
        val itemFrames = e.entities.filterIsInstance<ItemFrame>()
        if (itemFrames.isEmpty()) return
        Bukkit.getScheduler().runTask(plugin, Runnable {
            itemFrames.forEach { MapUtil.prepareItemFrame(it) }
        })
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (player.isOnline) MapUtil.prepareInventory(player)
        })
    }
}
