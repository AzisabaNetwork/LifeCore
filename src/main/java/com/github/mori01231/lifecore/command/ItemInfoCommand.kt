package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * 旧 ItemInfoGetter プラグインの /iteminfo コマンド。
 * メインハンドのアイテム情報を表示する。
 */
class ItemInfoCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return false
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return false

        val displayName = meta.displayName
        val customModelData = if (meta.hasCustomModelData()) meta.customModelData else 0
        val hideFlags = meta.itemFlags.toString()
            .replace("[", "")
            .replace("]", "")
        val unbreakable = if (meta.isUnbreakable) "true" else "false"
        val material = item.type.name
        val mmid = ItemUtil.getMythicType(item)

        player.sendMessage(ChatColor.AQUA.toString() + "----------Item Info----------")
        player.sendMessage(ChatColor.AQUA.toString() + "DisplayName :" + displayName)
        player.sendMessage(ChatColor.AQUA.toString() + "mmid :" + mmid)
        player.sendMessage(ChatColor.AQUA.toString() + "Material :" + material)
        player.sendMessage(ChatColor.AQUA.toString() + "CustomModelData :" + customModelData)
        player.sendMessage(ChatColor.AQUA.toString() + "Unbreakable :" + unbreakable)
        player.sendMessage(ChatColor.AQUA.toString() + "HideFlags :" + hideFlags)
        player.sendMessage(ChatColor.AQUA.toString() + "---------------------------")
        return true
    }
}
