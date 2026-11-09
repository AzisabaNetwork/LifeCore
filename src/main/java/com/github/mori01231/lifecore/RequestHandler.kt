package com.github.mori01231.lifecore

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider
import net.azisaba.azipluginmessaging.api.protocol.Protocol
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetRankMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.IOException

class RequestHandler(private val plugin: LifeCore) : HttpHandler {
    private val token: String = plugin.config.getString("http-server.token", "this-should-be-a-random-string")!!

    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        if (exchange.requestMethod != "POST") {
            exchange.sendResponseHeaders(405, 0)
            exchange.close()
            return
        }
        val len = exchange.requestBody.available()
        val body = exchange.requestBody.readNBytes(len).toString(Charsets.UTF_8)
        val args = body.split("|")
        if (args.size != 3) {
            exchange.sendResponseHeaders(400, 0)
            exchange.close()
            return
        }
        val playerName = args[0]
        val timestamp = args[1].toLong()
        val providedToken = args[2]
        if (providedToken != token) {
            // invalid token
            exchange.sendResponseHeaders(403, 0)
            exchange.close()
            return
        }
        if (System.currentTimeMillis() - timestamp > 10000) {
            // expired request
            exchange.sendResponseHeaders(400, 0)
            exchange.close()
            return
        }
        val player = Bukkit.getPlayerExact(playerName)
        if (player == null) {
            exchange.sendResponseHeaders(404, 0)
            exchange.close()
            return
        }
        if (!player.hasPermission("lifecore.noob")) {
            exchange.sendResponseHeaders(403, 0)
            exchange.close()
            return
        }
        val api = AziPluginMessagingProvider.get()
        Protocol.P_SET_RANK.sendPacket(
            api.server.packetSender,
            ProxyboundSetRankMessage("rank1", api.getPlayerAdapter(Player::class.java).get(player))
        )
        Bukkit.broadcast(
            LegacyComponentSerializer.legacyAmpersand()
                .deserialize("&d$playerName&dさんがチュートリアルを完了しました！ ようこそ&b&lLife鯖&dへ！")
        )
        Bukkit.getScheduler().runTask(plugin, Runnable {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp lifecore_onsubmit $playerName")
        })
        exchange.sendResponseHeaders(204, 0)
        exchange.responseBody.close()
    }
}
