package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class CreatureSpawnEventListener(private val plugin: LifeCore) : Listener {
    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if (event.entityType == EntityType.BEE) {
            // Remove the bee
            event.entity.remove()
        }
        if (event.entityType == EntityType.ENDERMAN &&
            plugin.config.getStringList("NonEndermanWorlds").contains(event.location.world.name)
        ) {
            // Remove the Enderman
            event.entity.remove()
        }
    }
}
