package com.github.mori01231.lifecore.command;

import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerWithServerMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaraCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            AziPluginMessaging api = AziPluginMessagingProvider.get();
            Protocol.P_TOGGLE_SARA_SHOW.sendPacket(api.getServer().getPacketSender(), new PlayerWithServerMessage(api.getPlayerAdapter(Player.class).get((Player) sender)));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }
        return true;
    }
}
