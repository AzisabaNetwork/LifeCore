package com.github.mori01231.lifecore.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PetClickFile {
    private static final String FILENAME = "pet-click.yml";

    private static final Set<UUID> disabledPlayers = new HashSet<>();

    public static void disable(UUID uuid) {
        disabledPlayers.add(uuid);
    }

    public static void enable(UUID uuid) {
        disabledPlayers.remove(uuid);
    }

    public static boolean isDisabled(UUID uuid) {
        return disabledPlayers.contains(uuid);
    }

    public static void save(@NotNull Plugin plugin) {
        Map<String, Object> map = new HashMap<>();
        map.put("disabled-players", disabledPlayers.stream().map(UUID::toString).collect(Collectors.toList()));
        ConfigFile.save(plugin, FILENAME, map);
    }

    public static void load(@NotNull Plugin plugin) {
        disabledPlayers.clear();

        disabledPlayers.addAll(Objects.requireNonNull(YamlConfiguration
                        .loadConfiguration(new File(plugin.getDataFolder(), FILENAME))
                        .getList("disabled-players", new ArrayList<String>()))
                .stream()
                .map(String::valueOf)
                .map(UUID::fromString)
                .collect(Collectors.toSet()));

        // save votes every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(plugin), 20 * 60, 20 * 60);
    }
}
