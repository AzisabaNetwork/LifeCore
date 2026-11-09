package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class NgWordCommand(private val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage(Component.text("/ngword (add|remove|list)", NamedTextColor.RED))
            return true
        }
        val word = args.drop(1).joinToString(" ")
        when (args[0]) {
            "add" -> {
                if (args.size == 1) {
                    sender.sendMessage(Component.text("/ngword add <word>", NamedTextColor.RED))
                    return true
                }
                plugin.ngWordsCache.add(sender.uniqueId, word)
                sender.sendMessage(Component.text("NGワードを追加しました。", NamedTextColor.GREEN))
            }
            "remove" -> {
                if (args.size == 1) {
                    sender.sendMessage(Component.text("/ngword remove <word>", NamedTextColor.RED))
                    return true
                }
                plugin.ngWordsCache.remove(sender.uniqueId, word)
                sender.sendMessage(Component.text("NGワードを削除しました。", NamedTextColor.GREEN))
            }
            "clear" -> {
                plugin.ngWordsCache.clear(sender.uniqueId)
                sender.sendMessage(Component.text("NGワードを全て削除しました。", NamedTextColor.GREEN))
            }
            "list" -> {
                sender.sendMessage(Component.text("NGワード一覧:", NamedTextColor.GREEN))
                for (ngWord in plugin.ngWordsCache.get(sender.uniqueId)) {
                    sender.sendMessage(Component.text(ngWord, NamedTextColor.RED))
                }
            }
            else -> sender.sendMessage(Component.text("/ngword (add|remove|list)", NamedTextColor.RED))
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (args.size == 1) {
            return listOf("add", "remove", "clear", "list").filter { it.startsWith(args[0]) }
        }
        if (sender is Player && args[0] == "remove") {
            return plugin.ngWordsCache.get(sender.uniqueId).filter { it.startsWith(args[1]) }
        }
        return emptyList()
    }
}
