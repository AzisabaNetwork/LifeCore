package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.util.ItemUtil
import com.github.mori01231.lifecore.util.damage
import org.bukkit.FluidCollisionMode
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent

class LavaSpongeItemListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (e.action == Action.RIGHT_CLICK_BLOCK && e.useItemInHand() == Event.Result.DENY) return
        val hitBlock = e.player.rayTraceBlocks(4.0, FluidCollisionMode.ALWAYS)?.hitBlock ?: return
        if (ITEM_ID != ItemUtil.getStringTag(e.item, "LifeItemId")) {
            // wrong item
            return
        }
        var affected = 0
        for (dx in -RADIUS..RADIUS) {
            for (dy in -RADIUS..RADIUS) {
                for (dz in -RADIUS..RADIUS) {
                    val block = hitBlock.location.clone().add(dx.toDouble(), dy.toDouble(), dz.toDouble()).block
                    if (block.type != Material.LAVA && block.type != Material.WATER) {
                        // wrong block
                        continue
                    }
                    if (BlockBreakEvent(block, e.player).callEvent()) {
                        block.setType(Material.AIR, true)
                        affected++
                    }
                }
            }
        }
        if (affected > 0 && e.player.gameMode != GameMode.CREATIVE) {
            e.item?.damage(e.player, 1, checkDurabilityEnchantment = true, consumeIfNotDamageable = true)
        }
    }

    companion object {
        private const val ITEM_ID = "56fabea9-e1f9-4e7f-ae78-83e07e8b8767"
        private const val RADIUS = 2
    }
}
