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
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank9")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank8")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank7")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank6")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank5")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank4")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank3")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank2")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.rank1")){
                //Show rank and what you can do.
            }
            else if(sender.hasPermission("lifecore.member")){
                //Show rank and what you can do.
            }


            //Show rank and what you can do.

        }
        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }

        return true;
    }
}
