package com.github.mori01231.lifecore.block

import com.github.mori01231.lifecore.region.WorldLocation
import com.github.mori01231.lifecore.util.AxisX
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.text.contains

abstract class CustomBlock(
    val material: Material,
    private val displayName: String? = null,
    private val lore: List<String>? = null,
) : Listener {
    open val name: String = javaClass.simpleName
    open val lockFacing: Boolean = false
    open val axisShift: Int = 0
    open val backgroundBlock: Material = Material.BARRIER
    var customModelData: Int? = null

    internal fun handleInteract(e: PlayerInteractEvent, state: CustomBlockState) {
        if (state.blockName != name) error("Block name mismatch: state ${state.blockName} != block $name")
        //e.isCancelled = true // player wouldn't be able to place blocks on top of this block
        onInteract(e, state)
    }

    open fun onInteract(e: PlayerInteractEvent, state: CustomBlockState) {
    }

    open fun canDestroy(state: CustomBlockState, wrench: Boolean) = wrench

    /**
     * Executes the pre-destroy process, and returns whether the block can be dropped.
     * @return Whether the block can be dropped. `true` if the block can be dropped. `false` if the block drop will be cancelled.
     */
    open fun preDestroy(state: CustomBlockState): Boolean {
        return true
    }

    open fun onPlace(e: BlockPlaceEvent): CustomBlockState {
        val nms = CraftItemStack.asNMSCopy(e.itemInHand)
        val tagString = if (nms.hasTag() && nms.tag!!.contains("CustomBlockState")) {
            nms.tag!!.getCompound("CustomBlockState").getString("tag")
        } else {
            ""
        }
        if (tagString.isBlank()) {
            return CustomBlockState(this, AxisX.valueOf(e.player.facing.name))
        }
        val tag = Json.decodeFromString<MutableMap<String, JsonElement>>(tagString)
        return CustomBlockState(this, AxisX.valueOf(e.player.facing.name), tag)
    }

    open fun postPlace(e: BlockPlaceEvent, state: CustomBlockState) {
    }

    open fun getItemStack(state: CustomBlockState?): ItemStack {
        val item = ItemStack(material)
        item.itemMeta = item.itemMeta?.apply {
            setCustomModelData(this@CustomBlock.customModelData)
            if (this@CustomBlock.displayName != null) {
                displayName(Component.text(this@CustomBlock.displayName))
            }
            lore = this@CustomBlock.lore
        }
        val nms = CraftItemStack.asNMSCopy(item)
        nms.orCreateTag.put("CustomBlockState", CompoundTag().apply {
            putString("blockName", this@CustomBlock.name)
            if (state != null) {
                putString("tag", Json.encodeToString(state.tag))
            }
        })
        return CraftItemStack.asCraftMirror(nms)
    }

    open fun tick(manager: CustomBlockManager, pos: WorldLocation, state: CustomBlockState): CustomBlockState? = null
}
