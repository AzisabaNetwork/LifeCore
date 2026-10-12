package com.github.mori01231.lifecore.gui;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.ItemUtil;
import net.azisaba.rarity.api.RarityAPIProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
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
    private final Inventory inventory = Bukkit.createInventory(this, 18, Component.text("TrashProtect"));
    private final LifeCore plugin;
    private final Player player;

    public TrashProtectScreen(@NotNull LifeCore plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
        reset();
    }

    public void reset() {
        Component noRarityLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "no_rarity") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        String commonName = RarityAPIProvider.get().getRarityById("common").getDisplayName(player);
        Component commonLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "common") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        String uncommonName = RarityAPIProvider.get().getRarityById("uncommon").getDisplayName(player);
        Component uncommonLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "uncommon") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        String rareName = RarityAPIProvider.get().getRarityById("rare").getDisplayName(player);
        Component rareLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "rare") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        String epicName = RarityAPIProvider.get().getRarityById("epic").getDisplayName(player);
        Component epicLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "epic") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        String legendaryName = RarityAPIProvider.get().getRarityById("legendary").getDisplayName(player);
        Component legendaryLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "legendary") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        String mythicName = RarityAPIProvider.get().getRarityById("mythic").getDisplayName(player);
        Component mythicLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "mythic") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        String specialName = RarityAPIProvider.get().getRarityById("special").getDisplayName(player);
        Component specialLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "special") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        Component hasPvELevelLore = plugin.getTrashProtectConfig().contains(player.getUniqueId(), "has_pve_level") ? Component.text("有効", NamedTextColor.GREEN) : Component.text("無効", NamedTextColor.RED);
        setItems(noRarityLore, commonName, commonLore, uncommonName, uncommonLore, rareName, rareLore, epicName, epicLore, legendaryName, legendaryLore, mythicName, mythicLore, specialName, specialLore, hasPvELevelLore, inventory);
    }

    static void setItems(Component noRarityLore, String commonName, Component commonLore, String uncommonName, Component uncommonLore, String rareName, Component rareLore, String epicName, Component epicLore, String legendaryName, Component legendaryLore, String mythicName, Component mythicLore, String specialName, Component specialLore, Component hasPvELevelLore, Inventory inventory) {
        inventory.setItem(0, ItemUtil.createItemStack(Material.PAPER, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text("レア度が付与されていないアイテム", NamedTextColor.WHITE));
                meta.lore(Collections.singletonList(noRarityLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(1, ItemUtil.createItemStack(Material.COBBLESTONE, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(commonName));
                meta.lore(Collections.singletonList(commonLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(2, ItemUtil.createItemStack(Material.IRON_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(uncommonName));
                meta.lore(Collections.singletonList(uncommonLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(3, ItemUtil.createItemStack(Material.GOLD_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(rareName));
                meta.lore(Collections.singletonList(rareLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(4, ItemUtil.createItemStack(Material.EMERALD_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(epicName));
                meta.lore(Collections.singletonList(epicLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(5, ItemUtil.createItemStack(Material.DIAMOND_BLOCK, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(legendaryName));
                meta.lore(Collections.singletonList(legendaryLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(6, ItemUtil.createItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(mythicName));
                meta.lore(Collections.singletonList(mythicLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(7, ItemUtil.createItemStack(Material.NETHER_STAR, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(specialName));
                meta.lore(Collections.singletonList(specialLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(8, ItemUtil.createItemStack(Material.MAP, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text("PvEレベルが付与されているアイテム", NamedTextColor.YELLOW));
                meta.lore(Collections.singletonList(hasPvELevelLore));
                item.setItemMeta(meta);
            }
        }));
        inventory.setItem(17, ItemUtil.createItemStack(Material.BARRIER, 1, item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text("閉じる", NamedTextColor.RED));
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
                    plugin.getTrashProtectConfig().toggle(screen.player.getUniqueId(), "has_pve_level");
                    break;
                }
                case 17: {
                    screen.player.closeInventory();
                    return;
                }
            }
            screen.reset();
        }
    }
}
