package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.config.VotesFile
import com.github.mori01231.lifecore.util.PlayerUtil
import com.vexsoftware.votifier.model.VotifierEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class VoteListener(private val plugin: LifeCore) : Listener {
    @EventHandler
    fun onVote(e: VotifierEvent) {
        processVotePacket(plugin, e.vote.username, e.vote.serviceName)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val uuid = e.player.uniqueId
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            val player = Bukkit.getPlayer(uuid)
            if (player == null || !player.isOnline) {
                return@Runnable
            }
            val count = VotesFile.getVotes(uuid.toString())
            VotesFile.setVotes(uuid.toString(), 0)
            processVotes(plugin, player, count)
        }, 20 * 10)
    }

    companion object {
        private val lock = Any()

        fun processVotePacket(plugin: LifeCore, username: String, serviceName: String) = synchronized(lock) {
            val player = Bukkit.getPlayerExact(username)
            Bukkit.broadcast(
                Component.text("[", NamedTextColor.GOLD)
                    .append(Component.text("Broadcast", NamedTextColor.DARK_RED))
                    .append(Component.text("] ", NamedTextColor.GOLD))
                    .append(Component.text("Thanks ", NamedTextColor.DARK_GREEN))
                    .append(Component.text(username, NamedTextColor.RED))
                    .append(Component.text(" for voting on $serviceName", NamedTextColor.DARK_GREEN))
            )
            if (player == null) {
                PlayerUtil.resolveUUIDAsync(plugin, username).thenAcceptAsync({ uuid ->
                    if (uuid == null) {
                        plugin.logger.warning("Could not resolve UUID for player $username")
                        return@thenAcceptAsync
                    }
                    VotesFile.increase(uuid.toString())
                    plugin.logger.info("Queued vote from $username (UUID: $uuid, service name: $serviceName)")
                }, plugin.asyncExecutor)
                return
            }
            player.sendMessage(Component.text(serviceName + "で投票していただきありがとうございます！", NamedTextColor.GREEN))
            val count = VotesFile.getVotes(player.uniqueId.toString()) + 1
            VotesFile.setVotes(player.uniqueId.toString(), 0)
            processVotes(plugin, player, count)
        }

        private fun processVotes(plugin: LifeCore, player: Player, count: Long) {
            plugin.logger.info("Processing vote from " + player.name + " (count: " + count + ")")
            for (i in 0 until count) {
                for (reward in plugin.voteConfig.rewards) {
                    reward.execute(plugin, player)
                }
            }
            player.sendMessage(Component.text(count.toString() + "回分の投票報酬を受け取りました。", NamedTextColor.GREEN))
        }
    }
}
