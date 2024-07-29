package com.github.mori01231.lifecore.util.external;

import net.azisaba.joinfilter.JoinFilter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class JoinFilterBridge {
    public static void addPlayerWithExpire(@NotNull UUID uuid, @NotNull String server, long expireSeconds) {
        JoinFilter.getPlugin(JoinFilter.class).addPlayerWithExpire(uuid, server, expireSeconds);
    }
}
