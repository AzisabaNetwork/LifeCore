package com.github.mori01231.lifecore.util;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum AxisX {
    SOUTH(270),
    WEST(0),
    NORTH(90),
    EAST(180);

    private final int yaw;

    AxisX(int yaw) {
        this.yaw = yaw;
    }

    public int getYaw() {
        return yaw;
    }

    @Contract(pure = true)
    public @NotNull BlockFace getBlockFace() {
        switch (this) {
            case SOUTH:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.WEST;
            case NORTH:
                return BlockFace.NORTH;
            case EAST:
                return BlockFace.EAST;
            default:
                throw new AssertionError();
        }
    }
}
