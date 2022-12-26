package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.reflector.towny.Resident;
import com.github.mori01231.lifecore.reflector.towny.Town;
import com.github.mori01231.lifecore.reflector.towny.TownBlock;
import com.github.mori01231.lifecore.reflector.towny.TownyAPI;
import com.github.mori01231.lifecore.util.LazyInitValue;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TownyOutlawListener implements Listener {
    private final Map<UUID, Long> lastMove = new HashMap<>();
    private final LazyInitValue<TownyAPI> towny = new LazyInitValue<>(() -> TownyAPI.getInstance(null).getInstance());

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (lastMove.containsKey(e.getPlayer().getUniqueId())) {
            if (System.currentTimeMillis() - lastMove.get(e.getPlayer().getUniqueId()) < 1000) {
                return;
            }
        }
        lastMove.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        TownBlock townBlock = towny.get().getTownBlock(e.getPlayer().getLocation());
        if (townBlock != null && townBlock.hasTown()) {
            Town town = townBlock.getTown();
            for (Object object : town.getOutlaws()) {
                Resident resident = Resident.getInstance(object);
                if (resident.getName().equals(e.getPlayer().getName())) {
                    e.getPlayer().sendMessage(ChatColor.RED + "出入り禁止になっている街に進入することはできません！");
                    e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
                }
            }
        }
    }
}
