package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.config.DamageLogFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DamageLogCommand extends PlayerTabExecutor {

    @Override
    public boolean execute(@NotNull Player player, @NotNull String[] args) {
        if (DamageLogFile.isEnabled(player.getUniqueId())) {
            DamageLogFile.disable(player.getUniqueId());
            player.sendMessage(Component.text("DamageLogが無効化されました", NamedTextColor.RED));
        } else {
            DamageLogFile.enable(player.getUniqueId());
            player.sendMessage(Component.text("DamageLogが有効化されました", NamedTextColor.GREEN));
        }
        return true;
    }
}

