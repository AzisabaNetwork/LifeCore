package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.util.ItemUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

object BlockListener : Listener {
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (ItemUtil.containsTag(e.itemInHand, "backup") && !e.player.isSneaking) {
            e.player.sendMessage(Component.text("このブロックを設置すると中身のアイテムが変質し、元の状態に戻らなくなる可能性があります。", NamedTextColor.RED))
            e.player.sendMessage(Component.text("この警告を無視して設置したい場合はスニークしながら設置してください。", NamedTextColor.RED))
            e.isCancelled = true
        }
    }
}
