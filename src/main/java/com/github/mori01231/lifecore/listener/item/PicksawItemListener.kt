package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.data.DataLoader
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PicksawItemListener(private val dataLoader: DataLoader) : Listener {
    companion object {
        val ALLOWED_WORLDS = listOf("art", "lifetownart")
        const val ITEM_ID = "2dd646f1-70a1-4613-92c3-431de7c0126f"

        private val TIER_TAG_MAP = mapOf(
            "minecraft:mineable/axe" to Material.DIAMOND_AXE,
            "minecraft:mineable/pickaxe" to Material.DIAMOND_PICKAXE,
            "minecraft:mineable/shovel" to Material.DIAMOND_SHOVEL,
            "minecraft:mineable/hoe" to Material.DIAMOND_HOE
        )
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND) return
        val item = e.player.inventory.itemInMainHand
        if (ItemUtil.getStringTag(item, "LifeItemId") != ITEM_ID) return

        if (!ALLOWED_WORLDS.contains(e.player.world.name) && !e.player.hasPermission("lifecore.picksaw")) {
            e.player.health = 0.0
            return
        }
        val offhandItem = e.player.inventory.itemInOffHand
        val hasBlockInOffhand = offhandItem.type.isBlock && offhandItem.type != Material.AIR
        when (e.action) {
            Action.RIGHT_CLICK_BLOCK -> {
                if (hasBlockInOffhand) {
                    if (item.type != Material.DIAMOND_PICKAXE) {
                        item.type = Material.DIAMOND_PICKAXE
                        e.player.inventory.setItemInMainHand(item)
                    }
                }
            }
            Action.LEFT_CLICK_BLOCK -> {
                val block = e.clickedBlock ?: return
                val minecraftName = "minecraft:" + (block.blockData as CraftBlockData).state.block.item.toString()
                val targetMaterial = TIER_TAG_MAP.entries.firstOrNull { (tag, _) ->
                    dataLoader.findTag(tag)?.resolve()?.contains(minecraftName) == true
                }?.value ?: if (block.type.name.endsWith("_WOOL")) Material.SHEARS else null

                if (targetMaterial != null && item.type != targetMaterial) {
                    item.type = targetMaterial
                    e.player.inventory.setItemInMainHand(item)
                }
            }
            else -> return
        }
    }
}