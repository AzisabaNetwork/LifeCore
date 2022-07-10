package com.github.mori01231.lifecore.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerUtil {
    @NotNull
    public static UUID resolveUUID(@NotNull String name) throws IllegalArgumentException {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return player.getUniqueId();
        }
        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("name is not UUID or player name");
        }
    }
}
