package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.TrashInventory;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;


public class TrashListener implements Listener {
    private final Logger logger;

    public TrashListener(LifeCore plugin){
        this.logger = plugin.getLogger();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String PlayerName = player.getName();

        int TrashMoneyPerItem = Integer.parseInt(LifeCore.getInstance().getConfig().getString("TrashMoneyPerItem"));

        if (event.getView().getTopInventory().getHolder() instanceof TrashInventory) {

            int MoneyCounter = 0;
            int moneyMultiplier = TrashMoneyPerItem;
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack item : event.getInventory().getContents()) {
                try {
                    if (item.getAmount() > 0) {

                        for (String line : LifeCore.getInstance().getConfig().getStringList("Trash.Items")) {
                            if(item.getItemMeta().getDisplayName().equals(line)){
                                moneyMultiplier = LifeCore.getInstance().getConfig().getInt("TrashMoneyPerSpecialItem");
                            }
                        }

                        MoneyCounter += item.getAmount() * moneyMultiplier;
                        items.add(item.clone());
                        item.setAmount(0);
                    }

                } catch (Exception ignored) {
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ゴミ箱に" + MoneyCounter + "個のアイテムを捨てました。" ));
            getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + PlayerName + " " + MoneyCounter);
            logger.info("Player " + PlayerName + " has trashed " + MoneyCounter + " items:");
            for (ItemStack item : items) {
                logger.info("  " + ItemUtil.toString(item));
            }
        }
    }
}
