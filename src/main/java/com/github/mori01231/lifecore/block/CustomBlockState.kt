package com.github.mori01231.lifecore.block

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.AxisX
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.bukkit.plugin.java.JavaPlugin

@Serializable
data class CustomBlockState(
    val blockName: String,
    val axis: AxisX = AxisX.SOUTH,
    val tag: MutableMap<String, JsonElement> = mutableMapOf(),
) {
    constructor(block: CustomBlock, axis: AxisX = AxisX.SOUTH, tag: MutableMap<String, JsonElement> = mutableMapOf()) :
            this(block.name, axis, tag)

    init {
        getBlock()
    }

    fun getBlock() =
        JavaPlugin.getPlugin(LifeCore::class.java).customBlockManager.findBlockByName(blockName)
            ?: error("Block $blockName is not registered")
}
