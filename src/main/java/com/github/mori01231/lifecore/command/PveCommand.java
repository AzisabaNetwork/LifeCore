package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class PveCommand implements CommandExecutor {
    private final LifeCore plugin;
    
    public PveCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();

            if(plugin.getConfig().getBoolean("use-pve-command-as-teleport")){
                String pve = plugin.getConfig().getString("pve-teleport");
                getServer().dispatchCommand(getServer().getConsoleSender(), "mvtp " + playerName + " " + pve);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3ダンジョンロビーにテレポートしました。" ));

            } else {
                // create list of servers
                ArrayList<String> servers = new ArrayList<>();
                //servers.add("lifepve");
                servers.add("lifepve1");
                servers.add("lifepve2");
                servers.add("lifepve3");

                // create random index to select from servers
                Random random = new Random();
                int index = random.nextInt(servers.size());

                // create bytearray for sending player to server
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    // send player to random server
                    out.writeUTF("Connect");
                    out.writeUTF(servers.get(index));
                } catch (IOException e) {
                    // never happens
                }
                player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            }

        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }


        return true;
    }
}
