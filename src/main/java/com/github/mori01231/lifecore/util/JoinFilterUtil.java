package com.github.mori01231.lifecore.util;

import com.github.mori01231.lifecore.util.external.JoinFilterBridge;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class JoinFilterUtil {
    public static boolean addPlayerWithExpire(@NotNull UUID uuid, @NotNull String server, long expireSeconds) {
        if (Bukkit.getPluginManager().isPluginEnabled("JoinFilter")) {
            JoinFilterBridge.addPlayerWithExpire(uuid, server, expireSeconds);
            return true;
        }
        return false;
    }
}
