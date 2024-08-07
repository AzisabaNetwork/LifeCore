package com.github.mori01231.lifecore.block

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.listener.CustomBlockListener
import com.github.mori01231.lifecore.util.AxisX
import com.github.mori01231.lifecore.util.LRUCache
import kotlinx.serialization.json.Json
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Leaves
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import java.io.File
import java.util.*

class CustomBlockManager(val plugin: LifeCore) {
    companion object {
        val regionDir = File("plugins/LifeCore/custom_block/region")

        init {
            regionDir.mkdirs()
        }
    }

    private val blocks = mutableListOf<CustomBlock>()
    private val region = mutableMapOf<String, LRUCache<Pair<Int, Int>, CustomBlockRegion>>()

    fun getBlocks() = blocks.toList()

    fun registerCustomBlock(block: CustomBlock) {
        Objects.requireNonNull(block)
        plugin.config.getInt("custom-model-data." + block.name, 0).let {
            if (it != 0) {
                block.customModelData = it
            }
        }
        plugin.logger.info("Registering custom block: ${block.name} (${block.javaClass.typeName})")
        blocks += block
        plugin.server.pluginManager.registerEvents(block, plugin)
    }

    fun findBlockByName(name: String) = blocks.firstOrNull { it.name == name }

    private fun getRegionPos(worldPos: Int) = worldPos shr 9

    fun getState(location: Location): CustomBlockState? {
        val region = loadRegion(location.world, getRegionPos(location.blockX), getRegionPos(location.blockZ))
        return region.getState(location.blockX, location.blockY, location.blockZ)
    }

    fun setState(location: Location, state: CustomBlockState?) {
        val region = loadRegion(location.world, getRegionPos(location.blockX), getRegionPos(location.blockZ))
        if (state == null) {
            findArmorStand(location)?.remove()
            location.block.type = Material.AIR
        } else {
            spawnBlock(location, state.getBlock(), state.axis)
        }
        region.setState(location.blockX, location.blockY, location.blockZ, state)
    }

    private fun loadRegion(world: World, x: Int, z: Int): CustomBlockRegion {
        val wld = region.getOrPut(world.name) { LRUCache(100) }
        val loaded = wld.getOrPut(x to z) {
            val file = File(regionDir, "${world.name}/$x.$z.json")
            if (file.exists()) {
                plugin.logger.info("Loading region $x, $z")
                Json.decodeFromString(CustomBlockRegion.serializer(), file.readText())
            } else {
                plugin.logger.info("Creating region $x, $z")
                CustomBlockRegion(world.name, x, z)
            }
        }
        if (loaded.dirty) {
            plugin.logger.warning("Region $x, $z was not saved properly")
            loaded.save()
        }
        return loaded
    }

    fun getWrenchItem() = ItemStack(Material.STICK).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName("${ChatColor.GOLD}レンチ")
            lore = listOf("${ChatColor.GRAY}シフト+右クリックで特殊ブロックを撤去できます。")
            setCustomModelData(plugin.customModelDataWrench)
        }
    }

    fun init() {
        plugin.server.pluginManager.registerEvents(CustomBlockListener(plugin), plugin)

        // add recipe
        try {
            plugin.server.addRecipe(ShapedRecipe(NamespacedKey(plugin, "wrench"), getWrenchItem()).apply {
                shape("I I", " I ", " I ")
                setIngredient('I', Material.IRON_INGOT)
            })
        } catch (_: Exception) {}

        reloadBlocks()
    }

    @Suppress("UNCHECKED_CAST")
    fun reloadBlocks() {
        plugin.reloadConfig()
        blocks.forEach { block ->
            HandlerList.unregisterAll(block)
        }
        blocks.clear()

        // add blocks
        plugin.config.getMapList("custom-blocks").forEach { map ->
            val blockName = map["name"]?.toString() ?: return plugin.logger.warning("name is required")
            val axisShift = map["axisShift"]?.toString()?.toIntOrNull() ?: 0
            val lockFacing = map["lockFacing"]?.toString()?.toBoolean() ?: false
            val backgroundBlock = Material.valueOf(map["backgroundBlock"]?.toString()?.uppercase() ?: "BARRIER")
            val material = Material.valueOf(map["material"]?.toString()?.uppercase() ?: return plugin.logger.warning("material is required"))
            val displayName = map["displayName"]?.toString()?.let { ChatColor.translateAlternateColorCodes('&', it) }
            val lore = map["lore"]?.toString()?.let { ChatColor.translateAlternateColorCodes('&', it) }?.split("\n")
            val customModelData = map["customModelData"]?.toString()?.toIntOrNull() ?: 0
            val commands = map["commands"] as List<String>?
            val consoleCommands = map["consoleCommands"] as List<String>?
            val destroyWithoutWrench = map["destroyWithoutWrench"]?.toString()?.toBoolean() ?: false
            val block = CommandCustomBlock(blockName, lockFacing, axisShift, backgroundBlock, material, displayName, lore, commands ?: emptyList(), consoleCommands ?: emptyList(), destroyWithoutWrench)
            block.customModelData = customModelData
            registerCustomBlock(block)
        }
    }

    fun saveAll() {
        region.values.forEach { it.values.forEach { r -> r.save() } }
    }

    fun findArmorStand(location: Location): ArmorStand? {
        val x = location.blockX + 0.5
        val y = location.blockY.toDouble()
        val z = location.blockZ + 0.5
        return location.world.getNearbyEntities(Location(location.world, x, y, z), 0.01, 0.01, 0.01)
            .filterIsInstance<ArmorStand>()
            .firstOrNull { it.customName == "custom_block" && !it.isVisible && it.isInvulnerable && it.isSmall }
    }

    private fun spawnBlock(location: Location, block: CustomBlock, axis: AxisX): ArmorStand {
        findArmorStand(location)?.remove()
        location.block.type = block.backgroundBlock
        location.block.blockData.let {
            if (!block.lockFacing && it is Directional && axis.blockFace in it.faces) {
                it.facing = axis.blockFace
                location.block.blockData = it
            }
            if (it is Leaves) {
                it.isPersistent = true
                location.block.blockData = it
            }
        }
        val x = location.blockX + 0.5
        val y = location.blockY.toDouble()
        val z = location.blockZ + 0.5
        return location.world.spawn(Location(location.world, x, y, z), ArmorStand::class.java) {
            it.setGravity(false)
            it.isVisible = false
            it.isInvulnerable = true
            it.isSmall = true
            it.customName = "custom_block"
            it.isCustomNameVisible = false
            it.isSilent = true
            if (block.lockFacing) {
                it.setRotation(block.axisShift.toFloat(), 0f)
            } else {
                it.setRotation(((axis.yaw + block.axisShift) % 360).toFloat(), 0f)
            }
            it.equipment?.helmet = ItemStack(block.material).apply {
                itemMeta = itemMeta?.apply {
                    setCustomModelData(block.customModelData)
                    setDisplayName(" ")
                }
            }
        }
    }

    fun isCustomBlockEntity(e: Entity?) =
        e is ArmorStand && e.customName == "custom_block" && !e.isVisible && e.isInvulnerable && e.isSmall
}
