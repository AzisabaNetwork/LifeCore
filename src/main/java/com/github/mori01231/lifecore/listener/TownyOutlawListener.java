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
    public void onPlayerMove(PlayerMoveEvent e) {
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
    public void onTeleport(PlayerTeleportEvent e) {
        if (!e.getPlayer().hasPermission("lifecore.bypass-outlaw") && isInOutlawedTown(e.getPlayer(), e.getTo())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "この場所にはテレポートできません。");
        }
    }

    private boolean isInOutlawedTown(@NotNull Player player, @NotNull Location location) {
        Object town = getTownAt(location);
        if (town != null) {
            return hasOutlaw(town, player.getName());
        }
        return false;
    }

    public static @NotNull Object /* = TownyAPI */ getTownyAPI() throws ReflectiveOperationException {
        return CLASS_TOWNY_API.get().getMethod("getInstance").invoke(null);
    }

    public static @Nullable Object /* = TownBlock */ getTownBlock(@NotNull Object townyApi, @NotNull Location location) throws ReflectiveOperationException {
        return CLASS_TOWNY_API.get().getMethod("getTownBlock", Location.class).invoke(townyApi, location);
    }

    public static boolean hasTown(@NotNull Object townBlock) {
        try {
            return (boolean) townBlock.getClass().getMethod("hasTown").invoke(townBlock);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull Object /* = Resident */ getTown(@NotNull Object townBlock) {
        try {
            return townBlock.getClass().getMethod("getTown").invoke(townBlock);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull String getNameTownyObject(@NotNull Object townyObject) {
        try {
            Class<?> clazz = Class.forName("com.palmergames.bukkit.towny.object.TownyObject");
            return (String) clazz.getMethod("getName").invoke(townyObject);
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

    public static Object /* = Resident */ getMayor(@NotNull Object town) {
        try {
            return town.getClass().getMethod("getMayor").invoke(town);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable Object getTownAt(@NotNull Location location) {
        try {
            Object townyApi = getTownyAPI();
            Object townBlock = getTownBlock(townyApi, location);
            if (townBlock != null && hasTown(townBlock)) {
                return getTown(townBlock);
            }
            return null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
