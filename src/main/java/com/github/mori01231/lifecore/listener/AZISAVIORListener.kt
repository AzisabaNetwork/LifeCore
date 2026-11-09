package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID
import kotlin.math.min

class AZISAVIORListener(private val plugin: LifeCore) : Listener {
    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        val player = e.player
        val mainHand = player.inventory.itemInMainHand
        val offHand = player.inventory.itemInOffHand
        if (ItemUtil.getMythicType(offHand) !in OFFHAND_ITEMS) return
        if ("offhandactivate" != ItemUtil.getMythicType(mainHand) && !mainHand.type.isAir) return
        if (!coolTime.add(player.uniqueId)) return
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { coolTime.remove(player.uniqueId) }, 20 * 15)
        val maxHealth = requireNotNull(player.getAttribute(Attribute.MAX_HEALTH)).value
        player.health = min(player.health + 50, maxHealth)
        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10000f, 2f)
    }

    @EventHandler
    fun onDamaged(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        if (ItemUtil.getMythicType(player.inventory.itemInOffHand) !in OFFHAND_ITEMS) return
        player.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 200, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, 200, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 1))
    }

    companion object {
        private val OFFHAND_ITEMS = setOf("AZISAVIOR", "ZARISAVIOR")
        private val coolTime = mutableSetOf<UUID>()
    }
}
