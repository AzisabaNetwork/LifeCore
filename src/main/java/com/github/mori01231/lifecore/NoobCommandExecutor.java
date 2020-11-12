package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class NoobCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;

            String playerName = player.getName();

            // if the player has already finished the tutorial once, don't execute the command
            if(player.hasPermission("lifecore.noob") == false){
                return true;
            }

            getServer().dispatchCommand(getServer().getConsoleSender(), "lp u " + playerName + " parent add rank1 server=life");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',"&d" + playerName + "&dさんが初参加しました！ ようこそ&b&lLife鯖&dへ！"));
            return true;
        }

        return true;
    }
}
