package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class KillNonAdminCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val players = mutableListOf<String>()
        for (player in Bukkit.getOnlinePlayers()) {
            // check if player has any item matching ItemUtil#isProbablyAdminSword in their inventory and kill if they have it
            if (player.hasPermission("lifecore.canuseadminsword")) continue
            if ((0 until player.inventory.size).any { ItemUtil.isProbablyAdminSword(player.inventory.getItem(it)) }) {
                players.add(player.name)
                player.health = 0.0
            }
        }
        sender.sendMessage("Killed " + players.size + " players: " + players.joinToString(", "))
        return true
    }
}
