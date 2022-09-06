package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VoteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&bJMS: &f" + LifeCore.getInstance().getConfig().getString("vote-url-jms") ));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aMonocraft: &f" + LifeCore.getInstance().getConfig().getString("vote-url-monocraft") ));

        return true;
    }
}
