package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class HelpCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val section = if (args.size == 1) {
            SECTIONS.firstOrNull { it.equals(args[0], ignoreCase = true) } ?: return true
        } else {
            "Menu"
        }
        for (line in plugin.config.getStringList("Help.$section")) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(line))
        }
        return true
    }

    companion object {
        /** Section names under "Help" in config.yml. "/help <name>" is matched case-insensitively. */
        private val SECTIONS = listOf(
            "Menu", "Town", "Money", "Rank", "Dungeon", "Command", "Pet",
            "Mcmmo", "Recipe", "Enchant", "Vote", "Basics", "Auction",
        )
    }
}
