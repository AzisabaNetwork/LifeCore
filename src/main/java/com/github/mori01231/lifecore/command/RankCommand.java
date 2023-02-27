package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public RankCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(args.length == 1){

            int foo = 0;
            try {
                foo = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&a/rank &3の後に1~10の数字を入れてください。例：&a/rank 3"));
            }
            for (String line : plugin.getConfig().getStringList("Rank.Rank" + args[0])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
            if (!(foo <= 10 && foo >= 1)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&a/rank &3の後に1~10の数字を入れてください。例：&a/rank 3"));
            }

        } else {
            if (sender instanceof Player){
                for (int i = 10; i >= 1; i--) {
                    if (sender.hasPermission("lifecore.rank" + i)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3現在のランク：&b&lランク" + i));
                        for (String line : plugin.getConfig().getStringList("Rank.Rank" + i)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                        }
                        break;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
            }
        }

        return true;
    }
}
