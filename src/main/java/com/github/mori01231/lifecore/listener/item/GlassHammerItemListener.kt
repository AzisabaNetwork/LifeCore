package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent

class GlassHammerItemListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.action != Action.LEFT_CLICK_BLOCK || e.player.gameMode != GameMode.SURVIVAL) {
            // return if the player is not in survival mode or if the player is not left-clicking a block
            return
        }
        val block = requireNotNull(e.clickedBlock)
        val type = block.type
        if (!type.name.contains("GLASS") && type != Material.GLOWSTONE && type != Material.SEA_LANTERN) {
            // return if the player is not left-clicking a glass block
            return
        }
        if (ITEM_ID != ItemUtil.getStringTag(e.item, "LifeItemId")) {
            // wrong item
            return
        }
        if (BlockBreakEvent(block, e.player).callEvent()) {
            block.breakNaturally(e.item ?: return, true)
        }
    }

    companion object {
        private const val ITEM_ID = "38ac6fcd-e77f-4a19-a251-bb9cd48bd367"
    }
}
