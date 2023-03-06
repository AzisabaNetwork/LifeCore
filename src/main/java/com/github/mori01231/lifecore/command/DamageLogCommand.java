package com.github.mori01231.lifecore.command;

import com.github.mori01231.lifecore.config.DamageLogFile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DamageLogCommand extends PlayerTabExecutor {

    @Override
    public boolean execute(@NotNull Player player, @NotNull String[] args) {
        if (DamageLogFile.isEnabled(player.getUniqueId())) {
            DamageLogFile.disable(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "DamageLogが無効化されました");
        } else {
            DamageLogFile.enable(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "DamageLogが有効化されました");
        }
        return true;
    }
}

