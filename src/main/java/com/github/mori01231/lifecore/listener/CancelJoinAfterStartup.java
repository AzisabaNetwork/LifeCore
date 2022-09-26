package com.github.mori01231.lifecore.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class CancelJoinAfterStartup implements Listener {
    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (Bukkit.getCurrentTick() < 200) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "You cannot join the server right now. Please try again 10 seconds later.");
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (Bukkit.getCurrentTick() < 200) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You cannot join the server right now. Please try again 10 seconds later.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Bukkit.getCurrentTick() < 100) {
            e.getPlayer().kickPlayer("Unsafe login");
        }
    }
}
