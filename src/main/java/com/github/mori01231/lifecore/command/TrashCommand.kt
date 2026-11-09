package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.TrashInventory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TrashCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            sender.openInventory(TrashInventory().inventory)
        } else {
            sender.sendMessage(Component.text("このコマンドはコンソールから使用できません。", NamedTextColor.DARK_AQUA))
        }
        return true
    }
}
