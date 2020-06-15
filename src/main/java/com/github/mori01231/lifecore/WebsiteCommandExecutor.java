package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WebsiteCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ホームページ: &f&l" + LifeCore.getInstance().getConfig().getString("website-url")));

        return true;
    }
}
