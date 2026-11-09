package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

/**
 * `/protect`: toggles drop/trash protection for the kind of item held in the main hand.
 *
 * Works for any item, including items without a PvE level. See [ItemUtil.getProtectKey]
 * for how the "kind" of the item is determined.
 */
class ProtectCommand(private val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("This command can only be executed by players.", NamedTextColor.RED))
            return true
        }
        val key = ItemUtil.getProtectKey(sender.inventory.itemInMainHand)
        if (key == null) {
            sender.sendMessage(Component.text("手にアイテムを持ってください。", NamedTextColor.RED))
            return true
        }
        if (plugin.mythicItemProtectConfig.toggle(sender.uniqueId, key)) {
            sender.sendMessage(Component.text("アイテム \"$key\" のドロップ/ゴミ箱保護を有効にしました。", NamedTextColor.GREEN))
            if (key.startsWith("minecraft:")) {
                sender.sendMessage(Component.text("※同じ種類のアイテム($key)はすべて保護されます。", NamedTextColor.YELLOW))
            }
        } else {
            sender.sendMessage(Component.text("アイテム \"$key\" のドロップ/ゴミ箱保護を無効にしました。", NamedTextColor.RED))
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> =
        emptyList()
}
