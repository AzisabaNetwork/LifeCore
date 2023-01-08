package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.network.PacketHandler;
import com.github.mori01231.lifecore.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final LifeCore plugin;

    public PlayerJoinListener(LifeCore plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // preload ng word data
            plugin.getNgWordsCache().loadAsync(e.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // spawn on join
        if (LifeCore.getInstance().getConfig().getBoolean("spawnOnJoin", false)) {
            Bukkit.dispatchCommand(e.getPlayer(), "spawn");
        }

        // inject channel handler
        PlayerUtil.getChannel(e.getPlayer()).pipeline()
                .addBefore("packet_handler", "lifecore", new PacketHandler(e.getPlayer()));
    }
}
