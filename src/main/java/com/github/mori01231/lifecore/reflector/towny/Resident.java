package com.github.mori01231.lifecore.reflector.towny;

import com.github.mori01231.lifecore.LifeCore;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.reflector.Reflector;

import java.util.Objects;

public interface Resident extends TownyObject {
    static @NotNull Resident getInstance(@NotNull Object instance) {
        Objects.requireNonNull(instance, "No static context allowed");
        return Objects.requireNonNull(Reflector.newReflector(LifeCore.class.getClassLoader(), Resident.class, "com.palmergames.bukkit.towny.object.Resident", instance));
    }
}
