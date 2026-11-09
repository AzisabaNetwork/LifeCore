package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GuideCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val legacy = LegacyComponentSerializer.legacyAmpersand()
        if (args.size == 1) {
            if (sender.hasPermission("lifecore.giveguide")) {
                val guide = plugin.config.getString("guide")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give " + args[0] + " " + guide)
                sender.sendMessage(legacy.deserialize("&a&l" + args[0] + "&3にガイドブックを授与しました。"))
            } else {
                sender.sendMessage(legacy.deserialize("&4権限不足です"))
            }
        } else {
            if (sender is Player) {
                val guide = plugin.config.getString("guide")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give " + sender.name + " " + guide)
                sender.sendMessage(legacy.deserialize("&3ガイドブックを入手しました。"))
            } else {
                sender.sendMessage(legacy.deserialize("&3このコマンドはコンソールから使用できません。"))
            }
        }
        return true
    }
}
