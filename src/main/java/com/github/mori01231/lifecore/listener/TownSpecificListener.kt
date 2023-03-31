package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.config.TownConfig
import com.github.mori01231.lifecore.util.runTaskTimer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.potion.PotionEffectType

class TownSpecificListener(private val plugin: LifeCore) : Listener {
    private fun getTownConfigAt(location: Location): TownConfig? {
        val town = TownyOutlawListener.getTownAt(location) ?: return null
        val townName = TownyOutlawListener.getNameTownyObject(town)
        return plugin.lifeCoreConfig.townConfig.computeIfAbsent(townName) { TownConfig() }
    }

    fun startTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, 20, 20) {
            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.hasPermission("lifecore.bypass-town-restrictions")) {
                    return@forEach
                }
                val config = getTownConfigAt(player.location) ?: return@forEach
                if (!config.allowInvisibility && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY)
                    player.sendMessage("${ChatColor.RED}この町での透明化は許可されていません。")
                }
            }
        }
    }

    @EventHandler
    fun onPotionSplash(e: PotionSplashEvent) {
        val config = getTownConfigAt(e.potion.location) ?: return
        if (!config.allowInvisibility) {
            val toRemove = mutableListOf<LivingEntity>()
            e.affectedEntities.forEach { entity ->
                if (!entity.hasPermission("lifecore.bypass-town-restrictions")) {
                    toRemove.add(entity)
                }
            }
            toRemove.forEach { e.setIntensity(it, 0.0) }
        }
    }

    @EventHandler
    fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
        if (e.player.hasPermission("lifecore.bypass-town-restrictions")) {
            return
        }
        val command = if (e.message.contains(' ')) e.message.substring(0, e.message.indexOf(' ')) else e.message
        val commandName = if (command.contains(':')) command.substring(command.indexOf(':') + 1) else command.substring(1)
        if (commandName == "sit" || commandName == "gsit") {
            val config = getTownConfigAt(e.player.location) ?: return
            if (!config.allowSit) {
                e.isCancelled = true
                e.player.sendMessage("${ChatColor.RED}この町での/sitは許可されていません。")
            }
        }
    }
}
