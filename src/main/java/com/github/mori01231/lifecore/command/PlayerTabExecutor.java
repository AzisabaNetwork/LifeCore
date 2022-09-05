package com.github.mori01231.lifecore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerTabExecutor extends TabExecutor {
    @Override
    default boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return execute(player, args);
        } else {
            sender.sendMessage("This command is only available for players.");
            return true;
        }
    }

    boolean execute(@NotNull Player player, @NotNull String [] args);

    @Override
    default @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
