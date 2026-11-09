package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.gui.DropProtectScreen
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class DropProtectCommand(private val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by players.")
            return true
        }
        sender.openInventory(DropProtectScreen(plugin, sender).inventory)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> =
        emptyList()
}
