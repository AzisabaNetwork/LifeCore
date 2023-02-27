package com.github.mori01231.lifecore.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DropProtectFile {
    private static final String FILENAME = "drop-protect.yml";

    private static final Map<String, Set<String>> players = new HashMap<>();

    public static @NotNull Set<String> get(@NotNull UUID uuid) {
        return players.computeIfAbsent(uuid.toString(), k -> new HashSet<>());
    }

    public static boolean contains(@NotNull UUID uuid, @NotNull String item) {
        return get(uuid).contains(item);
    }

    public static void toggle(@NotNull UUID uuid, @NotNull String item) {
        if (contains(uuid, item)) {
            get(uuid).remove(item);
        } else {
            get(uuid).add(item);
        }
    }

    public static void save(@NotNull Plugin plugin) {
        Map<String, List<String>> playersMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : players.entrySet()) {
            playersMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("players", playersMap);
        ConfigFile.save(plugin, FILENAME, map);
    }

    public static void load(@NotNull Plugin plugin) {
        players.clear();

        ConfigurationSection section =
                Objects.requireNonNull(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), FILENAME)))
                        .getConfigurationSection("players");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                players.put(key, new HashSet<>(section.getStringList(key)));
            }
        }

        // save every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(plugin), 20 * 60, 20 * 60);
    }
}
