package com.github.mori01231.lifecore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class PveCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            String pve = LifeCore.getInstance().getConfig().getString("pve-teleport");
            getServer().dispatchCommand(getServer().getConsoleSender(), "mvtp " + player.getName() + " " + pve);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ダンジョンロビーにテレポートしました。" ));

            // create bytearray for sending player to server
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF("pve");
            } catch (IOException e) {
                // never happens
            }
        }
        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }


        return true;
    }
}
