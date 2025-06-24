package com.github.mori01231.lifecore.gui;

import com.github.mori01231.lifecore.LifeCore;
import net.azisaba.rarity.api.RarityAPIProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class DropProtectScreen implements InventoryHolder {
    private final Inventory inventory = Bukkit.createInventory(this, 27, "DropProtect");
    private final LifeCore plugin;
    private final Player player;

    public DropProtectScreen(@NotNull LifeCore plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
        reset();
    }

    public void reset() {
        String noRarityLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "no_rarity") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String commonName = RarityAPIProvider.get().getRarityById("common").getDisplayName(player);
        String commonLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "common") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String uncommonName = RarityAPIProvider.get().getRarityById("uncommon").getDisplayName(player);
        String uncommonLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "uncommon") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String rareName = RarityAPIProvider.get().getRarityById("rare").getDisplayName(player);
        String rareLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "rare") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String epicName = RarityAPIProvider.get().getRarityById("epic").getDisplayName(player);
        String epicLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "epic") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String legendaryName = RarityAPIProvider.get().getRarityById("legendary").getDisplayName(player);
        String legendaryLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "legendary") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String mythicName = RarityAPIProvider.get().getRarityById("mythic").getDisplayName(player);
        String mythicLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "mythic") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String specialName = RarityAPIProvider.get().getRarityById("special").getDisplayName(player);
        String specialLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "special") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String hasPvELevelLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "has_pve_level") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        String hasNoPvELevelLore = plugin.getDropProtectConfig().contains(player.getUniqueId(), "has_no_pve_level") ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効";
        TrashProtectScreen.setItems(noRarityLore, commonName, commonLore, uncommonName, uncommonLore, rareName, rareLore, epicName, epicLore, legendaryName, legendaryLore, mythicName, mythicLore, specialName, specialLore, hasPvELevelLore, hasNoPvELevelLore, inventory);
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
            if (!(e.getInventory().getHolder() instanceof DropProtectScreen)) {
                return;
            }
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof DropProtectScreen)) {
                return;
            }
            DropProtectScreen screen = (DropProtectScreen) e.getInventory().getHolder();
            switch (e.getSlot()) {
                case 0: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "no_rarity");
                    break;
                }
                case 1: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "common");
                    break;
                }
                case 2: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "uncommon");
                    break;
                }
                case 3: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "rare");
                    break;
                }
                case 4: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "epic");
                    break;
                }
                case 5: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "legendary");
                    break;
                }
                case 6: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "mythic");
                    break;
                }
                case 7: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "special");
                    break;
                }
                case 8: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "has_pve_level");
                    break;
                }
                case 9: {
                    plugin.getDropProtectConfig().toggle(screen.player.getUniqueId(), "has_no_pve_level");
                    break;
                }
                case 26: {
                    screen.player.closeInventory();
                    return;
                }
            }
            screen.reset();
        }
    }
}
