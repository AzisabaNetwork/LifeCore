package com.github.mori01231.lifecore.resource

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

/**
 * 旧 LifeResource プラグインのリスナー。
 * 資源ボックス系 MythicMobs アイテムの消費防止と、右クリックでの開封を行う。
 */
class LifeResourceItemListener : Listener {

    @EventHandler
    fun onDrink(event: PlayerItemConsumeEvent) {
        val mmid = getMythicType(event.item) ?: return
        if (BLOCKED_CONSUME_MMIDS.any { mmid.equals(it, ignoreCase = true) }) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK && event.action != Action.RIGHT_CLICK_AIR) return
        val item = event.item ?: return
        if (!item.hasItemMeta()) return
        val mmid = getMythicType(item) ?: return
        val player = event.player
        when {
            mmid.equals("unknownopenbox", ignoreCase = true) -> {
                item.subtract(1)
                UnknownOpenBox.get(player)
            }
            mmid.equals("Xrandombox", ignoreCase = true) -> {
                item.subtract(1)
                XRandomBox.get(player)
            }
            mmid.equals("Yrandombox", ignoreCase = true) -> {
                item.subtract(1)
                YRandomBox.get(player)
            }
            mmid.equals("Yunknownopenbox", ignoreCase = true) -> {
                item.subtract(1)
                YUnknownOpenBox.get(player)
            }
            mmid.equals("Yrandombox_16set", ignoreCase = true) -> {
                item.subtract(1)
                repeat(16) { YRandomBox.get16(player) }
            }
            mmid.equals("Xrandombox_16set", ignoreCase = true) -> {
                item.subtract(1)
                repeat(16) { XRandomBox.get16(player) }
            }
            mmid.equals("unknownopenbox_16set", ignoreCase = true) -> {
                item.subtract(1)
                repeat(16) { UnknownOpenBox.get16(player) }
            }
            mmid.equals("Yunknownopenbox_16set", ignoreCase = true) -> {
                item.subtract(1)
                repeat(16) { YUnknownOpenBox.get(player) }
            }
            mmid.equals("afterweaponbox", ignoreCase = true) -> {
                item.subtract(1)
                AfterWeaponBox.get(player)
            }
            mmid.equals("afterweaponboxaxe", ignoreCase = true) -> {
                item.subtract(1)
                AfterWeaponBoxAxe.get(player)
            }
        }
    }

    companion object {
        private val BLOCKED_CONSUME_MMIDS = listOf(
            "unknownopenbox",
            "Xrandombox",
            "Yrandombox",
            "Yunknownopenbox",
            "Yrandombox_16set",
            "Xrandombox_16set",
            "unknownopenbox_16set",
            "Yunknownopenbox_16set",
            "afterweaponbox",
            "afterweaponboxaxe",
        )

        private fun getMythicType(item: org.bukkit.inventory.ItemStack?): String? {
            if (item == null || item.type.isAir) return null
            return runCatching { ItemUtil.getMythicType(item) }.getOrNull()
        }
    }
}
