package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WikiCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(args.length == 1){
            if (args[0].equalsIgnoreCase("unofficial")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3非公式Wikiリンク: &f&l" + LifeCore.getInstance().getConfig().getString("unofficial-wiki-url") ));
            }
            else if (args[0].equalsIgnoreCase("official")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3公式Wikiリンク: &f&l" + LifeCore.getInstance().getConfig().getString("wiki-url") ));
            }
        }

        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3非公式Wikiリンク: &f" + LifeCore.getInstance().getConfig().getString("unofficial-wiki-url") ));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3&l公式Wikiリンク: &f&l" + LifeCore.getInstance().getConfig().getString("wiki-url") ));
        }


        return true;
    }
}
