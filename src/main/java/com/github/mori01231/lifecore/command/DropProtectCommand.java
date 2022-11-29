package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.config.DropProtectFile;
import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DropProtectCommand implements TabExecutor {
    private final LifeCore plugin;

    public DropProtectCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            DropProtectFile.load(plugin);
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1) {
                return false;
            }
            DropProtectFile.add(args[1]);
            return true;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1) {
                return false;
            }
            DropProtectFile.remove(args[1]);
            return true;
        }
        if (args[0].equalsIgnoreCase("check")) {
            if (args.length == 1) {
                return false;
            }
            sender.sendMessage(String.valueOf(DropProtectFile.isProtected(args[1])));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(Stream.of("reload", "add", "remove", "check"), args[0]).collect(Collectors.toList());
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                return filter(DropProtectFile.getSet().stream(), args[1]).collect(Collectors.toList());
            } else if ((args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("check")) && sender instanceof Player) {
                Player player = (Player) sender;
                String mainHand = ItemUtil.getMythicType(player.getInventory().getItemInMainHand());
                String offHand = ItemUtil.getMythicType(player.getInventory().getItemInOffHand());
                return filter(Stream.of(mainHand, offHand).filter(Objects::nonNull), args[1]).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private static Stream<String> filter(@NotNull Stream<String> stream, String str) {
        return stream.filter(s -> s.toLowerCase().startsWith(str.toLowerCase()));
    }
}
