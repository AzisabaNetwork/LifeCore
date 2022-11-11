package com.github.mori01231.lifecore.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class CancelJoinAfterStartupListener implements Listener {
    private static final int TICKS = 200;

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (Bukkit.getCurrentTick() < TICKS) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "You cannot join the server right now. Please try again 10 seconds later.");
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (Bukkit.getCurrentTick() < TICKS) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You cannot join the server right now. Please try again 10 seconds later.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Bukkit.getCurrentTick() < TICKS) {
            e.getPlayer().kickPlayer("Unsafe login");
        }
    }
}
