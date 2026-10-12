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
        if (players.contains(player.getUniqueId())) {
            sender.sendMessage(Component.text("すでに投票済みです。", NamedTextColor.RED));
            return true;
        }
        players.add(player.getUniqueId());
        sender.sendMessage(Component.text("投票しました。", NamedTextColor.GREEN));
        if (sender.hasPermission("lifecore.extend-time-immediately") || players.size() == 5) {
            ScheduleRestartCommand.schedule(30 * 60);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
