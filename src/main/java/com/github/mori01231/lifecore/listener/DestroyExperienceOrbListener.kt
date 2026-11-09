package com.github.mori01231.lifecore.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent

class DestroyExperienceOrbListener : Listener {
    @EventHandler
    fun onChunkLoad(e: ChunkLoadEvent) {
        if (e.isNewChunk) return
        for (entity in e.chunk.entities) {
            if (entity.type == EntityType.EXPERIENCE_ORB) {
                entity.remove()
            }
        }
    }
}
