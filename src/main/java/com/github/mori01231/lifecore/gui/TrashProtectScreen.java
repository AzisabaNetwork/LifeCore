package com.github.mori01231.lifecore.gui;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.ItemUtil;
import net.azisaba.rarity.api.RarityAPIProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class TrashProtectScreen implements InventoryHolder {
    private final Inventory inventory = Bukkit.createInventory(this, 9, "TrashProtect");
    private final LifeCore plugin;
    private final Player player;

    public TrashProtectScreen(@NotNull LifeCore plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
        reset();
    }

    public void reset() {
        String noRarityLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "no_rarity") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String commonName = RarityAPIProvider.get().getRarityById("common").getDisplayName(player);
        String commonLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "common") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String uncommonName = RarityAPIProvider.get().getRarityById("uncommon").getDisplayName(player);
        String uncommonLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "uncommon") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String rareName = RarityAPIProvider.get().getRarityById("rare").getDisplayName(player);
        String rareLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "rare") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String epicName = RarityAPIProvider.get().getRarityById("epic").getDisplayName(player);
        String epicLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "epic") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String legendaryName = RarityAPIProvider.get().getRarityById("legendary").getDisplayName(player);
        String legendaryLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "legendary") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String mythicName = RarityAPIProvider.get().getRarityById("mythic").getDisplayName(player);
        String mythicLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "mythic") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String specialName = RarityAPIProvider.get().getRarityById("special").getDisplayName(player);
        String specialLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "special") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        inventory.setItem(0, ItemUtil.createItemStack(Material.PAPER, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.WHITE + "レア度が付与されていないアイテム");
                meta.setLore(Collections.singletonList(noRarityLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(1, ItemUtil.createItemStack(Material.COBBLESTONE, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', commonName));
                meta.setLore(Collections.singletonList(commonLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(2, ItemUtil.createItemStack(Material.IRON_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', uncommonName));
                meta.setLore(Collections.singletonList(uncommonLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(3, ItemUtil.createItemStack(Material.GOLD_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rareName));
                meta.setLore(Collections.singletonList(rareLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(4, ItemUtil.createItemStack(Material.EMERALD_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', epicName));
                meta.setLore(Collections.singletonList(epicLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(5, ItemUtil.createItemStack(Material.DIAMOND_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', legendaryName));
                meta.setLore(Collections.singletonList(legendaryLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(6, ItemUtil.createItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mythicName));
                meta.setLore(Collections.singletonList(mythicLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(7, ItemUtil.createItemStack(Material.NETHER_STAR, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', specialName));
                meta.setLore(Collections.singletonList(specialLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(8, ItemUtil.createItemStack(Material.BARRIER, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "閉じる");
                item.setItemMeta(meta);
            }
        }));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static class EventListener implements Listener {
        private final LifeCore plugin;

        public EventListener(@NotNull LifeCore plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof TrashProtectScreen)) {
                return;
            }
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof TrashProtectScreen)) {
                return;
            }
            TrashProtectScreen screen = (TrashProtectScreen) e.getInventory().getHolder();
            switch (e.getSlot()) {
                case 0: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "no_rarity");
                    break;
                }
                case 1: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "common");
                    break;
                }
                case 2: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "uncommon");
                    break;
                }
                case 3: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "rare");
                    break;
                }
                case 4: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "epic");
                    break;
                }
                case 5: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "legendary");
                    break;
                }
                case 6: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "mythic");
                    break;
                }
                case 7: {
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "special");
                    break;
                }
                case 8: {
                    screen.player.closeInventory();
                    return;
                }
            }
            screen.reset();
        }
    }
}
