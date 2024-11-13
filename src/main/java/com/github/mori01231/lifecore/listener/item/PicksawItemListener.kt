package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.data.DataLoader
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Material
import org.bukkit.craftbukkit.block.data.CraftBlockData
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
        if (e.hand != EquipmentSlot.HAND) return
        if (e.action != Action.LEFT_CLICK_BLOCK) return
        val item = e.player.inventory.itemInMainHand
        if (ItemUtil.getStringTag(item, "LifeItemId") != ITEM_ID) return
        val minecraftName = ((e.clickedBlock ?: return).blockData as CraftBlockData).state.block.asItem().toString()
        if (dataLoader.findTag("minecraft:mineable/axe")?.resolve()?.contains(minecraftName) == true) {
            val type = Material.valueOf(item.type.name.substring(0, item.type.name.lastIndexOf('_')) + "_AXE")
            e.player.inventory.setItemInMainHand(ItemUtil.cloneWithNewMaterial(item, type))
        } else if (dataLoader.findTag("minecraft:mineable/pickaxe")?.resolve()?.contains(minecraftName) == true) {
            val type = Material.valueOf(item.type.name.substring(0, item.type.name.lastIndexOf('_')) + "_PICKAXE")
            e.player.inventory.setItemInMainHand(ItemUtil.cloneWithNewMaterial(item, type))
        } else if (dataLoader.findTag("minecraft:mineable/shovel")?.resolve()?.contains(minecraftName) == true) {
            val type = Material.valueOf(item.type.name.substring(0, item.type.name.lastIndexOf('_')) + "_SHOVEL")
            e.player.inventory.setItemInMainHand(ItemUtil.cloneWithNewMaterial(item, type))
        } else if (dataLoader.findTag("minecraft:mineable/hoe")?.resolve()?.contains(minecraftName) == true) {
            val type = Material.valueOf(item.type.name.substring(0, item.type.name.lastIndexOf('_')) + "_HOE")
            e.player.inventory.setItemInMainHand(ItemUtil.cloneWithNewMaterial(item, type))
        }
    }
}
