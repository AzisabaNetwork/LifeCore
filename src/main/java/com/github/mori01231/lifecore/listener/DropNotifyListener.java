package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.config.DropNotifyFile;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DropNotifyListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (DropNotifyFile.contains(e.getPlayer().getUniqueId())) {
            List<String> list = new ArrayList<>();
            ItemStack stack = e.getItemDrop().getItemStack();
            if (stack.hasItemMeta()) {
                ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
                if (meta.hasDisplayName()) {
                    list.add(ChatColor.RESET + meta.getDisplayName());
                }
            }
            list.add(ChatColor.GRAY + "[Type: " + stack.getType().name() + "]");
            String mmid = ItemUtil.getMythicType(stack);
            if (mmid != null) {
                list.add(ChatColor.GRAY + "[MMID: " + mmid + "]");
            }
            e.getPlayer().sendMessage(String.join(" ", list) + ChatColor.YELLOW + "をドロップしました。");
        }
    }
}
