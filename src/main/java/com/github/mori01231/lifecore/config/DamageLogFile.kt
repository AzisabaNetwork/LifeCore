package com.github.mori01231.lifecore.config

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.UUID

object DamageLogFile {
    private const val FILENAME = "damage-log.yml"
    private val players: MutableSet<UUID> = HashSet()

    fun enable(uuid: UUID) {
        players.add(uuid)
    }

    fun disable(uuid: UUID) {
        players.remove(uuid)
    }

    fun isEnabled(uuid: UUID): Boolean = players.contains(uuid)

    fun save(plugin: Plugin) {
        ConfigFile.save(plugin, FILENAME, mapOf("players" to players.map(UUID::toString)))
    }

    fun load(plugin: Plugin) {
        players.clear()
        players.addAll(
            YamlConfiguration.loadConfiguration(File(plugin.dataFolder, FILENAME))
                .getList("players", ArrayList<String>())!!
                .map { UUID.fromString(it.toString()) }
        )

        // save every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable { save(plugin) }, 20 * 60, 20 * 60)
    }
}
