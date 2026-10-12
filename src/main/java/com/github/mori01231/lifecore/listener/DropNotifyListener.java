package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.config.DropNotifyFile;
import com.github.mori01231.lifecore.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
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
            List<Component> list = new ArrayList<>();
            ItemStack stack = e.getItemDrop().getItemStack();
            if (stack.hasItemMeta()) {
                ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
                Component displayName = meta.displayName();
                if (displayName != null) {
                    list.add(displayName);
                }
            }
            list.add(Component.text("[Type: " + stack.getType().name() + "]", NamedTextColor.GRAY));
            String mmid = ItemUtil.getMythicType(stack);
            if (mmid != null) {
                list.add(Component.text("[MMID: " + mmid + "]", NamedTextColor.GRAY));
            }
            e.getPlayer().sendMessage(Component.join(JoinConfiguration.spaces(), list).append(Component.text("をドロップしました。", NamedTextColor.YELLOW)));
        }
    }
}
