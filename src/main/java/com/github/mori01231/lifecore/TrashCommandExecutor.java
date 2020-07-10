package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;


public class TrashCommandExecutor implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ((sender instanceof Player))
        {
            Player p = (Player)sender;

            //Create inventory with TrashInventory holder
            Inventory trash = Bukkit.createInventory(new TrashInventory(), 54, "ゴミ箱");

            //Here opens the inventory
            p.openInventory(trash);

        }
        else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3このコマンドはコンソールから使用できません。" ));
        }


        return true;
    }


}
