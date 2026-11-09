package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.network.PacketHandler
import com.github.mori01231.lifecore.util.ItemUtil
import com.github.mori01231.lifecore.util.PlayerUtil
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.UUID

class PlayerJoinListener(private val plugin: LifeCore) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerPreLogin(e: AsyncPlayerPreLoginEvent) {
        if (e.loginResult == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // preload ng word data
            plugin.ngWordsCache.loadAsync(e.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        // spawn on join
        if (plugin.config.getBoolean("spawnOnJoin", false)) {
            Bukkit.dispatchCommand(e.player, "spawn")
        }

        // inject channel handler
        PlayerUtil.getChannel(e.player).pipeline()
            .addBefore("packet_handler", "lifecore", PacketHandler(e.player))

        val uuid = e.player.uniqueId
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { restoreItemTags(uuid) }, 20 * 2) // 2 seconds
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { restoreItemTags(uuid) }, 20 * 10) // 10 seconds
    }

    private fun restoreItemTags(uuid: UUID) {
        val player = Bukkit.getPlayer(uuid)
        if (player == null || !player.isOnline) {
            return
        }
        if (!plugin.config.getBoolean("enable-backup-item-tag", true)) {
            return
        }
        for (i in 0 until player.inventory.size) {
            player.inventory.setItem(i, ItemUtil.restoreTag(player.inventory.getItem(i)))
        }
        for (i in 0 until player.enderChest.size) {
            player.enderChest.setItem(i, ItemUtil.restoreTag(player.enderChest.getItem(i)))
        }
    }
}
