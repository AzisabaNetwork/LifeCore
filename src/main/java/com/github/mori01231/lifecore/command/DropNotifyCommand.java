package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.config.DropNotifyFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DropNotifyCommand extends PlayerTabExecutor {
    @Override
    public boolean execute(@NotNull Player player, @NotNull String[] args) {
        if (DropNotifyFile.contains(player.getUniqueId())) {
            DropNotifyFile.remove(player.getUniqueId());
            player.sendMessage(Component.text("ドロップ通知をオフにしました。", NamedTextColor.GREEN));
        } else {
            DropNotifyFile.add(player.getUniqueId());
            player.sendMessage(Component.text("ドロップ通知をオンにしました。", NamedTextColor.GREEN));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
