package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.config.PetClickFile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PetClickCommand extends PlayerTabExecutor {
    @Override
    public boolean execute(@NotNull Player player, @NotNull String[] args) {
        if (PetClickFile.isDisabled(player.getUniqueId())) {
            PetClickFile.enable(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "ペットに対してShift+右クリックができるようになりました。");
        } else {
            PetClickFile.disable(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "ペットに対してShift+右クリックができなくなりました。");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
