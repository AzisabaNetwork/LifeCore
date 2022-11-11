package com.github.mori01231.lifecore.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigFile {
    public static void save(@NotNull Plugin plugin, @NotNull String filename, @NotNull Object object) {
        try {
            String content = new Yaml().dumpAsMap(object);
            try (FileWriter writer = new FileWriter(new File(plugin.getDataFolder(), filename))) {
                writer.write(content);
            }
        } catch (IOException e) {
            plugin.getSLF4JLogger().warn("Failed to save {}", filename, e);
        }
    }
}
