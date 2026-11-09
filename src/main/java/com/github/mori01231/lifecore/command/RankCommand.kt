package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RankCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val legacy = LegacyComponentSerializer.legacyAmpersand()
        val usage = legacy.deserialize("&a/rank &3の後に1~10の数字を入れてください。例：&a/rank 3")
        if (args.size == 1) {
            val rank = args[0].toIntOrNull()
            if (rank == null) {
                sender.sendMessage(usage)
            }
            for (line in plugin.config.getStringList("Rank.Rank" + args[0])) {
                sender.sendMessage(legacy.deserialize(line))
            }
            if ((rank ?: 0) !in 1..10) {
                sender.sendMessage(usage)
            }
        } else {
            if (sender is Player) {
                for (i in 10 downTo 1) {
                    if (sender.hasPermission("lifecore.rank$i")) {
                        sender.sendMessage(legacy.deserialize("&3現在のランク：&b&lランク$i"))
                        for (line in plugin.config.getStringList("Rank.Rank$i")) {
                            sender.sendMessage(legacy.deserialize(line))
                        }
                        break
                    }
                }
            } else {
                sender.sendMessage(legacy.deserialize("&3このコマンドはコンソールから使用できません。"))
            }
        }
        return true
    }
}
