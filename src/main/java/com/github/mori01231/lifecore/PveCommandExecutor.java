package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class PveCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            String pve = LifeCore.getInstance().getConfig().getString("pve-teleport");
            getServer().dispatchCommand(getServer().getConsoleSender(), "mvtp " + player.getName() + " " + pve);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ダンジョンロビーにテレポートしました。" ));
        }
        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }


        return true;
    }
}
