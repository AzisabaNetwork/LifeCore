package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.ChatColor;
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
            sender.sendMessage(ChatColor.RED + "/ngword (add|remove|list)");
            return true;
        }
        Player player = (Player) sender;
        switch (args[0]) {
            case "add":
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "/ngword add <word>");
                    return true;
                }
                plugin.getNgWordsCache().add(player.getUniqueId(), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                player.sendMessage(ChatColor.GREEN + "NGワードを追加しました。");
                break;
            case "remove":
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "/ngword remove <word>");
                    return true;
                }
                plugin.getNgWordsCache().remove(player.getUniqueId(), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                player.sendMessage(ChatColor.GREEN + "NGワードを削除しました。");
                break;
            case "clear":
                plugin.getNgWordsCache().clear(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "NGワードを全て削除しました。");
                break;
            case "list":
                player.sendMessage(ChatColor.GREEN + "NGワード一覧:");
                for (String word : plugin.getNgWordsCache().get(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + word);
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "/ngword (add|remove|list)");
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
            return plugin.getNgWordsCache().get(((Player) sender).getUniqueId())
                    .stream()
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
