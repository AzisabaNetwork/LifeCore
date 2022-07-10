package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.TrashInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public class TrashCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if ((sender instanceof Player)) {
            Player p = (Player)sender;

            //Create inventory with TrashInventory holder
            Inventory trash = new TrashInventory().getInventory();

            //Here opens the inventory
            p.openInventory(trash);

        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }

        return true;
    }


}
