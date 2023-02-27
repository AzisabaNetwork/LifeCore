package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.config.DropProtectFile;
import net.azisaba.itemstash.ItemStash;
import net.azisaba.rarity.api.Rarity;
import net.azisaba.rarity.api.RarityAPI;
import net.azisaba.rarity.api.RarityAPIProvider;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class DropProtectListener implements Listener {
    private final RarityAPI rarityAPI = RarityAPIProvider.get();
    private final ItemStash itemStash = ItemStash.getInstance();

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Rarity rarity = rarityAPI.getRarityByItemStack(e.getItemDrop().getItemStack());
        if (rarity == null) {
            return;
        }
        if (DropProtectFile.contains(e.getPlayer().getUniqueId(), rarity.getId())) {
            e.getPlayer().sendMessage(ChatColor.RED + "このレア度のアイテムはドロップできません。");
            e.getPlayer().sendMessage(ChatColor.AQUA + "/dropprotect" + ChatColor.GOLD + "で設定を変更できます。");
            e.setCancelled(true);
            if (e.getPlayer().getInventory().firstEmpty() == -1) {
                ItemStack stack = e.getItemDrop().getItemStack();
                e.getItemDrop().remove();
                e.getPlayer().getInventory().addItem(stack).forEach((i, s) -> {
                    itemStash.addItemToStash(e.getPlayer().getUniqueId(), s);
                    e.getPlayer().sendMessage(ChatColor.RED + "インベントリがいっぱいのため、Stashに保管されました。");
                    e.getPlayer().sendMessage(ChatColor.AQUA + "/pickupstash" + ChatColor.RED + "で回収できます。");
                });
            }
        }
    }
}
