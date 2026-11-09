package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.config.DamageLogFile
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.plugin.java.JavaPlugin
import java.util.ArrayDeque
import java.util.UUID
import java.util.WeakHashMap

class DamageLogListener : Listener {
    private val pendingMythicDamage = mutableMapOf<UUID, ArrayDeque<MythicDamage>>()
    private val mythicArmorPiercing = WeakHashMap<EntityDamageEvent, Boolean>()

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onMythicDamage(e: MythicDamageEvent) {
        val targetId = e.target.uniqueId
        val damage = MythicDamage(e.damageMetadata.ignoresArmor == true)
        pendingMythicDamage.getOrPut(targetId) { ArrayDeque() }.addLast(damage)

        // MythicDamageEvent normally produces the Bukkit damage event synchronously. Remove the
        // marker on the next tick if another plugin cancels it before that happens.
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(LifeCore::class.java), Runnable {
            val pending = pendingMythicDamage[targetId] ?: return@Runnable
            pending.remove(damage)
            if (pending.isEmpty()) pendingMythicDamage.remove(targetId)
        })
    }

    private fun isMythicArmorPiercing(e: EntityDamageEvent): Boolean {
        mythicArmorPiercing[e]?.let { return it }
        val pending = pendingMythicDamage[e.entity.uniqueId]
        val damage = pending?.pollFirst()
        if (pending != null && pending.isEmpty()) pendingMythicDamage.remove(e.entity.uniqueId)
        val armorPiercing = damage?.ignoresArmor == true
        mythicArmorPiercing[e] = armorPiercing
        return armorPiercing
    }

    // a class (not data class) so that two damages with the same value are still different instances in the deque
    private class MythicDamage(val ignoresArmor: Boolean)

    fun message(p: Player, type: String, damage: Double, send: Boolean, entity: Entity? = null) {
        val prefix = "§8[§cDamageLog§8] "
        val entityName = if (entity == null) "" else "§f" + entity.name + " "
        val damageType = if (send) "§a与" else "§c受"
        val damageColor = if (send) "§a" else "§c"
        p.sendMessage("$prefix$entityName$damageColor$damage §8($damageType §7$type§8)")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDamaged(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        if (e.isCancelled) return
        if (!DamageLogFile.isEnabled(player.uniqueId)) return
        if (isMythicArmorPiercing(e)) {
            message(player, "貫通", e.finalDamage, false)
            return
        }
        RECEIVED_TYPES[e.cause]?.let { message(player, it, e.finalDamage, false) }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val victim = e.entity
        if (victim is Player) {
            if (!DamageLogFile.isEnabled(victim.uniqueId)) return
            if (isMythicArmorPiercing(e)) {
                message(victim, "貫通", e.finalDamage, false, e.damager)
            } else {
                RECEIVED_BY_ENTITY_TYPES[e.cause]?.let { (type, showEntity) ->
                    message(victim, type, e.finalDamage, false, if (showEntity) e.damager else null)
                }
            }
        }
        val attacker = e.damager
        if (attacker is Player) {
            if (!DamageLogFile.isEnabled(attacker.uniqueId)) return
            if (isMythicArmorPiercing(e)) {
                message(attacker, "貫通", e.finalDamage, true, e.entity)
                return
            }
            DEALT_TYPES[e.cause]?.let { (type, showEntity) ->
                message(attacker, type, e.finalDamage, true, if (showEntity) e.entity else null)
            }
        }
    }

    companion object {
        /** Damage received from anything (EntityDamageEvent) */
        private val RECEIVED_TYPES = mapOf(
            DamageCause.FALL to "落下",
            DamageCause.FIRE to "炎上",
            DamageCause.LAVA to "溶岩",
            DamageCause.POISON to "毒",
            DamageCause.VOID to "奈落",
            DamageCause.WITHER to "衰弱",
            DamageCause.FLY_INTO_WALL to "激突",
            DamageCause.SUICIDE to "自害",
            DamageCause.THORNS to "茨の鎧",
            DamageCause.STARVATION to "餓死",
            DamageCause.FIRE_TICK to "延焼",
            DamageCause.HOT_FLOOR to "マグマブロック",
            DamageCause.DROWNING to "窒息",
            DamageCause.ENTITY_EXPLOSION to "爆発",
            DamageCause.SUFFOCATION to "窒息",
            DamageCause.CONTACT to "棘",
            DamageCause.BLOCK_EXPLOSION to "爆発",
            DamageCause.CUSTOM to "貫通",
        )

        /** Damage received from an entity: type to whether to show the damager name */
        private val RECEIVED_BY_ENTITY_TYPES = mapOf(
            DamageCause.FALL to ("落下" to false),
            DamageCause.FIRE to ("炎上" to false),
            DamageCause.LAVA to ("溶岩" to false),
            DamageCause.POISON to ("毒" to false),
            DamageCause.VOID to ("奈落" to false),
            DamageCause.WITHER to ("衰弱" to false),
            DamageCause.LIGHTNING to ("雷" to false),
            DamageCause.FALLING_BLOCK to ("落下中のブロック" to false),
            DamageCause.FLY_INTO_WALL to ("激突" to false),
            DamageCause.MAGIC to ("魔法" to true),
            DamageCause.SUICIDE to ("自害" to false),
            DamageCause.THORNS to ("茨の鎧" to true),
            DamageCause.STARVATION to ("餓死" to false),
            DamageCause.FIRE_TICK to ("延焼" to false),
            DamageCause.HOT_FLOOR to ("マグマブロック" to false),
            DamageCause.DROWNING to ("窒息" to false),
            DamageCause.ENTITY_EXPLOSION to ("爆発" to false),
            DamageCause.SUFFOCATION to ("窒息" to false),
            DamageCause.CONTACT to ("棘" to false),
            DamageCause.BLOCK_EXPLOSION to ("爆発" to false),
            DamageCause.CUSTOM to ("貫通" to true),
            DamageCause.ENTITY_ATTACK to ("攻撃" to true),
            DamageCause.ENTITY_SWEEP_ATTACK to ("範囲攻撃" to true),
        )

        /** Damage dealt to an entity: type to whether to show the victim name */
        private val DEALT_TYPES = mapOf(
            DamageCause.POISON to ("毒" to false),
            DamageCause.WITHER to ("衰弱" to false),
            DamageCause.LIGHTNING to ("雷" to false),
            DamageCause.MAGIC to ("魔法" to false),
            DamageCause.SUICIDE to ("自害" to false),
            DamageCause.THORNS to ("茨の鎧" to false),
            DamageCause.FIRE_TICK to ("延焼" to false),
            DamageCause.ENTITY_EXPLOSION to ("爆発" to false),
            DamageCause.CONTACT to ("棘" to false),
            DamageCause.BLOCK_EXPLOSION to ("爆発" to false),
            DamageCause.CUSTOM to ("貫通" to true),
            DamageCause.ENTITY_ATTACK to ("攻撃" to true),
            DamageCause.ENTITY_SWEEP_ATTACK to ("範囲攻撃" to true),
        )
    }
}
