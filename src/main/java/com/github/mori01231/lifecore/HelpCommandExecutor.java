package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3Help Page"));

        for (String line : LifeCore.getInstance().getConfig().getStringList("help.menu")) {
            sender.sendMessage(line);
        }
        /*
        List<String> configcontents;
        configcontents = LifeCore.getInstance().getConfig().getStringList("help.menu");

        for (String temp : configcontents) {

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',temp));
        }*/

        return true;
    }
}
