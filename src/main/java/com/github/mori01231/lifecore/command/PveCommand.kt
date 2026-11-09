package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PveCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("このコマンドはコンソールから使用できません。", NamedTextColor.DARK_AQUA))
            return true
        }
        if (plugin.config.getBoolean("use-pve-command-as-teleport")) {
            val pve = plugin.config.getString("pve-teleport")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp " + sender.name + " " + pve)
            sender.sendMessage(Component.text("ダンジョンロビーにテレポートしました。", NamedTextColor.DARK_AQUA))
        } else {
            // send player to random server
            TransferCommand.connect(plugin, sender, SERVERS.random())
        }
        return true
    }

    companion object {
        private val SERVERS = listOf("lifepve1", "lifepve2", "lifepve3")
    }
}
