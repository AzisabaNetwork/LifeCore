package com.github.mori01231.lifecore.region

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.UUID

class PlayerRegionManager {
    companion object {
        val wandItem = ItemStack(Material.GOLDEN_AXE).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("${ChatColor.GOLD}範囲指定斧 ${ChatColor.DARK_GRAY}(LifeCore)")
            }
        }
    }

    private val playerRegionMap = mutableMapOf<UUID, CuboidRegion>()

    operator fun get(uuid: UUID): CuboidRegion? = playerRegionMap[uuid]

    operator fun set(uuid: UUID, region: CuboidRegion) {
        playerRegionMap[uuid] = region
    }

    fun put(uuid: UUID, pos1: WorldLocation? = null, pos2: WorldLocation? = null) {
        if (pos1 != null && pos2 != null && pos1.world != pos2.world) {
            throw IllegalArgumentException("Worlds must be the same")
        }
        val current = get(uuid)
        if (pos1 != null) {
            if (current?.pos2 != null && current.pos2.world != pos1.world) {
                playerRegionMap[uuid] = CuboidRegion(pos1, pos2)
            } else {
                playerRegionMap[uuid] = current?.copy(pos1 = pos1) ?: CuboidRegion(pos1, pos2)
            }
        }
        if (pos2 != null) {
            if (current?.pos1 != null && current.pos1.world != pos2.world) {
                playerRegionMap[uuid] = CuboidRegion(pos1, pos2)
            } else {
                playerRegionMap[uuid] = current?.copy(pos2 = pos2) ?: CuboidRegion(pos1, pos2)
            }
        }
    }
}
