package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.listener.VoteListener
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class DebugVoteCommand(private val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("Usage: /debugvote <player>")
            return true
        }
        val serviceName = args.getOrNull(1) ?: "example.com"
        VoteListener.processVotePacket(plugin, args[0], serviceName)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0]) }
        }
        if (args.size == 2) {
            return listOf("minecraft.jp", "mineportal.jp").filter { it.startsWith(args[1]) }
        }
        return emptyList()
    }
}
