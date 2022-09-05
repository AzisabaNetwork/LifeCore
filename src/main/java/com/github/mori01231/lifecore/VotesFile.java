package com.github.mori01231.lifecore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class VotesFile {
    private static YamlConfiguration votes;

    public static void load(@NotNull Plugin plugin) {
        // load votes file
        votes = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "votes.yml"));

        // save votes every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(plugin), 20 * 60, 20 * 60);
    }

    public static void save(@NotNull Plugin plugin) {
        try {
            votes.save(new File(plugin.getDataFolder(), "votes.yml"));
        } catch (IOException e) {
            plugin.getSLF4JLogger().warn("Failed to save votes.yml", e);
        }
    }

    public static long getVotes(@NotNull String key) {
        return votes.getLong(key);
    }

    public static void setVotes(@NotNull String key, long votes) {
        VotesFile.votes.set(key, votes);
    }

    public static void increase(@NotNull String key) {
        setVotes(key, getVotes(key) + 1);
    }
}
