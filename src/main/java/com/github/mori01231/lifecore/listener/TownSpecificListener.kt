package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.config.TownConfig
import com.github.mori01231.lifecore.util.runTaskTimer
import de.Keyle.MyPet.MyPetApi
import de.Keyle.MyPet.api.skill.skills.Backpack
import de.Keyle.MyPet.api.skill.skills.Pickup
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.potion.PotionEffectType
import java.lang.reflect.Field

class TownSpecificListener(private val plugin: LifeCore) : Listener {
    private val pickupPickupField: Field =
        Class.forName("de.Keyle.MyPet.skill.skills.PickupImpl")
            .getDeclaredField("pickup")
            .apply { isAccessible = true }

    fun startTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, 20, 20) {
            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.hasPermission("lifecore.bypass-town-restrictions")) {
                    return@forEach
                }
                val config = TownConfig.getTownConfigAt(plugin.lifeCoreConfig, player.location) ?: return@forEach
                if (!config.allowInvisibility && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY)
                    player.sendMessage("${ChatColor.RED}この町での透明化は許可されていません。")
                }
                if (Bukkit.getPluginManager().isPluginEnabled("MyPet")) {
                    runMyPet(config, player)
                }
            }
        }
    }

    private fun runMyPet(config: TownConfig, player: Player) {
        if (!config.allowPetPickup) {
            MyPetApi.getMyPetManager().getMyPet(player)?.let { pet ->
                val skill = pet.skills.get(Pickup::class.java)
                if (skill.isActive && pet.skills.isActive(Backpack::class.java) && (pickupPickupField[skill] as Boolean)) {
                    player.sendMessage("${ChatColor.RED}この町でのpetpickupは許可されていません。")
                    skill.activate() // this toggles the "pickup" status and sends a message to the player
                }
            }
        }
    }

    @EventHandler
    fun onPotionSplash(e: PotionSplashEvent) {
        val config = TownConfig.getTownConfigAt(plugin.lifeCoreConfig, e.potion.location) ?: return
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
            val config = TownConfig.getTownConfigAt(plugin.lifeCoreConfig, e.player.location) ?: return
            if (!config.allowSit) {
                e.isCancelled = true
                e.player.sendMessage("${ChatColor.RED}この町での/sitは許可されていません。")
            }
        }
    }
}
