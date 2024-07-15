package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class UpdateInventoryOnCloseListener(private val plugin: LifeCore) : Listener {
    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            (e.player as Player).updateInventory()
        })
    }
}