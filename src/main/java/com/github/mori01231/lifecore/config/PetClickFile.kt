package com.github.mori01231.lifecore.config

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.UUID

object PetClickFile {
    private const val FILENAME = "pet-click.yml"
    private val disabledPlayers: MutableSet<UUID> = HashSet()

    fun disable(uuid: UUID) {
        disabledPlayers.add(uuid)
    }

    fun enable(uuid: UUID) {
        disabledPlayers.remove(uuid)
    }

    fun isDisabled(uuid: UUID): Boolean = disabledPlayers.contains(uuid)

    fun save(plugin: Plugin) {
        ConfigFile.save(plugin, FILENAME, mapOf("disabled-players" to disabledPlayers.map(UUID::toString)))
    }

    fun load(plugin: Plugin) {
        disabledPlayers.clear()
        disabledPlayers.addAll(
            YamlConfiguration.loadConfiguration(File(plugin.dataFolder, FILENAME))
                .getList("disabled-players", ArrayList<String>())!!
                .map { UUID.fromString(it.toString()) }
        )

        // save every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable { save(plugin) }, 20 * 60, 20 * 60)
    }
}
