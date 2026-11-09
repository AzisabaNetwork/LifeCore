package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class FlashLightListener : Listener {
    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK && e.action != Action.RIGHT_CLICK_AIR) return
        if (ItemUtil.getMythicType(e.player.inventory.itemInOffHand) == "flashlight") {
            e.player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 18000, 0))
        }
    }
}
