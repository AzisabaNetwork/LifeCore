package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.LegacyBookParser
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GuideCommand(private val plugin: LifeCore) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val legacy = LegacyComponentSerializer.legacyAmpersand()
        if (args.size == 1) {
            if (sender.hasPermission("lifecore.giveguide")) {
                val target = Bukkit.getPlayerExact(args[0])
                if (target == null) {
                    sender.sendMessage(legacy.deserialize("&c" + args[0] + "はオンラインではありません。"))
                    return true
                }
                val book = createGuide() ?: run {
                    sender.sendMessage(legacy.deserialize("&cガイドブックが設定されていません。"))
                    return true
                }
                give(target, book)
                sender.sendMessage(legacy.deserialize("&a&l" + target.name + "&3にガイドブックを授与しました。"))
            } else {
                sender.sendMessage(legacy.deserialize("&4権限不足です"))
            }
        } else {
            if (sender is Player) {
                val book = createGuide() ?: run {
                    sender.sendMessage(legacy.deserialize("&cガイドブックが設定されていません。"))
                    return true
                }
                give(sender, book)
                sender.sendMessage(legacy.deserialize("&3ガイドブックを入手しました。"))
            } else {
                sender.sendMessage(legacy.deserialize("&3このコマンドはコンソールから使用できません。"))
            }
        }
        return true
    }

    private fun createGuide(): ItemStack? {
        val guide = plugin.config.getString("guide")
        if (guide.isNullOrBlank()) return null
        return LegacyBookParser.parse(guide, plugin.logger)
    }

    private fun give(player: Player, item: ItemStack) {
        player.inventory.addItem(item).values.forEach { leftover ->
            player.world.dropItemNaturally(player.location, leftover)
        }
    }
}
