package com.github.mori01231.lifecore.command

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider
import net.azisaba.azipluginmessaging.api.protocol.Protocol
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetRankMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NoobCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val player = sender as? Player ?: return true
        // if the player has already finished the tutorial once, don't execute the command
        if (!player.hasPermission("lifecore.noob")) {
            return true
        }
        val api = AziPluginMessagingProvider.get()
        Protocol.P_SET_RANK.sendPacket(
            api.server.packetSender,
            ProxyboundSetRankMessage("rank1", api.getPlayerAdapter(Player::class.java).get(player))
        )
        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize("&d" + player.name + "&dさんがチュートリアルを完了しました！ ようこそ&b&lLife鯖&dへ！"))
        return true
    }
}
