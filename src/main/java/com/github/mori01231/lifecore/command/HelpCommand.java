package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public class HelpCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public HelpCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("menu")) {
                for (String line : plugin.getConfig().getStringList("Help.Menu")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("town")) {
                for (String line : plugin.getConfig().getStringList("Help.Town")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("money")) {
                for (String line : plugin.getConfig().getStringList("Help.Money")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("rank")) {
                for (String line : plugin.getConfig().getStringList("Help.Rank")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("dungeon")) {
                for (String line : plugin.getConfig().getStringList("Help.Dungeon")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("command")) {
                for (String line : plugin.getConfig().getStringList("Help.Command")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("pet")) {
                for (String line : plugin.getConfig().getStringList("Help.Pet")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("mcmmo")) {
                for (String line : plugin.getConfig().getStringList("Help.Mcmmo")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("recipe")) {
                for (String line : plugin.getConfig().getStringList("Help.Recipe")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("enchant")) {
                for (String line : plugin.getConfig().getStringList("Help.Enchant")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("vote")) {
                for (String line : plugin.getConfig().getStringList("Help.Vote")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("basics")) {
                for (String line : plugin.getConfig().getStringList("Help.Basics")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            } else if (args[0].equalsIgnoreCase("auction")) {
                for (String line : plugin.getConfig().getStringList("Help.Auction")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
                }
            }
        } else {
            for (String line : plugin.getConfig().getStringList("Help.Menu")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
            }
        }


        return true;
    }
}
