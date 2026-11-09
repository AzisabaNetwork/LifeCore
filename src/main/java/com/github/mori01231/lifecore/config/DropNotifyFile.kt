package com.github.mori01231.lifecore.config

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

object DropNotifyFile {
    private const val FILENAME = "drop-notify.yml"
    private val dropNotify: MutableSet<UUID> = HashSet()
    private var saveTask: BukkitTask? = null

    fun add(uuid: UUID) {
        dropNotify.add(uuid)
    }

    fun remove(uuid: UUID) {
        dropNotify.remove(uuid)
    }

    fun contains(uuid: UUID): Boolean = dropNotify.contains(uuid)

    fun save(plugin: Plugin) {
        ConfigFile.save(plugin, FILENAME, mapOf("list" to dropNotify.map(UUID::toString)))
    }

    fun load(plugin: Plugin) {
        dropNotify.clear()
        dropNotify.addAll(ConfigFile.loadConfig(plugin, FILENAME).getStringList("list").map(UUID::fromString))

        if (saveTask == null) {
            // save every minute
            saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable { save(plugin) }, 20 * 60, 20 * 60)
        }
    }
}
