package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

object BlockListener : Listener {
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (ItemUtil.containsTag(e.itemInHand, "backup") && !e.player.isSneaking) {
            e.player.sendMessage("${ChatColor.RED}このブロックを設置すると中身のアイテムが変質し、元の状態に戻らなくなる可能性があります。")
            e.player.sendMessage("${ChatColor.RED}この警告を無視して設置したい場合はスニークしながら設置してください。")
            e.isCancelled = true
        }
    }
}
