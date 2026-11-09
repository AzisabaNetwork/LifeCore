package com.github.mori01231.lifecore.listener

import org.bukkit.entity.Villager
import org.bukkit.entity.WanderingTrader
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType

class PreventOpenMerchantMenuListener : Listener {
    @EventHandler
    fun cancelOpenVillagerInventory(event: InventoryOpenEvent) {
        if (event.inventory.type != InventoryType.MERCHANT) return
        val holder = event.inventory.holder
        if (holder is WanderingTrader || holder is Villager) {
            event.isCancelled = true
        }
    }
}
