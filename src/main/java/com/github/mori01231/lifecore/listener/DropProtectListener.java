package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.config.DropProtectFile;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DropProtectListener implements Listener {
    private static final int TIME = 1500;
    private static final String MESSAGE = ChatColor.RED + "このアイテムをドロップするには1.5秒以内にもう一度ドロップしてください。";
    private final Map<UUID, Long> lastDropTime = new HashMap<>();

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        String mmId = ItemUtil.getMythicType(e.getItemDrop().getItemStack());
        if (mmId == null) {
            return;
        }
        if (DropProtectFile.isProtected(mmId)) {
            if (!lastDropTime.containsKey(e.getPlayer().getUniqueId()) ||
                    System.currentTimeMillis() - lastDropTime.get(e.getPlayer().getUniqueId()) > TIME) {
                lastDropTime.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
                e.setCancelled(true);
                e.getPlayer().sendMessage(MESSAGE);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1f);
            }
        }
    }
}
