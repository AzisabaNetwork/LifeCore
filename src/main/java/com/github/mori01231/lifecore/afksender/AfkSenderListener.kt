package com.github.mori01231.lifecore.afksender

import com.earth2me.essentials.Essentials
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.command.TransferCommand
import net.ess3.api.events.AfkStatusChangeEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

/**
 * Port of the standalone AfkSender plugin.
 *
 * Sends players to a configured AFK server when EssentialsX marks them as AFK, and
 * optionally treats a yaw (rotation) change as activity to prevent water-flow AFK abuse.
 */
class AfkSenderListener(
    private val plugin: LifeCore,
    initialServerNames: List<String>,
    initialCancelAfkOnRotate: Boolean,
) : Listener {
    var serverNames: List<String> = initialServerNames
        private set
    var cancelAfkOnRotate: Boolean = initialCancelAfkOnRotate
        private set

    fun update(serverNames: List<String>, cancelAfkOnRotate: Boolean) {
        this.serverNames = serverNames
        this.cancelAfkOnRotate = cancelAfkOnRotate
    }

    private var essentials: Essentials? = Bukkit.getPluginManager().getPlugin("Essentials") as? Essentials

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (!cancelAfkOnRotate) return

        // To prevent players from abusing the water flow, we check the rotation change.
        // This only works if you set cancelAfkOnMove=false in the EssentialsX config.
        val ess = essentials
        if (ess != null && e.from.yaw != e.to.yaw) {
            ess.getUser(e.player).updateActivity(false, AfkStatusChangeEvent.Cause.MOVE)
        } else if (ess == null) {
            essentials = Bukkit.getPluginManager().getPlugin("Essentials") as? Essentials
        }
    }

    @EventHandler
    fun onAfkStatusChange(e: AfkStatusChangeEvent) {
        // Return if the player is back from AFK.
        if (!e.value) return

        val player = e.affected.base
        if (player == null || player.hasPermission(BYPASS_PERMISSION) || player.hasPermission(LEGACY_BYPASS_PERMISSION)) return
        if (serverNames.isEmpty()) return

        player.sendMessage(
            Component.text("離席状態になったため、自動的にAFKサーバーに転送されました。", NamedTextColor.GOLD),
        )

        // If multiple servers are configured, try them in order with a short delay.
        serverNames.forEachIndexed { index, serverName ->
            Bukkit.getScheduler().runTaskLater(
                plugin,
                Runnable {
                    if (!player.isOnline) return@Runnable
                    TransferCommand.connect(plugin, player, serverName)
                },
                index * 20L,
            )
        }
    }

    companion object {
        const val BYPASS_PERMISSION = "lifecore.afk.bypass"

        @Deprecated("Legacy AfkSender permission; kept for compatibility during migration")
        const val LEGACY_BYPASS_PERMISSION = "afksender.bypass"
    }
}
