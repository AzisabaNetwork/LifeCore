package com.github.mori01231.lifecore.util

import org.bukkit.block.BlockFace

enum class AxisX(val yaw: Int) {
    SOUTH(270),
    WEST(0),
    NORTH(90),
    EAST(180);

    val blockFace: BlockFace
        get() = when (this) {
            SOUTH -> BlockFace.SOUTH
            WEST -> BlockFace.WEST
            NORTH -> BlockFace.NORTH
            EAST -> BlockFace.EAST
        }
}
