package com.github.mori01231.lifecore.block

import com.github.mori01231.lifecore.LifeCore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.bukkit.plugin.java.JavaPlugin

@Serializable
data class CustomBlockState(
    val blockName: String,
    val tag: MutableMap<String, JsonElement> = mutableMapOf(),
) {
    constructor(block: CustomBlock, tag: MutableMap<String, JsonElement> = mutableMapOf()) :
            this(block.name, tag)

    init {
        getBlock()
    }

    fun getBlock() =
        JavaPlugin.getPlugin(LifeCore::class.java).customBlockManager.findBlockByName(blockName)
            ?: error("Block $blockName is not registered")
}
