package com.github.mori01231.lifecore.listener

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.TabCompleteEvent

class OverrideTabCompleteListener : Listener {
    @EventHandler
    fun onTabComplete(e: TabCompleteEvent) {
        handlers.forEach { (key, handler) ->
            if (e.buffer.startsWith(key)) {
                e.completions = handler.handle(e.sender, e.buffer, e.completions)
            }
        }
    }

    fun interface TabCompleteHandler {
        fun handle(sender: CommandSender, buffer: String, list: List<String>): List<String>
    }

    companion object {
        private val handlers: Map<String, TabCompleteHandler> = mapOf(
            "/nick " to TabCompleteHandler { sender, buffer, list ->
                if (sender !is Player || list.isNotEmpty()) {
                    return@TabCompleteHandler list
                }
                val last = buffer.substring(buffer.lastIndexOf(' ') + 1)
                if (last == "off" || sender.name.equals(last, ignoreCase = true)) {
                    return@TabCompleteHandler emptyList()
                }
                val chat = getChat()
                val prefix = ampersandToSection(chat.getPlayerPrefix(sender))
                val suffix = ampersandToSection(chat.getPlayerSuffix(sender))
                listOf(prefix + "~" + ampersandToSection(last) + suffix)
            },
        )

        private fun ampersandToSection(text: String): String =
            LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text))

        private fun getChat(): Chat =
            requireNotNull(Bukkit.getServicesManager().getRegistration(Chat::class.java)).provider
    }
}
