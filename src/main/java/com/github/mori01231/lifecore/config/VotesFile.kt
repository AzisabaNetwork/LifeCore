package com.github.mori01231.lifecore.config

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object VotesFile {
    private val votes: MutableMap<String, Long> = ConcurrentHashMap()

    fun load(plugin: Plugin) {
        // clear everything
        votes.clear()

        // load votes file
        YamlConfiguration.loadConfiguration(File(plugin.dataFolder, "votes.yml"))
            .getValues(true)
            .forEach { (key, value) -> votes[key] = toLong(value) }

        // save votes every minute
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable { save(plugin) }, 20 * 60, 20 * 60)
    }

    fun save(plugin: Plugin) {
        ConfigFile.save(plugin, "votes.yml", votes)
    }

    fun getVotes(key: String): Long = synchronized(votes) { votes.getOrDefault(key, 0L) }

    fun setVotes(key: String, count: Long) {
        synchronized(votes) { votes[key] = count }
    }

    fun increase(key: String) {
        synchronized(votes) { setVotes(key, getVotes(key) + 1) }
    }

    private fun toLong(o: Any): Long =
        (o as? Number)?.toLong() ?: throw IllegalArgumentException("$o is not a number")
}
