package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

open class TransferCommand(private val plugin: LifeCore, private val server: String) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            connect(plugin, sender, server)
        } else {
            sender.sendMessage(Component.text("このコマンドはコンソールから使用できません。", NamedTextColor.DARK_AQUA))
        }
        return true
    }

    companion object {
        /**
         * Sends the player to the specified server via BungeeCord plugin messaging channel.
         */
        fun connect(plugin: Plugin, player: Player, server: String) {
            val b = ByteArrayOutputStream()
            DataOutputStream(b).use { out ->
                out.writeUTF("Connect")
                out.writeUTF(server)
            }
            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray())
        }
    }
}
