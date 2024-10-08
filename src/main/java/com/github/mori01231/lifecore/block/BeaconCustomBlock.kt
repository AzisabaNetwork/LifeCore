package com.github.mori01231.lifecore.block

import com.github.mori01231.lifecore.region.WorldLocation
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffectType

class BeaconCustomBlock(
    override val name: String,
    override val lockFacing: Boolean,
    override val axisShift: Int,
    override val backgroundBlock: Material,
    material: Material,
    displayName: String? = null,
    lore: List<String>? = null,
    private val effect: PotionEffectType,
    private val amplifier: Int,
    private val radius: Double = 250.0,
    private val destroyWithoutWrench: Boolean,
) : CustomBlock(material, displayName, lore) {
    var ticks = 0

    override fun tick(manager: CustomBlockManager, pos: WorldLocation, state: CustomBlockState): CustomBlockState? {
        if (ticks++ % 80 == 0) {
            pos.toBukkitLocation().getNearbyEntitiesByType(Player::class.java, radius).forEach { player ->
                player.addPotionEffect(effect.createEffect(20 * 60, amplifier))
            }
        }
        return super.tick(manager, pos, state)
    }

    override fun canDestroy(state: CustomBlockState, wrench: Boolean): Boolean = wrench || destroyWithoutWrench
}
