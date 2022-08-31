package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpawnOnJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (LifeCore.getInstance().getConfig().getBoolean("spawnOnJoin", false)) {
            Bukkit.dispatchCommand(e.getPlayer(), "spawn");
        }
    }
}
