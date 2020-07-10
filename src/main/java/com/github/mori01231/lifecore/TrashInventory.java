package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TrashInventory implements InventoryHolder {

    public Inventory getInventory(){
        Inventory trash = Server.createInventory(this, 54, "ゴミ箱");
        return(trash);
    }

}
