package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import net.azisaba.itemstash.ItemStash;
import net.azisaba.rarity.api.Rarity;
import net.azisaba.rarity.api.RarityAPI;
import net.azisaba.rarity.api.RarityAPIProvider;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.EntityItem;
import net.minecraft.server.v1_15_R1.Items;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class DropProtectListener implements Listener {
    private final RarityAPI rarityAPI = RarityAPIProvider.get();
    private final ItemStash itemStash = ItemStash.getInstance();
    private final LifeCore plugin;

    public DropProtectListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Rarity rarity = rarityAPI.getRarityByItemStack(e.getItemDrop().getItemStack());
        boolean shouldCancel;
        if (rarity == null) {
            if (plugin.getDropProtectConfig().contains(e.getPlayer().getUniqueId(), "no_rarity")) {
                shouldCancel = true;
            } else {
                return;
            }
        } else {
            shouldCancel = plugin.getDropProtectConfig().contains(e.getPlayer().getUniqueId(), rarity.getId());
        }
        if (shouldCancel) {
            ItemStack stack = e.getItemDrop().getItemStack();
            e.setCancelled(true);

            // set item to air via reflection and data watcher
            e.getItemDrop().remove();
            EntityItem entityItem = (EntityItem) ((CraftItem) e.getItemDrop()).getHandle();
            DataWatcherObject<net.minecraft.server.v1_15_R1.ItemStack> itemStackDataWatcherObject;
            try {
                Field f = EntityItem.class.getDeclaredField("ITEM");
                f.setAccessible(true);
                itemStackDataWatcherObject = (DataWatcherObject<net.minecraft.server.v1_15_R1.ItemStack>) f.get(null);
            } catch (ReflectiveOperationException ex) {
                return;
            }
            entityItem.getDataWatcher().set(itemStackDataWatcherObject, new net.minecraft.server.v1_15_R1.ItemStack(Items.AIR));
            entityItem.getDataWatcher().markDirty(itemStackDataWatcherObject);

            e.getPlayer().sendMessage(ChatColor.RED + "このレア度のアイテムはドロップできません。");
            e.getPlayer().sendMessage(ChatColor.AQUA + "/dropprotect" + ChatColor.GOLD + "で設定を変更できます。");
            e.getPlayer().getInventory().addItem(stack).forEach((i, s) -> {
                itemStash.addItemToStash(e.getPlayer().getUniqueId(), s);
                e.getPlayer().sendMessage(ChatColor.RED + "インベントリがいっぱいのため、Stashに保管されました。");
                e.getPlayer().sendMessage(ChatColor.AQUA + "/pickupstash" + ChatColor.RED + "で回収できます。");
            });
        }
    }
}
