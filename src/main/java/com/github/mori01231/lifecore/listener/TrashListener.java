package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.TrashInventory;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TrashListener implements Listener {
    private final LifeCore plugin;

    public TrashListener(@NotNull LifeCore plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String playerName = player.getName();

        int trashMoneyPerItem = plugin.getConfig().getInt("TrashMoneyPerItem", 0);

        if (event.getView().getTopInventory().getHolder() instanceof TrashInventory) {

            int moneyCounter = 0;
            int moneyMultiplier = trashMoneyPerItem;
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack item : event.getInventory().getContents()) {
                try {
                    if (item.getAmount() > 0) {

                        for (String line : plugin.getConfig().getStringList("Trash.Items")) {
                            if(item.getItemMeta().getDisplayName().equals(line)){
                                moneyMultiplier = plugin.getConfig().getInt("TrashMoneyPerSpecialItem");
                            }
                        }

                        moneyCounter += item.getAmount() * moneyMultiplier;
                        items.add(item.clone());
                        item.setAmount(0);
                    }

                } catch (Exception ignored) {
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ゴミ箱に" + moneyCounter + "個のアイテムを捨てました。" ));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + playerName + " " + moneyCounter);
            plugin.getSLF4JLogger().info("Player {} has trashed {} items:", playerName, moneyCounter);
            for (ItemStack item : items) {
                plugin.getLogger().info("  " + ItemUtil.toString(item));
            }
        }
    }
}
