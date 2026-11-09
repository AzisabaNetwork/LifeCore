package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class OreOnlyItemListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onBlockDropItem(e: BlockDropItemEvent) {
        if (!e.player.world.name.startsWith("resource")) {
            // wrong world
            return
        }
        if (ITEM_ID != ItemUtil.getStringTag(e.player.inventory.itemInOffHand, "LifeItemId")) {
            // wrong offhand item
            return
        }
        val type = e.blockState.type
        val name = type.name
        if (name.endsWith("_SIGN") || name.endsWith("_DOOR") || name.endsWith("_ORE") || name.endsWith("SHULKER_BOX")) {
            return
        }
        if (type in EXCEPTION_TYPES) {
            // wrong block type
            return
        }
        e.items.clear()
    }

    companion object {
        private const val ITEM_ID = "3f7cec47-aaec-41b3-a47e-c3f87e16932a"
        private val EXCEPTION_TYPES = setOf(
            Material.CHEST,
            Material.ENDER_CHEST,
            Material.BARREL,
            Material.BLAST_FURNACE,
            Material.FURNACE,
            Material.TRAPPED_CHEST,
            Material.DISPENSER,
            Material.DROPPER,
            Material.HOPPER,
            Material.SMOKER,
            Material.BEACON,
            Material.CONDUIT,
            Material.DIAMOND_BLOCK,
        )
    }
}
