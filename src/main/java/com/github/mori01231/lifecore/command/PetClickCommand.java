package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.config.PetClickFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            player.sendMessage(Component.text("ペットに対してShift+右クリックができるようになりました。", NamedTextColor.GREEN));
        } else {
            PetClickFile.disable(player.getUniqueId());
            player.sendMessage(Component.text("ペットに対してShift+右クリックができなくなりました。", NamedTextColor.GREEN));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
