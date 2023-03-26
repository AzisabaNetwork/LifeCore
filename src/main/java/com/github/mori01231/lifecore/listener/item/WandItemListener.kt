package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.region.CuboidRegion.Companion.isComplete
import com.github.mori01231.lifecore.region.PlayerRegionManager
import com.github.mori01231.lifecore.region.WorldLocation
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class WandItemListener(private val plugin: LifeCore) : Listener {
    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (!e.player.hasPermission("lifecore.wand")) {
            return
        }
        if (e.action != Action.LEFT_CLICK_BLOCK && e.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        val block = e.clickedBlock ?: return
        val item = e.item ?: return
        if (!item.isSimilar(PlayerRegionManager.wandItem)) {
            return
        }
        e.isCancelled = true
        val which = if (e.action == Action.LEFT_CLICK_BLOCK) "First" else "Second"
        val loc = WorldLocation(block.location)
        if (e.action == Action.LEFT_CLICK_BLOCK) {
            plugin.playerRegionManager.put(e.player.uniqueId, pos1 = loc)
        } else {
            plugin.playerRegionManager.put(e.player.uniqueId, pos2 = loc)
        }
        val region = plugin.playerRegionManager[e.player.uniqueId] ?: error("Region is null")
        val suffix = if (region.isComplete()) " ${ChatColor.GRAY}(${region.size} blocks)" else ""
        e.player.sendMessage("${ChatColor.YELLOW}$which position set to ${ChatColor.LIGHT_PURPLE}${loc.x}, ${loc.y}, ${loc.z}$suffix")
    }
}
