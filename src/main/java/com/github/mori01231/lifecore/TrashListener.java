package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;


public class TrashListener implements Listener {

    private LifeCore plugin;
    public TrashListener(LifeCore plugin, Inventory inv){
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        String PlayerName = player.getName();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3イベント発火！" ));
        getLogger().info(ChatColor.translateAlternateColorCodes('&',"&3イベント発火！" ));

        if (event.getView().getTopInventory().getHolder() instanceof TrashInventory) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Got inventory" ));

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
        }else{
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Not trash inventory" ));
        }
    }

}
