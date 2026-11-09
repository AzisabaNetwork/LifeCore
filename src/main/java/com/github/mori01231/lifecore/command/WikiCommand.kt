package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class WikiCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size == 1) {
            if (args[0].equals("unofficial", ignoreCase = true)) {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3非公式Wikiリンク: &f&l" + plugin.config.getString("unofficial-wiki-url")))
            } else if (args[0].equals("official", ignoreCase = true)) {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3公式Wikiリンク: &f&l" + plugin.config.getString("wiki-url")))
            }
        } else {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3非公式Wikiリンク: &f" + plugin.config.getString("unofficial-wiki-url")))
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&3&l公式Wikiリンク: &f&l" + plugin.config.getString("wiki-url")))
        }
        return true
    }
}
