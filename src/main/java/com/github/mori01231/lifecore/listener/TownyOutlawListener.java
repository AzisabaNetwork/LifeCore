package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.util.LazyInitValue;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TownyOutlawListener implements Listener {
    private static final LazyInitValue<Class<?>> CLASS_TOWNY_API = new LazyInitValue<>(() -> {
        try {
            return Class.forName("com.palmergames.bukkit.towny.TownyAPI");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    });
    private final Map<UUID, Long> lastMove = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) throws ReflectiveOperationException {
        if (lastMove.containsKey(e.getPlayer().getUniqueId())) {
            if (System.currentTimeMillis() - lastMove.get(e.getPlayer().getUniqueId()) < 1000) {
                return;
            }
        }
        lastMove.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        if (!e.getPlayer().hasPermission("lifecore.bypass-outlaw") && isInOutlawedTown(e.getPlayer(), e.getPlayer().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + "出入り禁止になっている街に進入することはできません！");
            e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) throws ReflectiveOperationException {
        if (!e.getPlayer().hasPermission("lifecore.bypass-outlaw") && isInOutlawedTown(e.getPlayer(), e.getTo())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "この場所にはテレポートできません。");
        }
    }

    private boolean isInOutlawedTown(@NotNull Player player, @NotNull Location location) throws ReflectiveOperationException {
        Object townyApi = getTownyAPI();
        Object townBlock = getTownBlock(townyApi, location);
        if (townBlock != null && hasTown(townBlock)) {
            Object town = getTown(townBlock);
            return hasOutlaw(town, player.getName());
        }
        return false;
    }

    private static @NotNull Object getTownyAPI() throws ReflectiveOperationException {
        return CLASS_TOWNY_API.get().getMethod("getInstance").invoke(null);
    }

    private static @Nullable Object getTownBlock(@NotNull Object townyApi, @NotNull Location location) throws ReflectiveOperationException {
        return CLASS_TOWNY_API.get().getMethod("getTownBlock", Location.class).invoke(townyApi, location);
    }

    private static boolean hasTown(@NotNull Object townBlock) {
        try {
            return (boolean) townBlock.getClass().getMethod("hasTown").invoke(townBlock);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull Object getTown(@NotNull Object townBlock) {
        try {
            return townBlock.getClass().getMethod("getTown").invoke(townBlock);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean hasOutlaw(@NotNull Object town, @NotNull String name) {
        try {
            return (boolean) town.getClass().getMethod("hasOutlaw", String.class).invoke(town, name);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
