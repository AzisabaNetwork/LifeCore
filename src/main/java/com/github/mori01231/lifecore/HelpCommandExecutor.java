package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class HelpCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        for (String line : LifeCore.getInstance().getConfig().getStringList("Help.Menu")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
        }

        return true;
    }
}
