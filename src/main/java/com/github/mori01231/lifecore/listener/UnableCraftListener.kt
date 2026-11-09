package com.github.mori01231.lifecore.listener

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.inventory.ItemStack

class UnableCraftListener : Listener {
    @EventHandler
    fun onCraft(e: PrepareGrindstoneEvent) {
        if (e.view.player.hasPermission("lifecore.bypasscraft")) return
        if (checkItemStack(e.inventory.getItem(0))) e.result = null
        if (checkItemStack(e.inventory.getItem(1))) e.result = null
    }

    @EventHandler
    fun onAnvil(e: PrepareAnvilEvent) {
        if (e.view.player.hasPermission("lifecore.bypasscraft")) return
        if (checkItemStack(e.inventory.getItem(0))) e.result = null
        if (checkItemStack(e.inventory.getItem(1))) e.result = null
    }

    companion object {
        private val LIST = listOf(
            "§a", "§b", "§c", "§d", "§e", "§f", "§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9",
            "§n", "§m", "§o", "§k", "§l",
        )

        fun checkItemStack(item: ItemStack?): Boolean {
            if (item == null || item.type.isAir) return false
            val meta = item.itemMeta ?: return false
            val displayName = meta.displayName() ?: return false
            if (meta.hasLore()) return true
            val legacyName = LegacyComponentSerializer.legacySection().serialize(displayName)
            return LIST.any { legacyName.contains(it) }
        }
    }
}
