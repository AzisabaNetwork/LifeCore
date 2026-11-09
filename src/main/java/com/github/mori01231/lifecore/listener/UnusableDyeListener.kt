package com.github.mori01231.lifecore.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class UnusableDyeListener : Listener {
    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        val block = e.clickedBlock ?: return
        val item = e.player.inventory.itemInMainHand
        if (!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) return
        if (block.type.name.endsWith("_SIGN") && item.type in DYES) {
            e.isCancelled = true
        }
    }

    companion object {
        private val DYES = setOf(
            Material.WHITE_DYE,
            Material.ORANGE_DYE,
            Material.MAGENTA_DYE,
            Material.LIGHT_BLUE_DYE,
            Material.YELLOW_DYE,
            Material.LIME_DYE,
            Material.PINK_DYE,
            Material.GRAY_DYE,
            Material.LIGHT_GRAY_DYE,
            Material.CYAN_DYE,
            Material.PURPLE_DYE,
            Material.BLUE_DYE,
            Material.BROWN_DYE,
            Material.GREEN_DYE,
            Material.RED_DYE,
            Material.BLACK_DYE,
        )
    }
}
