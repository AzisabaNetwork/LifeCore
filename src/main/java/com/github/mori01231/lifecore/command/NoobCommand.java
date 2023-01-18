package com.github.mori01231.lifecore.command;

import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetRankMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoobCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        String playerName = player.getName();

        // if the player has already finished the tutorial once, don't execute the command
        if (!player.hasPermission("lifecore.noob")) {
            return true;
        }

        AziPluginMessaging api = AziPluginMessagingProvider.get();
        Protocol.P_SET_RANK.sendPacket(
                api.getServer().getPacketSender(),
                new ProxyboundSetRankMessage("rank1", api.getPlayerAdapter(Player.class).get(player)));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d" + playerName + "&dさんがチュートリアルを完了しました！ ようこそ&b&lLife鯖&dへ！"));
        return true;
    }
}
