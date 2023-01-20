package com.github.mori01231.lifecore.config;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DropNotifyFile {
    private static final String FILENAME = "drop-notify.yml";
    private static final Set<UUID> dropNotify = new HashSet<>();
    private static BukkitTask saveTask;

    public static void add(@NotNull UUID uuid) {
        dropNotify.add(uuid);
    }

    public static void remove(@NotNull UUID uuid) {
        dropNotify.remove(uuid);
    }

    public static boolean contains(@NotNull UUID uuid) {
        return dropNotify.contains(uuid);
    }

    public static void save(@NotNull Plugin plugin) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", dropNotify.stream().map(UUID::toString).collect(Collectors.toList()));
        ConfigFile.save(plugin, FILENAME, map);
    }

    public static void load(@NotNull Plugin plugin) {
        dropNotify.clear();

        dropNotify.addAll(ConfigFile.loadConfig(plugin, FILENAME).getStringList("list").stream().map(UUID::fromString).collect(Collectors.toSet()));

        if (saveTask == null) {
            // save votes every minute
            saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(plugin), 20 * 60, 20 * 60);
        }
    }
}
