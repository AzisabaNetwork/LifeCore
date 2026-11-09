package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TutorialCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            val tutorial = plugin.config.getString("tutorial-teleport")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp " + sender.name + " " + tutorial)
            sender.sendMessage(Component.text("任意チュートリアルにテレポートしました。", NamedTextColor.DARK_AQUA))
        } else {
            sender.sendMessage(Component.text("このコマンドはコンソールから使用できません。", NamedTextColor.DARK_AQUA))
        }
        return true
    }
}
