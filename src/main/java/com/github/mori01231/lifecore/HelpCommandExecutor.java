package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class HelpCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 1){
            if (args[0].equalsIgnoreCase("menu")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Menu")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("official")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3公式Wikiリンク: &f&l" + LifeCore.getInstance().getConfig().getString("wiki-url") ));
            }
        }

        else{
            for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Menu")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
            }
        }


        return true;
    }
}
