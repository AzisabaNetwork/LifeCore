package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.UUID

class GCListenerRestartExtendTimeCommand(private val plugin: LifeCore) : TabExecutor {
    private val players = mutableSetOf<UUID>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!plugin.gcListener.isTriggered) return true
        if (sender !is Player) return true
        if (!players.add(sender.uniqueId)) {
            sender.sendMessage(Component.text("すでに投票済みです。", NamedTextColor.RED))
            return true
        }
        sender.sendMessage(Component.text("投票しました。", NamedTextColor.GREEN))
        if (sender.hasPermission("lifecore.extend-time-immediately") || players.size == 5) {
            ScheduleRestartCommand.schedule(15 * 60)
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> =
        emptyList()
}
