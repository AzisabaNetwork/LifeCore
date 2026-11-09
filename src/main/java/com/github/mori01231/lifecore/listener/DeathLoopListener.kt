package com.github.mori01231.lifecore.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin

class DeathLoopListener : Listener {
    @EventHandler
    fun onAttack(e: EntityDamageByEntityEvent) {
        val entity = e.damager as? LivingEntity ?: return
        if (hasTempHealthBoost(entity)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (hasTempHealthBoost(e.player)) {
            e.isCancelled = true
        }
    }

    private fun hasTempHealthBoost(entity: LivingEntity): Boolean {
        val attr = entity.getAttribute(Attribute.MAX_HEALTH) ?: return false
        return attr.modifiers.any { it.key in TEMP_KEYS }
    }

    companion object {
        private val KEY_SCALAR = NamespacedKey("lifecore", "temp_health_boost_scalar")
        private val KEY_NUMBER = NamespacedKey("lifecore", "temp_health_boost_number")
        private val TEMP_KEYS = setOf(KEY_SCALAR, KEY_NUMBER)

        fun checkAttribute(plugin: Plugin, entity: LivingEntity) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable { checkAttribute0(entity) }, 5)
        }

        private fun checkAttribute0(entity: LivingEntity) {
            val attr = entity.getAttribute(Attribute.MAX_HEALTH) ?: return
            attr.modifiers.filter { it.key in TEMP_KEYS }.forEach { attr.removeModifier(it) }
            if (attr.value > 0) return
            var amountScalar = 0.0
            var amountNumber = 0.0
            for (modifier in attr.modifiers) {
                if (modifier.amount >= 0) continue
                when (modifier.operation) {
                    AttributeModifier.Operation.ADD_SCALAR -> amountScalar -= modifier.amount
                    AttributeModifier.Operation.ADD_NUMBER -> amountNumber -= modifier.amount
                    else -> {}
                }
            }
            if (amountScalar != 0.0) {
                attr.addModifier(AttributeModifier(KEY_SCALAR, amountScalar, AttributeModifier.Operation.ADD_SCALAR))
            }
            if (amountNumber != 0.0) {
                attr.addModifier(AttributeModifier(KEY_NUMBER, amountNumber, AttributeModifier.Operation.ADD_NUMBER))
            }
            entity.sendMessage(Component.text("不可能な装備の組み合わせです。最大体力が0以下になっています。", NamedTextColor.RED))
        }
    }
}
