package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class GuideCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(args.length == 1){
            if(sender.hasPermission("lifecore.giveguide")){

                String guide = LifeCore.getInstance().getConfig().getString("guide");
                getServer().dispatchCommand(getServer().getConsoleSender(), "minecraft:give " + args[0] + " " + guide);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&a&l" + args[0] +"&3にガイドブックを授与しました。" ));
            }
            else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&a&l" + args[0] +"&4権限不足です" ));
            }
        }

        else{
            if (sender instanceof Player){
                Player player = (Player) sender;
                String guide = LifeCore.getInstance().getConfig().getString("guide");
                getServer().dispatchCommand(getServer().getConsoleSender(), "minecraft:give " + player.getName() + " " + guide);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ガイドブックを入手しました。" ));
            }
            else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
            }

        }



        return true;
    }
}
