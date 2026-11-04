package com.github.mori01231.lifecore.config

import com.github.mori01231.lifecore.util.TowniaUtil
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
data class TownConfig(
    @JvmField var allowInvisibility: Boolean = true,
    @JvmField var allowSit: Boolean = true,
    @JvmField var allowPetPickup: Boolean = true,
    @JvmField var allowPassenger: Boolean = true,
) {
    companion object {
        fun getTownConfigAt(lifeCoreConfig: LifeCoreConfig, location: Location): TownConfig? {
            val town = TowniaUtil.getTownAt(location) ?: return null
            val townName = TowniaUtil.getTownName(town) ?: return null
            return lifeCoreConfig.townConfig.computeIfAbsent(townName) { TownConfig() }
        }
    }
}
