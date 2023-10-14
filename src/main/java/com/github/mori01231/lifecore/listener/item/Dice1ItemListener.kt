package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.security.SecureRandom

class Dice1ItemListener(val plugin: LifeCore) : Listener {
    private val random = SecureRandom()
    private val itemId = "b46ed137-a581-4ee8-b714-c1e136d141f2"
    private val prefix = "§f§l【§6§l抽選§f§l】"

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.player.gameMode == GameMode.SPECTATOR) {
            return
        }
        if (itemId != ItemUtil.getStringTag(e.item, "LifeItemId")) {
            // wrong item
            return
        }
        fun rollNow() {
            val randomPlayer =
                e.player
                    .getNearbyEntities(50.0, 50.0, 50.0)
                    .filterIsInstance<Player>()
                    .filter { it.gameMode != GameMode.SPECTATOR && !it.hasMetadata("vanished") }
                    .let { if (it.isEmpty()) null else it[random.nextInt(it.size)] }
            e.player.sendMessageToNearbyPlayers("§f§l${e.player.name}§6§lの抽選ダイスの結果は§f§l${randomPlayer?.name}§6§lさんになりました！")
        }
        if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) {
            return rollNow()
        }
        e.player.sendMessageToNearbyPlayers("$prefix§6§l${e.player.name}§f§lの§a§l抽§b§l選§c§lま§a§lで§b§lえ§c§lえ§a§l.§b§l.§c§l.§a§l.§b§l!§c§l!§a§l!§b§l!§c§l!§a§l!§b§l!")
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                e.player.sendMessageToNearbyPlayers("$prefix§a§l3!!!")
            })
        }, 19)
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                e.player.sendMessageToNearbyPlayers("$prefix§b§l2!!!")
            })
        }, 39)
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                e.player.sendMessageToNearbyPlayers("$prefix§c§l1!!!")
            })
        }, 59)
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                rollNow()
            })
        }, 79)
    }

    private fun Player.sendMessageToNearbyPlayers(msg: String) {
        sendMessage(msg)
        getNearbyEntities(50.0, 50.0, 50.0).filterIsInstance<Player>().forEach { player ->
            player.sendMessage(msg)
        }
    }
}
