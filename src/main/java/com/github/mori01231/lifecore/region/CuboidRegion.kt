package com.github.mori01231.lifecore.region

import kotlinx.serialization.Serializable
import org.bukkit.Location
import kotlin.math.abs

@Serializable
data class CuboidRegion(
    val pos1: WorldLocation?,
    val pos2: WorldLocation?,
) {
    companion object {
        fun CuboidRegion?.isIncomplete() = this == null || pos1 == null || pos2 == null

        fun CuboidRegion?.isComplete() = !isIncomplete()
    }

    init {
        if (pos1 != null && pos2 != null && pos1.world != pos2.world) {
            throw IllegalArgumentException("Worlds must be the same")
        }
    }

    val world = pos1?.world ?: pos2?.world

    val xLength: Long = if (isIncomplete()) {
        0
    } else {
        abs(pos1!!.x.toLong() - pos2!!.x) + 1
    }

    val yLength: Long = if (isIncomplete()) {
        0
    } else {
        abs(pos1!!.y.toLong() - pos2!!.y) + 1
    }

    val zLength: Long = if (isIncomplete()) {
        0
    } else {
        abs(pos1!!.z.toLong() - pos2!!.z) + 1
    }

    val size = xLength * yLength * zLength

    fun contains(location: WorldLocation): Boolean {
        if (pos1 == null) {
            error("This CuboidRegion is incomplete (pos1 is null)")
        }
        if (pos2 == null) {
            error("This CuboidRegion is incomplete (pos2 is null)")
        }
        if (location.world != world) {
            return false
        }
        return contains(location.x, location.y, location.z)
    }

    fun contains(location: Location): Boolean {
        if (pos1 == null) {
            error("This CuboidRegion is incomplete (pos1 is null)")
        }
        if (pos2 == null) {
            error("This CuboidRegion is incomplete (pos2 is null)")
        }
        if (location.world?.name != world) {
            return false
        }
        return contains(location.blockX, location.blockY, location.blockZ)
    }

    fun contains(x: Int, y: Int, z: Int): Boolean {
        if (pos1 == null) {
            error("This CuboidRegion is incomplete (pos1 is null)")
        }
        if (pos2 == null) {
            error("This CuboidRegion is incomplete (pos2 is null)")
        }

        val minX = minOf(pos1.x, pos2.x)
        val minY = minOf(pos1.y, pos2.y)
        val minZ = minOf(pos1.z, pos2.z)

        val maxX = maxOf(pos1.x, pos2.x)
        val maxY = maxOf(pos1.y, pos2.y)
        val maxZ = maxOf(pos1.z, pos2.z)

        return x in minX..maxX && y in minY..maxY && z in minZ..maxZ
    }
}
