package com.github.mori01231.lifecore.reflector.towny;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.reflector.CastTo;

public interface TownBlock extends TownyObject {
    /**
     * Get the town at this block. The caller must check {@link #hasTown()} before calling this method as this method
     * will throw exception if the town does not exist at this block.
     * @return the town
     */
    @CastTo(Town.class)
    @NotNull Town getTown();

    boolean hasTown();
}
