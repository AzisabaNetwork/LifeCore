package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class RankCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;

            if(sender.hasPermission("lifecore.rank10")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank10")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank9")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank9")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank8")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank8")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank7")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank7")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank6")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank6")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank5")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank5")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank4")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank4")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank3")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank3")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank2")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank2")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.rank1")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Rank1")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if(sender.hasPermission("lifecore.member")){
                for (String line : LifeCore.getInstance().getConfig().getStringList("Rank.Member")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }


            //Show rank and what you can do.

        }
        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }

        return true;
    }
}
