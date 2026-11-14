package com.github.mori01231.lifecore.dice

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KotlinDiceCommand(private val listener: KotlinDiceItemListener) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("lifecore.givedice")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c権限がありません。"))
            return true
        }
        if (sender !is Player) {
            sender.sendMessage("プレイヤーからのみ実行できます。")
            return true
        }
        sender.inventory.addItem(listener.createDiceItem())
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a抽選ダイスを取得しました！"))
        return true
    }
}
