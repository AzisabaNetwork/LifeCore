package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeChangeLoggerListener implements Listener {
    private final LifeCore plugin;

    public GameModeChangeLoggerListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        plugin.getLogger().info("Game mode of " + e.getPlayer().getName() + " changed from " + e.getPlayer().getGameMode().name() + " to " + e.getNewGameMode().name());
    }
}
