package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.event.AsyncPreSignChangeEvent
import com.github.mori01231.lifecore.util.PromptSign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object PromptSignListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSign(e: AsyncPreSignChangeEvent) {
        PromptSign.awaitingSign.remove(e.player.uniqueId)?.invoke(e.lines.toList())
    }
}
