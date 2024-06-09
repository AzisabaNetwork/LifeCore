package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TrashInventory implements InventoryHolder {
    private final Inventory inventory;

    public TrashInventory() {
        this.inventory = Bukkit.createInventory(this, 54, "ゴミ箱");
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
