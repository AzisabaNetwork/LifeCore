package com.github.mori01231.lifecore.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

abstract class PlayerTabExecutor : TabExecutor {
    final override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            return execute(sender, args)
        }
        sender.sendMessage("This command is only available for players.")
        return true
    }

    abstract fun execute(player: Player, args: Array<String>): Boolean

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? =
        emptyList()
}
