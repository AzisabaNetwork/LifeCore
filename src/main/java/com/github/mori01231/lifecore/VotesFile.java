package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VotesFile {
    private static final Map<String, Long> votes = new ConcurrentHashMap<>();

    public static void load(@NotNull Plugin plugin) {
        // clear everything
        votes.clear();

        // load votes file
        votes.putAll(
                YamlConfiguration
                        .loadConfiguration(new File(plugin.getDataFolder(), "votes.yml"))
                        .getValues(true)
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> toLong(e.getValue())))
        );

        // save votes every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(plugin), 20 * 60, 20 * 60);
    }

    public static void save(@NotNull Plugin plugin) {
        try {
            String content = new Yaml().dumpAsMap(votes);
            try (FileWriter writer = new FileWriter(new File(plugin.getDataFolder(), "votes.yml"))) {
                writer.write(content);
            }
        } catch (IOException e) {
            plugin.getSLF4JLogger().warn("Failed to save votes.yml", e);
        }
    }

    public static long getVotes(@NotNull String key) {
        synchronized (votes) {
            return votes.getOrDefault(key, 0L);
        }
    }

    public static void setVotes(@NotNull String key, long count) {
        synchronized (votes) {
            votes.put(key, count);
        }
    }

    public static void increase(@NotNull String key) {
        synchronized (votes) {
            setVotes(key, getVotes(key) + 1);
        }
    }

    private static long toLong(@NotNull Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        } else if (o instanceof Long) {
            return (Long) o;
        } else if (o instanceof Number) {
            return ((Number) o).longValue();
        } else {
            throw new IllegalArgumentException(o + " is not a number");
        }
    }
}
