package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.listener.VoteListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DebugVoteCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /debugvote <player>");
            return true;
        }
        String serviceName;
        if (args.length == 1) {
            serviceName = "example.com";
        } else {
            serviceName = args[1];
        }
        VoteListener.processVotePacket(args[0], serviceName);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName)
                    .filter(name -> name.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Stream.of("minecraft.jp", "monocraft.net")
                    .filter(name -> name.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
