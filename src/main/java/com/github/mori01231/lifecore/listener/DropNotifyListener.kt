package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.config.DropNotifyFile
import com.github.mori01231.lifecore.util.ItemUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class DropNotifyListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        if (!DropNotifyFile.contains(e.player.uniqueId)) return
        val list = mutableListOf<Component>()
        val stack = e.itemDrop.itemStack
        if (stack.hasItemMeta()) {
            stack.itemMeta.displayName()?.let { list.add(it) }
        }
        list.add(Component.text("[Type: " + stack.type.name + "]", NamedTextColor.GRAY))
        ItemUtil.getMythicType(stack)?.let { mmid ->
            list.add(Component.text("[MMID: $mmid]", NamedTextColor.GRAY))
        }
        e.player.sendMessage(
            Component.join(JoinConfiguration.spaces(), list)
                .append(Component.text("をドロップしました。", NamedTextColor.YELLOW))
        )
    }
}
