package com.github.mori01231.lifecore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getServer;


public class TrashListener implements Listener {

    private LifeCore plugin;
    public TrashListener(LifeCore plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        String PlayerName = event.getPlayer().getName();

        if (event.getView().getTitle().equals("ゴミ箱")) {
            int MoneyCounter = 0;
            for (ItemStack item: event.getInventory().getContents()) {
                try{
                    if(item.getAmount() > 0){
                        MoneyCounter += item.getAmount();
                        item.setAmount(0);
                    }

                }catch (Exception e){
                }
            }
            getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + PlayerName + " " + MoneyCounter);
        }
    }

}
