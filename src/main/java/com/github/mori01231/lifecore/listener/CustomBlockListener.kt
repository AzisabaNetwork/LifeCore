package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class CustomBlockListener(val plugin: LifeCore) : Listener {
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.clickedBlock == null) return
        val wrench = plugin.customBlockManager.getWrenchItem().isSimilar(e.item) && e.player.isSneaking && e.action == Action.RIGHT_CLICK_BLOCK
        val state = plugin.customBlockManager.getState(e.clickedBlock!!.location) ?: return
        if (wrench) {
            e.isCancelled = true
            if (!e.player.hasPermission("lifecore.customblock.destroy.${state.blockName}")) {
                e.player.sendActionBar("このブロックを破壊する権限がありません。")
                return
            }
            plugin.customBlockManager.setState(e.clickedBlock!!.location, null)
            e.player.playSound(e.player.location, Sound.ENTITY_ITEM_PICKUP, 1f, 1f)
            e.player.inventory.addItem(state.getBlock().getItemStack(state)).forEach { (_, item) ->
                e.player.world.dropItem(e.player.location, item)
            }
        } else {
            if (!e.player.hasPermission("lifecore.customblock.interact.${state.blockName}")) {
                e.player.sendActionBar("このブロックを使用する権限がありません。")
                return
            }
            state.getBlock().handleInteract(e, state)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockPlace(e: BlockPlaceEvent) {
        val nms = CraftItemStack.asNMSCopy(e.itemInHand)
        if (!nms.hasTag()) {
            return
        }
        if (!nms.tag!!.hasKey("BlockState")) {
            return
        }
        val blockState = nms.tag!!.getCompound("BlockState")
        val blockName = blockState.getString("blockName")
        val block = plugin.customBlockManager.findBlockByName(blockName) ?: return
        if (!e.player.hasPermission("lifecore.customblock.place.$blockName")) {
            e.player.sendActionBar("このブロックを設置する権限がありません。")
            return
        }
        val state = block.onPlace(e)
        plugin.customBlockManager.setState(e.block.location, state)
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.player.gameMode != GameMode.CREATIVE) return
        val state = plugin.customBlockManager.getState(e.block.location) ?: return
        e.isCancelled = true
        if (!e.player.hasPermission("lifecore.customblock.destroy.${state.blockName}")) {
            e.player.sendActionBar("このブロックを破壊する権限がありません。")
            return
        }
        plugin.customBlockManager.setState(e.block.location, null)
    }

    @EventHandler
    fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
        val armorStand = plugin.customBlockManager.findArmorStand(e.rightClicked.location)
        if (armorStand == e.rightClicked) {
            e.isCancelled = true
        }
    }
}
