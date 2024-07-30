package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.network.PacketHandler;
import com.github.mori01231.lifecore.util.ItemUtil;
import com.github.mori01231.lifecore.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {
    private final LifeCore plugin;

    public PlayerJoinListener(LifeCore plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // preload ng word data
            plugin.ngWordsCache.loadAsync(e.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // spawn on join
        if (plugin.getConfig().getBoolean("spawnOnJoin", false)) {
            Bukkit.dispatchCommand(e.getPlayer(), "spawn");
        }

        // inject channel handler
        PlayerUtil.getChannel(e.getPlayer()).pipeline()
                .addBefore("packet_handler", "lifecore", new PacketHandler(e.getPlayer()));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = Bukkit.getPlayer(e.getPlayer().getUniqueId());
            if (player == null || !player.isOnline()) {
                return;
            }

            // restore item tags
            if (plugin.getConfig().getBoolean("enable-backup-item-tag", true)) {
                int inventorySize = player.getInventory().getSize();
                for (int i = 0; i < inventorySize; i++) {
                    player.getInventory().setItem(i, ItemUtil.restoreTag(player.getInventory().getItem(i)));
                }
                int enderSize = player.getEnderChest().getSize();
                for (int i = 0; i < enderSize; i++) {
                    player.getEnderChest().setItem(i, ItemUtil.restoreTag(player.getEnderChest().getItem(i)));
                }
            }
        }, 20 * 2); // 2 seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = Bukkit.getPlayer(e.getPlayer().getUniqueId());
            if (player == null || !player.isOnline()) {
                return;
            }

            // restore item tags
            if (plugin.getConfig().getBoolean("enable-backup-item-tag", true)) {
                int inventorySize = player.getInventory().getSize();
                for (int i = 0; i < inventorySize; i++) {
                    player.getInventory().setItem(i, ItemUtil.restoreTag(player.getInventory().getItem(i)));
                }
                int enderSize = player.getEnderChest().getSize();
                for (int i = 0; i < enderSize; i++) {
                    player.getEnderChest().setItem(i, ItemUtil.restoreTag(player.getEnderChest().getItem(i)));
                }
            }
        }, 20 * 10); // 10 seconds
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            return;
        }
        if (e.getPlayer().getName().toLowerCase().startsWith("town_") || e.getPlayer().getName().toLowerCase().startsWith("nation_")) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Towny is preventing you from logging in using this account name.");
        }
    }
}
