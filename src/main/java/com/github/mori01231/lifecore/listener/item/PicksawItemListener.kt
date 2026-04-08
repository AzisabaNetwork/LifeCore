package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.data.DataLoader
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PicksawItemListener(private val dataLoader: DataLoader) : Listener {
    companion object {
        const val ITEM_ID = "2dd646f1-70a1-4613-92c3-431de7c0126f"
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.player.world.name != "art" && !e.player.hasPermission("lifecore.picksaw")) {
            e.player.health = 0.0
            return
        }
        if (e.hand != EquipmentSlot.HAND) return
        if (e.action != Action.LEFT_CLICK_BLOCK) return
        val item = e.player.inventory.itemInMainHand
        if (ItemUtil.getStringTag(item, "LifeItemId") != ITEM_ID) return
        val minecraftName = "minecraft:" + ((e.clickedBlock ?: return).blockData as CraftBlockData).state.block.item.toString()
        if (dataLoader.findTag("minecraft:mineable/axe")?.resolve()?.contains(minecraftName) == true) {
            item.type = Material.DIAMOND_AXE
            e.player.inventory.setItemInMainHand(item)
        } else if (dataLoader.findTag("minecraft:mineable/pickaxe")?.resolve()?.contains(minecraftName) == true) {
            item.type = Material.DIAMOND_PICKAXE
            e.player.inventory.setItemInMainHand(item)
        } else if (dataLoader.findTag("minecraft:mineable/shovel")?.resolve()?.contains(minecraftName) == true) {
            item.type = Material.DIAMOND_SHOVEL
            e.player.inventory.setItemInMainHand(item)
        } else if (dataLoader.findTag("minecraft:mineable/hoe")?.resolve()?.contains(minecraftName) == true) {
            item.type = Material.DIAMOND_HOE
            e.player.inventory.setItemInMainHand(item)
        } else if (e.clickedBlock?.type?.name?.endsWith("_WOOL") == true) {
            item.type = Material.SHEARS
        }
    }
}