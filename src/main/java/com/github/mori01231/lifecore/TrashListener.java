package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;


public class TrashListener implements Listener {

    private LifeCore plugin;
    public TrashListener(LifeCore plugin){
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        String PlayerName = player.getName();

        Integer TrashMoneyPerItem = Integer.valueOf(LifeCore.getInstance().getConfig().getString("TrashMoneyPerItem"));

        if (event.getView().getTopInventory().getHolder() instanceof TrashInventory) {

            int MoneyCounter = 0;
            int moneyMultiplier = TrashMoneyPerItem;
            for (ItemStack item: event.getInventory().getContents()) {
                try{
                    if(item.getAmount() > 0){

                        for (String line : LifeCore.getInstance().getConfig().getStringList("Trash.Items")) {
                            if(item.getItemMeta().getDisplayName().equals(line)){
                                moneyMultiplier = LifeCore.getInstance().getConfig().getInt("TrashMoneyPerSpecialItem");
                            }
                        }

                        MoneyCounter += item.getAmount() * moneyMultiplier;
                        item.setAmount(0);
                    }

                }catch (Exception e){
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ゴミ箱に" + MoneyCounter + "個のアイテムを捨てました。" ));
            getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + PlayerName + " " + MoneyCounter);
        }
    }
}
