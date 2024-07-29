package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import com.github.mori01231.lifecore.util.JoinFilterUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TownServerCommand(private val plugin: LifeCore) : TransferCommand(plugin, "lifetown") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return false
        if (plugin.config.getBoolean("enable-backup-item-tag", true)) {
            val size = player.inventory.size
            for (i in 0..<size) {
                player.inventory.setItem(i, ItemUtil.restoreTag(player.inventory.getItem(i)))
                player.inventory.setItem(i, ItemUtil.backupTag(player.inventory.getItem(i)))
            }
        }
        JoinFilterUtil.addPlayerWithExpire(player.uniqueId, "lifetown", 1)
        super.onCommand(sender, command, label, args)
        return true
    }
}
