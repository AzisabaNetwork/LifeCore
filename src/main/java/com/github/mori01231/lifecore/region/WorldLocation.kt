package com.github.mori01231.lifecore.region

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
data class WorldLocation(
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
) {
    constructor(location: Location) : this(
        location.world?.name ?: error("World is null"),
        location.blockX,
        location.blockY,
        location.blockZ,
    )

    fun toBukkitLocation() = Location(Bukkit.getWorld(world), x.toDouble(), y.toDouble(), z.toDouble())
}
