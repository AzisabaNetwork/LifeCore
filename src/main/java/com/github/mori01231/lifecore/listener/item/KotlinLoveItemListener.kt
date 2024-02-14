package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class KotlinLoveItemListener(plugin: LifeCore) : Listener {
    companion object {
        private const val MYTHIC_TYPE = "KotlinLove"
        private const val HEALTH_THRESHOLD_PERCENT = 0.5
        private const val DAMAGE_ADDITION = 0.05
        private const val DAMAGE_REDUCTION = 0.2
    }

    init {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            Bukkit.getOnlinePlayers().forEach { player ->
                if (ItemUtil.isEquippedInAnySlot(player, MYTHIC_TYPE) && isHealthBelowThreshold(player) && player.isSneaking) {
                    // health 1% of max health per second
                    player.health += getMaxHealth(player) * 0.01
                }
            }
        }, 20, 20)
    }

    private fun getMaxHealth(player: Player) = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0

    private fun isHealthBelowThreshold(player: Player): Boolean {
        return player.health <= getMaxHealth(player) * HEALTH_THRESHOLD_PERCENT
    }

    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        val damager = e.damager
        if (damager is Player) {
            // +5% damage if equipped and health is below threshold
            if ((e.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || e.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
                ItemUtil.isEquippedInAnySlot(damager, MYTHIC_TYPE) && isHealthBelowThreshold(damager)) {
                e.damage += e.damage * DAMAGE_ADDITION
            }
        }
        val entity = e.entity
        if (entity is Player) {
            // -20% damage if equipped and health is below threshold
            if (ItemUtil.isEquippedInAnySlot(entity, MYTHIC_TYPE) && isHealthBelowThreshold(entity)) {
                e.damage -= e.damage * DAMAGE_REDUCTION
            }
        }
    }
}
