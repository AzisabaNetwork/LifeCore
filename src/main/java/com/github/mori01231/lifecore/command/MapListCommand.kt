package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.gui.MapListScreen
import org.bukkit.entity.Player

object MapListCommand : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<out String>): Boolean {
        MapListScreen(player).openAsync()
        return true
    }
}