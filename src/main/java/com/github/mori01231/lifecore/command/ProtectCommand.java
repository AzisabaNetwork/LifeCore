package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ProtectCommand implements TabExecutor {
    private final LifeCore plugin;

    public ProtectCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }
        
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "手にアイテムを持ってください。");
            return true;
        }
        
        String mythicType = ItemUtil.getMythicType(item);
        if (mythicType == null) {
            player.sendMessage(ChatColor.RED + "このアイテムはMythicMobsアイテムではありません。");
            return true;
        }
        
        if (plugin.getMythicItemProtectConfig().toggle(player.getUniqueId(), mythicType)) {
            player.sendMessage(ChatColor.GREEN + "アイテム \"" + mythicType + "\" のドロップ/ゴミ箱保護を有効にしました。");
        } else {
            player.sendMessage(ChatColor.RED + "アイテム \"" + mythicType + "\" のドロップ/ゴミ箱保護を無効にしました。");
        }
        
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}