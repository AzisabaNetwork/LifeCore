package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class CancelMythicItemPlaceListener : Listener {
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (ItemUtil.getMythicType(e.itemInHand) != null) {
            e.isCancelled = true
        }
    }
}
