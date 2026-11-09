package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class VoteCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bJMS: &f" + plugin.config.getString("vote-url-jms")))
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&aMinePortal: &f" + plugin.config.getString("vote-url-mineportal")))
        return true
    }
}
