package com.github.mori01231.lifecore.config;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DropProtectFile {
    private static final String FILENAME = "drop-protect.yml";
    private static final Set<String> dropProtect = new HashSet<>();
    private static BukkitTask saveTask;

    public static void add(@NotNull String name) {
        dropProtect.add(name);
    }

    public static void remove(@NotNull String name) {
        dropProtect.remove(name);
    }

    public static boolean isProtected(@NotNull String name) {
        return dropProtect.contains(name);
    }

    public static void save(@NotNull Plugin plugin) {
        Map<String, Object> map = new HashMap<>();
        map.put("items", new ArrayList<>(dropProtect));
        ConfigFile.save(plugin, FILENAME, map);
    }

    @Contract(pure = true)
    @UnmodifiableView
    public static @NotNull Set<String> getSet() {
        return Collections.unmodifiableSet(dropProtect);
    }

    public static void load(@NotNull Plugin plugin) {
        dropProtect.clear();

        dropProtect.addAll(ConfigFile.loadConfig(plugin, FILENAME).getStringList("items"));

        if (saveTask == null) {
            // save votes every minute
            saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(plugin), 20 * 60, 20 * 60);
        }
    }
}
