package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GCListenerRestartExtendTimeCommand implements TabExecutor {
    private final LifeCore plugin;
    private final Set<UUID> players = new HashSet<>();

    public GCListenerRestartExtendTimeCommand(@NotNull LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!plugin.getGcListener().isTriggered()) return true;
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        players.add(player.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "投票しました。");
        if (sender.hasPermission("lifecore.extend-time-immediately") || players.size() == 5) {
            ScheduleRestartCommand.schedule(30);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
