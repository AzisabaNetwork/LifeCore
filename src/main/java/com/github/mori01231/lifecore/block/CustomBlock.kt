package com.github.mori01231.lifecore.block

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

abstract class CustomBlock(
    val material: Material,
    private val displayName: String? = null,
    private val lore: List<String>? = null,
) : Listener {
    open val name: String = javaClass.simpleName
    var customModelData = 0

    internal fun handleInteract(e: PlayerInteractEvent, state: CustomBlockState) {
        //e.isCancelled = true
        e.setUseItemInHand(Event.Result.ALLOW)
        e.setUseInteractedBlock(Event.Result.ALLOW)
        if (state.blockName != name) {
            return
        }
        onInteract(e, state)
    }

    open fun onInteract(e: PlayerInteractEvent, state: CustomBlockState) {
    }

    open fun canDestroy(state: CustomBlockState) = true

    open fun onPlace(e: BlockPlaceEvent): CustomBlockState {
        val nms = CraftItemStack.asNMSCopy(e.itemInHand)
        val tagString = if (nms.hasTag() && nms.tag!!.hasKey("BlockState")) {
            nms.tag!!.getCompound("BlockState").getString("tag")
        } else {
            ""
        }
        if (tagString.isBlank()) {
            return CustomBlockState(this)
        }
        val tag = Json.decodeFromString<MutableMap<String, JsonElement>>(tagString)
        return CustomBlockState(this, tag)
    }

    open fun getItemStack(state: CustomBlockState?): ItemStack {
        val item = ItemStack(material)
        item.itemMeta = item.itemMeta?.apply {
            setCustomModelData(this@CustomBlock.customModelData)
            setDisplayName(this@CustomBlock.displayName)
            lore = this@CustomBlock.lore
        }
        val nms = CraftItemStack.asNMSCopy(item)
        nms.orCreateTag.set("BlockState", NBTTagCompound().apply {
            setString("blockName", this@CustomBlock.name)
            if (state != null) {
                setString("tag", Json.encodeToString(state.tag))
            }
        })
        return CraftItemStack.asCraftMirror(nms)
    }
}
