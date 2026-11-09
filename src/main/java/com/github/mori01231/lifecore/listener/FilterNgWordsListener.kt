package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.NGWordsCache
import net.azisaba.ryuzupluginchat.event.AsyncChannelMessageEvent
import net.azisaba.ryuzupluginchat.event.AsyncGlobalMessageEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FilterNgWordsListener(private val plugin: LifeCore) : Listener {
    @EventHandler
    fun onGlobalChat(e: AsyncGlobalMessageEvent) {
        filter(e.recipients, e.message.format())
    }

    @EventHandler
    fun onChannelChat(e: AsyncChannelMessageEvent) {
        filter(e.recipients, e.message.format())
    }

    private fun filter(recipients: MutableSet<Player>, format: String) {
        val toRemove = mutableListOf<Player>()
        for (player in recipients) {
            val filtered = NGWordsCache.filter(plugin.ngWordsCache.get(player.uniqueId), format)
            if (format != filtered) {
                toRemove.add(player)
                player.sendMessage(filtered)
            }
        }
        recipients.removeAll(toRemove.toSet())
    }
}
