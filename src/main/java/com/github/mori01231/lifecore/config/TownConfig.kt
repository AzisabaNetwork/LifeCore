package com.github.mori01231.lifecore.config

import com.github.mori01231.lifecore.listener.TownyOutlawListener
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
            val town = TownyOutlawListener.getTownAt(location) ?: return null
            val townName = TownyOutlawListener.getNameTownyObject(town)
            return lifeCoreConfig.townConfig.computeIfAbsent(townName) { TownConfig() }
        }
    }
}
