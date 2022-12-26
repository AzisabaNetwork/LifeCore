package com.github.mori01231.lifecore.reflector.towny;

import com.github.mori01231.lifecore.LifeCore;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.reflector.CastTo;
import xyz.acrylicstyle.util.reflector.Reflector;
import xyz.acrylicstyle.util.reflector.Static;

import java.util.Objects;

public interface TownyAPI {
    static @NotNull TownyAPI getInstance(@Nullable Object instance) {
        return Objects.requireNonNull(Reflector.newReflector(LifeCore.class.getClassLoader(), TownyAPI.class, "com.palmergames.bukkit.towny.TownyAPI", instance));
    }

    @CastTo(TownyAPI.class)
    @Static
    @NotNull TownyAPI getInstance();

    @CastTo(TownBlock.class)
    @Nullable TownBlock getTownBlock(@NotNull Location location);
}
