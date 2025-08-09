package com.github.mori01231.lifecore.listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object WorkaroundLuckPerms : Listener {
    private var lastExecuted = 0L

    @EventHandler
    fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
        if (System.currentTimeMillis() - lastExecuted < 1000 * 60 * 60) return
        if (e.message == "/spawn" && !e.player.hasPermission("essentials.spawn")) {
            lastExecuted = System.currentTimeMillis()
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp sync")
        }
    }
}
