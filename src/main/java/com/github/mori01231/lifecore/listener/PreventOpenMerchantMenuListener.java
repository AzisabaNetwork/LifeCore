package com.github.mori01231.lifecore.listener;

import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class PreventOpenMerchantMenuListener implements Listener {
    @EventHandler
    public void cancelOpenVillagerInventory(InventoryOpenEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof WanderingTrader || holder instanceof Villager) {
            event.setCancelled(true);
        }
    }
}
