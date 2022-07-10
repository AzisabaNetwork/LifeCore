package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KillNonAdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            // check if player has any item matching ItemUtil#isProbablyAdminSword in their inventory and kill if they have it
            if (!player.hasPermission("lifecore.canuseadminsword")) {
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    if (ItemUtil.isProbablyAdminSword(player.getInventory().getItem(i))) {
                        players.add(player.getName());
                        player.setHealth(0);
                        break;
                    }
                }
            }
        }
        sender.sendMessage("Killed " + players.size() + " players: " + String.join(", ", players));
        return true;
    }
}
