package com.github.mori01231.lifecore.config

import com.github.mori01231.lifecore.util.YAML.yaml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

object ConfigFile {
    inline fun <reified T : Any> saveToFile(plugin: Plugin, filename: String, obj: T) {
        try {
            val content = yaml.encodeToString(obj)
            File(plugin.dataFolder, filename).writeText(content)
        } catch (e: Exception) {
            plugin.slF4JLogger.warn("Failed to save {}", filename, e)
        }
    }

    inline fun <reified T : Any> loadFromFile(plugin: Plugin, filename: String): T {
        val content = File(plugin.dataFolder, filename).readText()
        return yaml.decodeFromString(content)
    }

    @JvmStatic
    fun save(plugin: Plugin, filename: String, obj: Any) {
        try {
            val content = org.yaml.snakeyaml.Yaml().dumpAsMap(obj)
            File(plugin.dataFolder, filename).writeText(content)
        } catch (e: IOException) {
            plugin.slF4JLogger.warn("Failed to save {}", filename, e)
        }
    }

    @JvmStatic
    fun loadConfig(plugin: Plugin, filename: String): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(File(plugin.dataFolder, filename))
    }
}
