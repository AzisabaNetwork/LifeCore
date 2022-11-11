package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.network.PacketHandler;
import com.github.mori01231.lifecore.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
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
