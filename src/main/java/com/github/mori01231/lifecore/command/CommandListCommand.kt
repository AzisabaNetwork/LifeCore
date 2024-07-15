package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.gui.CommandListScreen
import org.bukkit.entity.Player

object CommandListCommand : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<out String>): Boolean {
        player.openInventory(CommandListScreen(player).inventory)
        return true
    }
}