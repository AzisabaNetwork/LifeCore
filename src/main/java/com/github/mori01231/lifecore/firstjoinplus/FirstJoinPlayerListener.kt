package com.github.mori01231.lifecore.firstjoinplus

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

class FirstJoinPlayerListener(private val integration: FirstJoinPlusIntegration) : Listener {

    private val plugin = integration.plugin

    @EventHandler(priority = EventPriority.HIGHEST)
    fun firstJoinDetection(event: PlayerJoinEvent) {
        val player = event.player
        var existingPlayer = player.hasPlayedBefore()
        if (integration.manager.config.getBoolean("settings.every-join-is-first-join")) {
            existingPlayer = false
        }

        if (integration.manager.config.getBoolean("on-first-join.reset-state.enabled")) {
            FirstJoinPlusUtilities.debugPlayer(player, false)
        }

        if (!existingPlayer) {
            plugin.server.pluginManager.callEvent(FirstJoinEvent(event))
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            if (integration.manager.godMode.contains((event.entity as Player).name)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            if (integration.manager.noPVP.contains((event.entity as Player).name) ||
                integration.manager.noPVP.contains((event.damager as Player).name)
            ) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (integration.manager.config.getBoolean("other-messages.join-message.enabled")) {
            if (event.player.hasPlayedBefore() ||
                (!integration.manager.config.getBoolean("on-first-join.first-join-message.enabled") && !event.player.hasPlayedBefore())
            ) {
                val message = integration.manager.config.getString("other-messages.join-message.message")
                if (!message.equals("%none", ignoreCase = true)) {
                    event.setJoinMessage(FirstJoinPlusUtilities.replaceVariables(message ?: "", event.player))
                } else {
                    event.setJoinMessage("")
                }
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (integration.manager.config.getBoolean("other-messages.quit-message.enabled")) {
            val message = integration.manager.config.getString("other-messages.quit-message.message")
            if (!message.equals("%none", ignoreCase = true)) {
                event.setQuitMessage(FirstJoinPlusUtilities.replaceVariables(message ?: "", event.player))
            } else {
                event.setQuitMessage("")
            }
        }
    }

    @EventHandler
    fun onPlayerKick(event: PlayerKickEvent) {
        if (integration.manager.config.getBoolean("other-messages.kick-message.enabled")) {
            val message = integration.manager.config.getString("other-messages.kick-message.message")
            if (!message.equals("%none", ignoreCase = true)) {
                event.setLeaveMessage(FirstJoinPlusUtilities.replaceVariables(message ?: "", event.player, event.reason))
            } else {
                event.setLeaveMessage("")
            }
        }
    }
}
