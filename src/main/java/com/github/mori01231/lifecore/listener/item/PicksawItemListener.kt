package com.github.mori01231.lifecore.listener.item

import com.github.mori01231.lifecore.data.DataLoader
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PicksawItemListener(private val dataLoader: DataLoader) : Listener {
    companion object {
        const val ITEM_ID = "2dd646f1-70a1-4613-92c3-431de7c0126f"
        val ALLOWED_WORLDS = listOf("art", "lifetownart")
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND) return
        if (!ALLOWED_WORLDS.contains(e.player.world.name.lowercase()) && !e.player.hasPermission("lifecore.picksaw")) return
        val item = e.player.inventory.itemInMainHand
        if (ItemUtil.getStringTag(item, "LifeItemId") != ITEM_ID) return
        val hasBlockInOffhand = e.player.inventory.itemInOffHand.type.let { !it.isAir && it.isBlock }
        if (e.action == Action.LEFT_CLICK_BLOCK) {
            val clickedBlock = e.clickedBlock ?: return
            val blockType = clickedBlock.type
            val minecraftName = blockType.key.toString()
            val targetMaterial = when {
                Tag.MINEABLE_AXE.isTagged(blockType) || dataLoader.findTag("minecraft:mineable/axe")?.resolve()?.contains(minecraftName) == true ->
                    Material.DIAMOND_AXE
                Tag.MINEABLE_PICKAXE.isTagged(blockType) || dataLoader.findTag("minecraft:mineable/pickaxe")?.resolve()?.contains(minecraftName) == true ->
                    Material.DIAMOND_PICKAXE
                Tag.MINEABLE_SHOVEL.isTagged(blockType) || dataLoader.findTag("minecraft:mineable/shovel")?.resolve()?.contains(minecraftName) == true ->
                    Material.DIAMOND_SHOVEL
                Tag.MINEABLE_HOE.isTagged(blockType) || dataLoader.findTag("minecraft:mineable/hoe")?.resolve()?.contains(minecraftName) == true ->
                    Material.DIAMOND_HOE
                Tag.WOOL.isTagged(blockType) || dataLoader.findTag("minecraft:wool")?.resolve()?.contains(minecraftName) == true ->
                    Material.SHEARS
                else -> null
            }
            if (targetMaterial != null && item.type != targetMaterial) {
                e.player.inventory.setItemInMainHand(ItemUtil.cloneWithNewMaterial(item, targetMaterial))
            }
        } else if (e.action == Action.RIGHT_CLICK_BLOCK && hasBlockInOffhand) {
            if (item.type != Material.DIAMOND_PICKAXE) {
                e.player.inventory.setItemInMainHand(ItemUtil.cloneWithNewMaterial(item, Material.DIAMOND_PICKAXE))
            }
        }
    }
}

