package com.github.mori01231.lifecore;

import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetRankMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements HttpHandler {
    private final String token;

    public RequestHandler(LifeCore plugin){
        this.token = plugin.getConfig().getString("http-server.token", "this-should-be-a-random-string");
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }
        int len = exchange.getRequestBody().available();
        byte[] bodyBytes = new byte[len];
        ByteStreams.readFully(exchange.getRequestBody(), bodyBytes);
        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        String[] args = body.split("\\|");
        if (args.length != 3) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }
        String playerName = args[0];
        long timestamp = Long.parseLong(args[1]);
        String providedToken = args[2];
        if (!providedToken.equals(token)) {
            // invalid token
            exchange.sendResponseHeaders(403, 0);
            exchange.close();
            return;
        }
        if (System.currentTimeMillis() - timestamp > 10000) {
            // expired request
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }
        if (!player.hasPermission("lifecore.noob")) {
            exchange.sendResponseHeaders(403, 0);
            exchange.close();
            return;
        }
        AziPluginMessaging api = AziPluginMessagingProvider.get();
        Protocol.P_SET_RANK.sendPacket(
                api.getServer().getPacketSender(),
                new ProxyboundSetRankMessage("rank1", api.getPlayerAdapter(Player.class).get(player)));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d" + playerName + "&dさんがチュートリアルを完了しました！ ようこそ&b&lLife鯖&dへ！"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp lifecore_onsubmit " + playerName);
        exchange.sendResponseHeaders(204, 0);
        exchange.getResponseBody().close();
    }
}
