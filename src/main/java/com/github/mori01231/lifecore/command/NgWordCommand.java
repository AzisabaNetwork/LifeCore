package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NgWordCommand implements TabExecutor {
    private final LifeCore plugin;

    public NgWordCommand(LifeCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Component.text("/ngword (add|remove|list)", NamedTextColor.RED));
            return true;
        }
        Player player = (Player) sender;
        switch (args[0]) {
            case "add":
                if (args.length == 1) {
                    sender.sendMessage(Component.text("/ngword add <word>", NamedTextColor.RED));
                    return true;
                }
                plugin.ngWordsCache.add(player.getUniqueId(), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                player.sendMessage(Component.text("NGワードを追加しました。", NamedTextColor.GREEN));
                break;
            case "remove":
                if (args.length == 1) {
                    sender.sendMessage(Component.text("/ngword remove <word>", NamedTextColor.RED));
                    return true;
                }
                plugin.ngWordsCache.remove(player.getUniqueId(), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                player.sendMessage(Component.text("NGワードを削除しました。", NamedTextColor.GREEN));
                break;
            case "clear":
                plugin.ngWordsCache.clear(player.getUniqueId());
                player.sendMessage(Component.text("NGワードを全て削除しました。", NamedTextColor.GREEN));
                break;
            case "list":
                player.sendMessage(Component.text("NGワード一覧:", NamedTextColor.GREEN));
                for (String word : plugin.ngWordsCache.get(player.getUniqueId())) {
                    player.sendMessage(Component.text(word, NamedTextColor.RED));
                }
                break;
            default:
                player.sendMessage(Component.text("/ngword (add|remove|list)", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("add", "remove", "clear", "list")
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        if (sender instanceof Player && args[0].equals("remove")) {
            return plugin.ngWordsCache.get(((Player) sender).getUniqueId())
                    .stream()
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
