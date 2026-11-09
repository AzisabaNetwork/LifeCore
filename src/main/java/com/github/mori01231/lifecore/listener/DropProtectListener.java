package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.ItemUtil;
import net.azisaba.itemstash.ItemStash;
import net.azisaba.lifepvelevel.util.Util;
import net.azisaba.rarity.api.Rarity;
import net.azisaba.rarity.api.RarityAPI;
import net.azisaba.rarity.api.RarityAPIProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class DropProtectListener implements Listener {
    private final RarityAPI rarityAPI = RarityAPIProvider.get();
    private final ItemStash itemStash = ItemStash.getInstance();
    private final LifeCore plugin;

    public DropProtectListener(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack itemStack = e.getItemDrop().getItemStack();

        // Check /protect first
        String protectKey = ItemUtil.getProtectKey(itemStack);
        if (protectKey != null && plugin.getMythicItemProtectConfig().contains(e.getPlayer().getUniqueId(), protectKey)) {
            cancelDrop(e, itemStack, "/protect によるアイテム保護 (" + protectKey + ")");
            return;
        }

        Rarity rarity = rarityAPI.getRarityByItemStack(itemStack);
        boolean shouldCancel;
        long requiredLevel = Util.getRequiredLevel(itemStack);
        if (plugin.getDropProtectConfig().contains(e.getPlayer().getUniqueId(), "has_pve_level") && requiredLevel > 0) {
            shouldCancel = true;
        } else if (plugin.getDropProtectConfig().contains(e.getPlayer().getUniqueId(), "has_no_pve_level") && requiredLevel == 0 && !ItemUtil.isVanillaItem(itemStack)) {
            shouldCancel = true;
        } else {
            if (rarity == null) {
                if (plugin.getDropProtectConfig().contains(e.getPlayer().getUniqueId(), "no_rarity")) {
                    shouldCancel = true;
                } else {
                    return;
                }
            } else {
                shouldCancel = plugin.getDropProtectConfig().contains(e.getPlayer().getUniqueId(), rarity.getId());
            }
        }
        if (shouldCancel) {
            cancelDrop(e, itemStack, null);
        }
    }

    @SuppressWarnings("unchecked")
    private void cancelDrop(@NotNull PlayerDropItemEvent e, @NotNull ItemStack stack, @Nullable String reason) {
        e.setCancelled(true);

        // set item to air via reflection and data watcher
        e.getItemDrop().remove();
        ItemEntity entityItem = (ItemEntity) ((CraftItem) e.getItemDrop()).getHandle();
        EntityDataAccessor<net.minecraft.world.item.ItemStack> itemStackDataWatcherObject;
        try {
            Field f = ItemEntity.class.getDeclaredField("DATA_ITEM");
            f.setAccessible(true);
            itemStackDataWatcherObject = (EntityDataAccessor<net.minecraft.world.item.ItemStack>) f.get(null);
        } catch (ReflectiveOperationException ex) {
            return;
        }
        entityItem.getEntityData().set(itemStackDataWatcherObject, new net.minecraft.world.item.ItemStack(Items.AIR));
        entityItem.getEntityData().markDirty(itemStackDataWatcherObject);

        e.getPlayer().sendMessage(Component.text("このアイテムはドロップできません。", NamedTextColor.RED));
        if (reason != null) {
            e.getPlayer().sendMessage(Component.text("理由: " + reason, NamedTextColor.YELLOW));
            e.getPlayer().sendMessage(Component.text("/protect", NamedTextColor.AQUA).append(Component.text("でアイテムの保護設定を変更できます。", NamedTextColor.GOLD)));
        } else {
            e.getPlayer().sendMessage(Component.text("/dropprotect", NamedTextColor.AQUA).append(Component.text("で設定を変更できます。", NamedTextColor.GOLD)));
        }
        e.getPlayer().getInventory().addItem(stack).forEach((i, s) -> {
            itemStash.addItemToStash(e.getPlayer().getUniqueId(), s);
            e.getPlayer().sendMessage(Component.text("インベントリがいっぱいのため、Stashに保管されました。", NamedTextColor.RED));
            e.getPlayer().sendMessage(Component.text("/pickupstash", NamedTextColor.AQUA).append(Component.text("で回収できます。", NamedTextColor.RED)));
        });
    }
}
