package com.github.mori01231.lifecore.util

import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

fun ItemStack.damage(player: Player? = null, amount: Int = 1, checkDurabilityEnchantment: Boolean = false, consumeIfNotDamageable: Boolean = true) {
    if (itemMeta !is Damageable || type.maxDurability.toInt() == 0) {
        if (!consumeIfNotDamageable) error("item is not damageable and consumeIfNotDamageable is false")
        this.amount--
        return
    }
    itemMeta = itemMeta.apply {
        if (this !is Damageable) error("not damageable")

        val level = getEnchantLevel(Enchantment.DURABILITY)
        if (checkDurabilityEnchantment && Math.random() >= 1.0 / (level + 1)) {
            return
        }
        this.damage += amount
        if (type.maxDurability < this.damage) {
            setAmount(0)
            type = Material.AIR
            if (player != null) {
                player.world.playSound(player.location, Sound.ITEM_SHIELD_BREAK, 1F, 1F)
                player.world.spawnParticle(Particle.ITEM_CRACK, player.location, 1, this@damage)
            }
        }
    }
}
