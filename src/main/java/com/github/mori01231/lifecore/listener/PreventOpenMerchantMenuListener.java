package com.github.mori01231.lifecore.listener;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class PreventOpenMerchantMenuListener implements Listener {
    @EventHandler
    public void cancelOpenVillagerInventory(InventoryOpenEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
        Villager villager = (Villager) event.getInventory().getHolder();
        if (villager == null) return;
        event.setCancelled(true);
    }
}
