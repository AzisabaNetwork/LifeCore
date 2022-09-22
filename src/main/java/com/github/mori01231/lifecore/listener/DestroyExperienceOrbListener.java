package com.github.mori01231.lifecore.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class DestroyExperienceOrbListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (e.isNewChunk()) {
            return;
        }
        for (Entity entity : e.getChunk().getEntities()) {
            if (entity.getType() == EntityType.EXPERIENCE_ORB) {
                entity.remove();
            }
        }
    }
}
