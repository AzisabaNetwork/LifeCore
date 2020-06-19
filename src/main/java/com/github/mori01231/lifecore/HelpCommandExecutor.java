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
            else if (args[0].equalsIgnoreCase("town")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Town")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("money")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Money")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("rank")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Rank")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("dungeon")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Dungeon")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("command")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Command")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("pet")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Pet")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("mcmmo")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Mcmmo")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("recipe")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Recipe")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
            else if (args[0].equalsIgnoreCase("enchant")) {
                for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Enchant")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
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
